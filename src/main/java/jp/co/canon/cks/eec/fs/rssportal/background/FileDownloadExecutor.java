package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnector;
import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc.FileServiceProc;
import jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc.RemoteFileServiceProc;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class FileDownloadExecutor {

    private final Log log;

    private enum Status {
        idle, init, download, compress, complete, stop, error
    }

    private static int mUniqueKey = 1;
    private final String jobType;
    private final String desc;
    private final String downloadId;
    private Status status;
    private final List<DownloadForm> downloadForms;
    private final List<FileDownloadContext> downloadContexts;
    private final String baseDir;

    private final FileDownloader downloader;
    private final FileServiceManageConnector connector;
    private DownloadMonitor monitor;
    private int totalFiles = -1;
    private String mPath = null;
    private long lastUpdate;

    private boolean attrCompression;
    private boolean attrEmptyAllPathBeforeDownload;
    private boolean attrReplaceFileForSameFileName;
    private boolean attrDownloadFilesViaMultiSessions;

    public FileDownloadExecutor(
            @NonNull final String jobType,
            @Nullable final String desc,
            @NonNull final FileDownloader downloader,
            @NonNull final List<DownloadForm> request,
            boolean compress) {

        if(desc==null) {
            log = LogFactory.getLog(getClass());
        } else {
            String fmt = "%s:%s";
            log = LogFactory.getLog(String.format(fmt, getClass().toString(), desc));
        }

        this.jobType = jobType;
        this.downloader = downloader;
        this.connector = downloader.getConnector();

        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        downloadId = "DL"+(mUniqueKey++)+stamp.getTime();
        downloadForms = request;
        downloadContexts = new ArrayList<>();
        baseDir = Paths.get(downloader.getDownloadCacheDir(), downloadId).toString();

        this.desc = desc==null?"noname":desc;
        this.attrCompression = compress;
        this.attrEmptyAllPathBeforeDownload = true;
        this.attrReplaceFileForSameFileName = false;
        this.attrDownloadFilesViaMultiSessions = false;
        setStatus(Status.idle);
    }

    private void initialize() {
        setStatus(Status.init);
        log.info(downloadId+": initialize()");
        for(DownloadForm form: downloadForms) {
            if(form.getFiles().size()==0)
                continue;
            FileDownloadContext context = new FileDownloadContext(jobType, downloadId, form, baseDir);
            context.setConnector(connector);
            downloadContexts.add(context);
        }
        totalFiles = 0;
        downloadContexts.forEach(context -> totalFiles +=context.getFileCount());
    }

    private void compress() {
        log.info(downloadId+": compress()");
        if(status==Status.error || status==Status.stop)
            return;
        setStatus(Status.compress);

        Compressor comp = new Compressor();
        String fileName = String.format("%d.zip", System.currentTimeMillis());
        String zipDir = Paths.get(downloader.getDownloadResultDir(), downloadId, fileName).toString();
        if(comp.compress(baseDir, zipDir)) {
            mPath = zipDir;
        }
    }

    private void wrapup() {
        log.info(downloadId+": wrapup()");
        setStatus(Status.complete);
    }

    private void cleanup() {
        log.info(downloadId+": cleanup()");
    }

    private class DownloadRunner implements Runnable {

        private Log log;
        private AtomicInteger runnings;
        private final int maxThreads = 16;

        public DownloadRunner(Log log) {
            this.log = log;
            runnings = new AtomicInteger(0);
        }

        private Consumer<FileServiceProc> finishCallback = proc->{
            log.info(proc.getName()+" finished");
            runnings.getAndDecrement();
        };

        private Consumer<FileServiceProc> errorCallback = proc->{
            log.info(proc.getName()+" error");
            setStatus(Status.error);
        };

        @Override
        public void run() {
            try {
                initialize();
                if(status==Status.stop)
                    return;

                setStatus(Status.download);
                List<FileServiceProc> procs = new ArrayList<>();

                for(int i=0; i<downloadContexts.size();) {
                    FileDownloadContext context = downloadContexts.get(i);

                    if(status==Status.stop || status==Status.error)
                        break;

                    while(runnings.get()>=maxThreads) {
                        Thread.sleep(500);
                    }

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
                    proc.setNotifyError(errorCallback);
                    proc.setNotifyFinish(finishCallback);
                    proc.start();
                    procs.add(proc);
                    runnings.getAndIncrement();
                    log.info(proc.getName()+" starts");
                    ++i;
                }

                while(runnings.get()>0) {
                    Thread.sleep(500);
                    if(status==Status.stop || status==Status.error)
                        return;
                }

                if(attrCompression)
                    compress();

                wrapup();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setStatus(Status status) {
        this.status = status;
        lastUpdate = System.currentTimeMillis();
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

    public String getId() {
        return downloadId;
    }

    public void start() {
        log.info("file download start ("+ downloadForms.size()+")");
        printExecutorInfo();
//        (new Thread(runner)).start();
        (new Thread(new DownloadRunner(log))).start();
    }

    public void stop() {
        log.info("stop downloading");
        setStatus(Status.stop);
    }

    public boolean isRunning() {
        return (status==Status.complete || status==Status.error || status==Status.stop)?false:true;
    }

    public String getStatus() {
        return status.toString();
    }


    public String getBaseDir() {
        return baseDir;
    }

    public String getDownloadPath() {
        return mPath;
    }

    public long getDownloadFiles() {
        AtomicLong files = new AtomicLong();
        downloadContexts.forEach(context->files.addAndGet(context.getDownloadFiles()));
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

    public long getLastUpdate() {
        return lastUpdate;
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
