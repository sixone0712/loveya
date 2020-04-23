package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceUsedSOAP;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FileDownloadExecutor implements DownloadConfig {

    private final Log log;
    private static final String file_format = "%s/%s/%s";

    private enum Status {
        idle, init, download, compress, complete, error
    };

    private static int mUniqueKey = 1;
    private String jobType;
    private String desc;
    private String downloadId;
    private Status status;
    private List<DownloadForm> downloadForms;
    private List<FileDownloadContext> downloadContexts;
    private String baseDir;

    private FileServiceManage mServiceManager;
    private FileServiceModel mService;
    private DownloadMonitor monitor;
    private int totalFiles = -1;
    private String mPath = null;

    private boolean attrCompression;
    private boolean attrEmptyAllPathBeforeDownload;
    private boolean attrReplaceFileForSameFileName;
    private boolean attrDownloadFilesViaMultiSessions;

    public FileDownloadExecutor(
            @NonNull final String jobType,
            @Nullable final String desc,
            @NonNull final FileServiceManage serviceManager,
            @NonNull final FileServiceModel serviceModel,
            @NonNull final List<DownloadForm> request,
            boolean compress) {

        if(desc==null) {
            log = LogFactory.getLog(getClass());
        } else {
            String fmt = "%s:%s";
            log = LogFactory.getLog(String.format(fmt, getClass().toString(), desc));
        }

        this.jobType = jobType;
        status = Status.idle;
        mServiceManager = serviceManager;
        if(false) {
            mService = new FileServiceUsedSOAP(DownloadConfig.FCS_SERVER_ADDR);
        } else {
            mService = serviceModel;
        }

        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        downloadId = "DL"+(mUniqueKey++)+String.valueOf(stamp.getTime());
        downloadForms = request;
        downloadContexts = new ArrayList<>();
        baseDir = Paths.get(DownloadConfig.ROOT_PATH, downloadId).toString();

        this.desc = desc==null?"noname":desc;
        this.attrCompression = compress;
        this.attrEmptyAllPathBeforeDownload = true;
        this.attrReplaceFileForSameFileName = false;
        this.attrDownloadFilesViaMultiSessions = false;
    }

    private void initialize() {
        status = Status.init;
        log.info(downloadId+": initialize()");
        for(DownloadForm form: downloadForms) {
            FileDownloadContext context = new FileDownloadContext(jobType, downloadId, form);
            context.setFileManager(mServiceManager);
            context.setFileService(mService);
            downloadContexts.add(context);
        }
        totalFiles = 0;
        downloadContexts.forEach(context -> totalFiles +=context.getFileCount());
    }

    private void compress() {
        if(status==Status.error) {
            return;
        }
        status = Status.compress;
        log.info(downloadId+": compress()");
        Compressor comp = new Compressor();
        String zipDir = Paths.get(DownloadConfig.ZIP_PATH, downloadId, "test.zip").toString();
        if(comp.compress(baseDir, zipDir)) {
            mPath = zipDir;
        }
    }

    private void wrapup() {
        log.info(downloadId+": wrapup()");
        status = Status.complete;
    }

    private Runnable runner = () -> {
        try {

            initialize();
            List<FileServiceProc> procs = new ArrayList<>();
            status = Status.download;
            downloadContexts.forEach(context->{
                try {
                    FileServiceProc proc = null;
                    switch (context.getJobType()) {
                        case "manual":
                        case "auto":
                            proc = new RemoteFileServiceProc(context);
                            break;
                        case "virtual":
                            proc = new VirtualFileServiceProc(context);
                            break;
                        default:
                            throw new IllegalArgumentException("invalid jobType");
                    }
                    proc.setMonitor(monitor);
                    proc.start();
                    procs.add(proc);

                    if(attrDownloadFilesViaMultiSessions==false)
                        while(proc.isCompleted()==false)
                            Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            for(FileServiceProc proc:procs)
                while(proc.isCompleted()==false)
                    Thread.sleep(100);

            if(attrCompression)
                compress();

            wrapup();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    private void printExecutorInfo() {

        log.info("FileDownloadExecutor(desc="+desc+", id="+downloadId+")");
        log.info("attr."+attrCompression);
        log.info("    .DownloadFilesViaMultiSessions="+attrDownloadFilesViaMultiSessions);
        log.info("    .EmptyAllPathBeforeDownload"+attrEmptyAllPathBeforeDownload);
        log.info("    .ReplaceFileForSameFileName"+attrReplaceFileForSameFileName);
        log.info("path.base="+baseDir);
        log.info("download");
        for(DownloadForm form: downloadForms)
            log.info("    "+form.getTool()+" / "+form.getLogType()+" ("+form.getFiles().size()+" files)");
    }

    public String getId() {
        return downloadId;
    }

    public void start() {
        log.warn("file download start ("+ downloadForms.size()+")");
        printExecutorInfo();
        (new Thread(runner)).start();
    }

    public void stop() {
        // TBD
    }

    public boolean isRunning() {
        return (status==Status.complete || status==Status.error)?false:true;
    }

    public String getStatus() {
        return status.toString();
    }

    public List<String> getFileList() {
        List<String> list = new ArrayList<>();
        for(DownloadForm form: downloadForms) {
            form.getFiles().forEach(fileInfo -> {
                list.add(String.format(file_format, form.getTool(), form.getLogType(), fileInfo.getName()));
            });
        }
        return list;
    }

    public String getDownloadPath() {
        return mPath;
    }

    public int getDownloadFiles() {
        AtomicInteger files = new AtomicInteger();
        downloadContexts.forEach(context-> files.addAndGet(context.getDownloadFiles()));
        return files.get();
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setMonitor(@NonNull DownloadMonitor monitor) {
        this.monitor = monitor;
    }

    public boolean isAttrCompression() {
        return attrCompression;
    }

    public void setAttrCompression(boolean attrCompression) {
        this.attrCompression = attrCompression;
    }

    public boolean isAttrEmptyAllPathBeforeDownload() {
        return attrEmptyAllPathBeforeDownload;
    }

    public void setAttrEmptyAllPathBeforeDownload(boolean attrEmptyAllPathBeforeDownload) {
        this.attrEmptyAllPathBeforeDownload = attrEmptyAllPathBeforeDownload;
    }

    public boolean isAttrReplaceFileForSameFileName() {
        return attrReplaceFileForSameFileName;
    }

    public void setAttrReplaceFileForSameFileName(boolean attrReplaceFileForSameFileName) {
        this.attrReplaceFileForSameFileName = attrReplaceFileForSameFileName;
    }

    public boolean isAttrDownloadFilesViaMultiSessions() {
        return attrDownloadFilesViaMultiSessions;
    }

    public void setAttrDownloadFilesViaMultiSessions(boolean attrDownloadFilesViaMultiSessions) {
        this.attrDownloadFilesViaMultiSessions = attrDownloadFilesViaMultiSessions;
    }
}
