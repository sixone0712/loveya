package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpCompatDownloadRequest;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnector;
import jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.File;
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
    private final String ftpType;
    private final String desc;
    private final String downloadId;
    private Status status;
    private final List<DownloadRequestForm> downloadForms;
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
            @NonNull final String ftpType,
            @Nullable final String desc,
            @NonNull final FileDownloader downloader,
            @NonNull final List<DownloadRequestForm> request,
            boolean compress) {

        if(desc==null) {
            log = LogFactory.getLog(getClass());
        } else {
            String fmt = "%s:%s";
            log = LogFactory.getLog(String.format(fmt, getClass().toString(), desc));
        }

        this.ftpType = ftpType;
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
        for (DownloadRequestForm f : downloadForms) {
            if(f instanceof FtpDownloadRequestForm) {
                FtpDownloadRequestForm form = (FtpDownloadRequestForm)f;
                if (form.getFtpType().equals("ftp") && form.getFiles().size() == 0)
                    continue;
            }

            FileDownloadContext context = new FileDownloadContext(ftpType, downloadId, f, baseDir);
            context.setConnector(connector);
            switch (ftpType) {
                default :
                    log.error("undefined ftp-type  "+ftpType);
                    setStatus(Status.error);
                    return;
                case "ftp":
                case "vftp_sss":
                    context.setAchieve(true);
                    context.setAchieveDecompress(true);
                    break;
                case "vftp_compat":
                    context.setAchieve(false);
                    context.setAchieveDecompress(false);
                    break;
            }
            downloadContexts.add(context);
        }
        totalFiles = 0;
        downloadContexts.forEach(context -> totalFiles += context.getFileCount());
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

        // When the executor didn't compress the downloads then the result path is null.
        // Because wrapup() means this download has finished successfully.
        // Therefore we have to fill the download path information below.
        if(mPath==null) {
            if(downloadContexts.size()==1) {
                FileDownloadContext context = downloadContexts.get(0);
                File destination = new File(context.getLocalFilePath());
                if(!destination.exists()) {
                    log.error("destination doesn't exist  "+destination.toString());
                    setStatus(Status.error);
                    return;
                }
                if(destination.isDirectory()) {
                    File[] files = destination.listFiles();
                    if(files.length!=1 || (files.length>0 && files[0].isDirectory())) {
                        log.error("cannot specify destination");
                        setStatus(Status.error);
                        return;
                    } else {
                        mPath = files[0].toString();
                    }
                } else {
                    mPath = destination.toString();
                }
            } else {
                log.error("download config error");
                setStatus(Status.error);
                return;
            }
        }
        log.info("output destination="+mPath);
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

                if(true) {
                    List<FileDownloadServiceProc> procs = new ArrayList<>();

                    for(FileDownloadContext context: downloadContexts) {
                        if (status == Status.stop || status == Status.error)
                            break;
                        FileDownloadHandler handler;
                        switch(ftpType) {
                            default:
                            case "ftp":
                                handler = new FtpFileDownloadHandler(connector, context.getTool(), context.getLogType(), context.getFileNames());
                                break;
                            case "vftp_compat":
                                handler = new VFtpCompatFileDownloadHandler(connector, context.getTool(), context.getCommand());
                                break;
                            case "vftp_sss":
                                handler = new VFtpSssFileDownloadHandler(connector, context.getTool(), context.getDirectory(), context.getFileNames());
                                break;

                        }
                        procs.add(new FileDownloadServiceProc(handler, context, process->{
                            if(process.getStatus()==FileDownloadServiceProc.Status.Error) {
                                status = Status.error;
                                log.error("download error occurs");
                            }}));
                    }

                    thread_wait:
                    for (FileDownloadServiceProc proc : procs) {
                        while(true) {
                            if (status == Status.stop || status == Status.error) {
                                log.info("stop waiting threads");
                                break thread_wait;
                            }
                            if (proc.getStatus() == FileDownloadServiceProc.Status.InProgress) {
                                Thread.sleep(100);
                                continue;
                            } else if(proc.getStatus()==FileDownloadServiceProc.Status.Finished) {
                                continue thread_wait;
                            }
                        }
                    }

                    log.info("threads terminate");
                    for (FileDownloadServiceProc proc : procs) {
                        proc.interrupt();
                        proc.join();
                    }
                } else {
                    List<FileServiceProc> procs = new ArrayList<>();

                    for (int i = 0; i < downloadContexts.size(); ) {
                        FileDownloadContext context = downloadContexts.get(i);
                        if (status == Status.stop || status == Status.error)
                            break;

                        while (runnings.get() >= maxThreads) {
                            Thread.sleep(500);
                        }

                        FileServiceProc proc = null;
                        switch (context.getFtpType()) {
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
                        log.info(proc.getName() + " starts");
                        ++i;
                    }

                    while (runnings.get() > 0) {
                        Thread.sleep(500);
                        if (status == Status.stop || status == Status.error)
                            return;
                    }
                }

                if(attrCompression) {
                    compress();
                }

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
        for(DownloadRequestForm form: downloadForms) {
            if(form instanceof VFtpCompatDownloadRequestForm) {
                log.info("    " + form.getMachine() + " / " + ((VFtpCompatDownloadRequestForm) form).getCommand());
            } else if(form instanceof VFtpSssDownloadRequestForm) {
                log.info("    " + form.getMachine() + " / " + ((VFtpSssDownloadRequestForm) form).getDirectory() +
                        " ("+((VFtpSssDownloadRequestForm)form).getFiles().size()+" files)");
            } else {
                log.info("    " + form.getMachine() + " / " + ((FtpDownloadRequestForm)form).getCategoryType() +
                        " (" + ((FtpDownloadRequestForm)form).getFiles().size() + " files)");
                /*for(FileInfo f:form.getFiles()) {
                    log.info("      - "+f.getName()+" "+f.getDate()+" "+f.getSize());
                }*/
            }
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
        for(DownloadRequestForm form: downloadForms) {
            String fab = form.getFab();
            if(!fabs.contains(fab))
                fabs.add(fab);
        }
        return fabs;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    private boolean isAchieveJobType() {
        if(ftpType.equals("vftp_sss"))
            return false;
        return true;
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
