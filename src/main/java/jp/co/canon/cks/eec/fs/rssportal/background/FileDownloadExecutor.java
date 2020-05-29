package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc.FileServiceProc;
import jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc.RemoteFileServiceProc;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.model.FileInfo;
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
        idle, init, download, compress, complete, stop, error
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
    private boolean stop = false;

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
        mService = serviceModel;

        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        downloadId = "DL"+(mUniqueKey++)+stamp.getTime();
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
        log.info(downloadId+": compress()");

        if(status==Status.error || status==Status.stop)
            return;
        status = Status.compress;

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

    private void cleanup() {
        log.info(downloadId+": cleanup()");

    }

    private Runnable runner = () -> {
        try {
            initialize();
            if(status==Status.stop)
                return;

            status = Status.download;
            List<FileServiceProc> procs = new ArrayList<>();
            for(FileDownloadContext context: downloadContexts) {
                if(status==Status.stop || status==Status.error)
                    break;
                try {
                    FileServiceProc proc = null;
                    switch (context.getJobType()) {
                        case "manual":
                        case "auto":
                            proc = new RemoteFileServiceProc(context);
                            break;
                        default:
                            throw new IllegalArgumentException("invalid jobType");
                    }
                    proc.setMonitor(monitor);
                    proc.start();
                    procs.add(proc);

                    if(attrDownloadFilesViaMultiSessions==false) {
                        while (proc.getCompleted()>0)
                            Thread.sleep(100);
                        // check error
                        if(proc.getCompleted()<0) {
                            status = Status.error;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for(FileServiceProc proc:procs)
                while(proc.getCompleted()>0)
                    Thread.sleep(100);

            if(status==Status.stop || status==Status.error)
                return;

            if(attrCompression)
                compress();

            wrapup();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    public String getId() {
        return downloadId;
    }

    public void start() {
        log.info("file download start ("+ downloadForms.size()+")");
        printExecutorInfo();
        (new Thread(runner)).start();
    }

    public void stop() {
        log.info("stop downloading");
        status = Status.stop;
    }

    public boolean isRunning() {
        return (status==Status.complete || status==Status.error || status==Status.stop)?false:true;
    }

    public String getStatus() {
        return status.toString();
    }

    private void printExecutorInfo() {

        log.info("FileDownloadExecutor(desc="+desc+", id="+downloadId+")");
        log.info("attr."+attrCompression);
        log.info("    .DownloadFilesViaMultiSessions="+attrDownloadFilesViaMultiSessions);
        log.info("    .EmptyAllPathBeforeDownload"+attrEmptyAllPathBeforeDownload);
        log.info("    .ReplaceFileForSameFileName"+attrReplaceFileForSameFileName);
        log.info("path.base="+baseDir);
        log.info("download");
        for(DownloadForm form: downloadForms) {
            log.info("    " + form.getTool() + " / " + form.getLogType() + " (" + form.getFiles().size() + " files)");
            /*for(FileInfo f:form.getFiles()) {
                log.info("      - "+f.getName()+" "+f.getDate()+" "+f.getSize());
            }*/
        }

    }

    /*public List<String> getFileList() {
        List<String> list = new ArrayList<>();
        for(DownloadForm form: downloadForms) {
            form.getFiles().forEach(fileInfo -> {
                list.add(String.format(file_format, form.getTool(), form.getLogType(), fileInfo.getName()));
            });
        }
        return list;
    }*/

    public String getBaseDir() {
        return baseDir;
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

    public List<String> getFabs() {
        List<String> fabs = new ArrayList<>();
        for(DownloadForm form: downloadForms) {
            String fab = form.getFab();
            if(!fabs.contains(fab))
                fabs.add(fab);
        }
        return fabs;
    }

    /* Attributes */
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
