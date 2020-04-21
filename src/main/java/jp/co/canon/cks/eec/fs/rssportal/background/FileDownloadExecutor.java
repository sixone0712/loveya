package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.portal.bean.RequestInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.RequestListBean;
import jp.co.canon.cks.eec.fs.portal.bussiness.CustomURL;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceUsedSOAP;
import jp.co.canon.cks.eec.fs.portal.bussiness.ServiceException;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.model.FileInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileDownloadExecutor implements DownloadConfig {

    private final Log log;
    private static final String file_format = "%s/%s/%s";
    private static final String user_name = "eecAdmin";     // FIXME

    private enum Status {
        idle, init, download, compress, complete, error
    };

    private static int mUniqueKey = 1;
    private String desc;
    private String downloadId;
    private Status status;
    private String errString;
    private List<DownloadForm> downloadForms;
    private List<FileDownloadContext> downloadContexts;
    private String baseDir;

    private FileServiceManage mServiceManager;
    private FileServiceModel mService;
    private DownloadMonitor downloadMonitor;
    private int totalFiles = -1;
    private String mPath = null;

    private boolean attrCompression;
    private boolean attrEmptyAllPathBeforeDownload;
    private boolean attrReplaceFileForSameFileName;
    private boolean attrDownloadFilesViaMultiSessions;

    public FileDownloadExecutor(
            @NonNull final FileServiceManage serviceManager,
            @NonNull final List<DownloadForm> request) {
        this(null, serviceManager, request);
    }

    public FileDownloadExecutor(
            @Nullable final String desc,
            @NonNull final FileServiceManage serviceManager,
            @NonNull final List<DownloadForm> request) {
        this(desc, serviceManager, request, false);
    }

    public FileDownloadExecutor(
            @Nullable final String desc,
            @NonNull final FileServiceManage serviceManager,
            @NonNull final List<DownloadForm> request,
            boolean compress) {

        if(desc==null) {
            log = LogFactory.getLog(getClass());
        } else {
            String fmt = "%s:%s";
            log = LogFactory.getLog(String.format(fmt, getClass().toString(), desc));
        }

        status = Status.idle;
        mServiceManager = serviceManager;
        mService = new FileServiceUsedSOAP(DownloadConfig.FCS_SERVER_ADDR);
        downloadMonitor = new DownloadMonitor();

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
            downloadContexts.add(new FileDownloadContext(downloadId, form));
        }
        totalFiles = 0;
        downloadContexts.forEach(context -> totalFiles +=context.getFileCount());
    }

    private class AsyncProc implements Runnable {

        private boolean completed = false;
        private final FileDownloadContext context;

        private AsyncProc(@NonNull FileDownloadContext context) {
            this.context = context;
        }

        @Override
        public void run() {

            log.info(downloadId+": doAsyncProc()");

            try {
                regist();
                download();
                transfer();
                if(false) {
                    extract();
                }
                completed = true;

            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InterruptedException e) { // relevant to sleep working
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }

        private void regist() throws RemoteException {

            log.info("regist()");
            String request = mServiceManager.registRequest(
                    context.getSystem(),
                    context.getUser(),
                    context.getTool(),
                    context.getComment(),
                    context.getLogType(),
                    context.getFileNames(),
                    context.getFileSizes(),
                    context.getFileDates());

            log.warn("requestNo="+request);
            context.setRequestNo(request);
        }

        private void download() throws ServiceException, RemoteException, InterruptedException {

            log.info("download()");
            downloadMonitor.add(context.getSystem(), context.getTool(), context.getRequestNo());

            while(true) {
                RequestInfoBean requestInfoBean = downloadMonitor.get(context.getSystem(), context.getTool(), context.getRequestNo());
                if (requestInfoBean != null) {
                    context.setDownloadFiles(requestInfoBean.getNumerator());
                    if(requestInfoBean.getNumerator() == requestInfoBean.getDenominator()) {
                        downloadMonitor.delete(context.getSystem(), context.getTool(), context.getRequestNo());
                        break;
                    }
                }
                Thread.sleep(100);
            }

            while(true) {
                RequestListBean requestList = mService.createDownloadList(context.getSystem(), context.getTool(), context.getRequestNo());
                RequestInfoBean reqInfo = requestList.get(context.getRequestNo());
                if(reqInfo==null) {
                    Thread.sleep(100);
                    continue;
                }

                String achieveUrl = mServiceManager.download(user_name, context.getSystem(), context.getTool(),
                        context.getRequestNo(), reqInfo.getArchiveFileName());
                log.info("download " + reqInfo.getArchiveFileName() + "(url=" + achieveUrl + ")");
                context.setAchieveUrl(achieveUrl);
                break;
            }
        }

        private void transfer() {

            log.info("transfer()");
            CustomURL url = context.getAchieveUrl();

            FtpWorker worker = new FtpWorker(url.getHost(), url.getPort(), url.getLoginUser(),
                    url.getLoginPassword(), url.getFtpMode());

            worker.open();

            Path path = Paths.get(context.getOutPath(), url.getLastFileName());
            if(worker.transfer(url.getFile(), path.toString())) {
                context.setFtpProcComplete(true);
            }

            worker.close();
        }

        private void extract() {
            log.trace("extract(achieve="+context.getAchieveUrl().getLastFileName()+")");
            String achieve = Paths.get(context.getOutPath(), context.getAchieveUrl().getLastFileName()).toString();
            File zipFile = new File(achieve);

            if(achieve.endsWith(".zip")) {
                String dir = parseDir(achieve);
                if(zipFile.exists()==false || zipFile.isDirectory()) {
                    setError("wrong achieve file");
                    return;
                }

                try {
                    byte[] buf = new byte[1024];
                    ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
                    ZipEntry entry = zis.getNextEntry();
                    while(entry!=null) {
                        File tmpFile = new File(dir, entry.getName());
                        FileOutputStream fos = new FileOutputStream(tmpFile);
                        while(zis.read(buf)>0) {
                            fos.write(buf);
                        }
                        fos.close();
                        entry = zis.getNextEntry();
                    }
                    zis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    setError("cannot find next ZipEntry");
                }
                zipFile.delete();
            }
        }

        public boolean isCompleted() {
            return completed;
        }
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

    private class DownloadMonitor implements Runnable {

        class TargetInfo {
            String system;
            String tool;
            String requestNo;
            RequestInfoBean info;
            long ts;
            boolean activated;

            public TargetInfo(String system, String tool, String requestNo, RequestInfoBean info, long ts) {
                this.system = system;
                this.tool = tool;
                this.requestNo = requestNo;
                this.info = info;
                this.ts = ts;
                this.activated = true;
            }
        }

        private List<TargetInfo> targets = new ArrayList<>();

        @Override
        public void run() {
            log.info("download monitor start");
            while(status==Status.download) {
                for(TargetInfo target: targets) {
                    if(target.activated) {
                        synchronized (target) {
                            try {
                                RequestListBean requestList = mService.createRequestList(target.system,
                                        target.tool, target.requestNo);
                                if (requestList != null) {
                                    target.info = requestList.get(target.requestNo);
                                    if (target.info != null) {
                                    /*log.info("monitor: " + target.info.getRequestNo() + ": " + target.info.getNumerator() + "/" +
                                            target.info.getDenominator());*/
                                        target.ts = System.currentTimeMillis();
                                    }
                                }
                            } catch (ServiceException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("download monitor end");
        }

        private TargetInfo getTarget(String system, String tool, String requestNo) {
            for(TargetInfo target: targets) {
                if(target.system.equals(system) && target.tool.equals(tool) && target.requestNo.equals(requestNo)) {
                    return target;
                }
            }
            return null;
        }

        public void add(@NonNull final String system, @NonNull final String tool, @NonNull final String requestNo) {
            if(getTarget(system, tool, requestNo)==null) {
                try {
                    RequestListBean requestList = mService.createDownloadList(system, tool, requestNo);
                    RequestInfoBean inf = requestList.get(requestNo);
                    synchronized (targets) {
                        targets.add(new TargetInfo(system, tool, requestNo, inf, System.currentTimeMillis()));
                    }
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
        }

        public void delete(@NonNull final String system, @NonNull final String tool, @NonNull final String requestNo) {
            TargetInfo target = getTarget(system, tool, requestNo);
            if(target!=null) {
                synchronized (targets) {
                    //targets.remove(target);
                    target.activated = false;
                }
            }
        }

        public RequestInfoBean get(@NonNull final String system, @NonNull final String tool, @NonNull final String requestNo) {
            TargetInfo target = getTarget(system, tool, requestNo);
            return target!=null?target.info:null;
        }
    };

    private Runnable runner = () -> {

        initialize();

        status = Status.download;
        new Thread(downloadMonitor).start();

        List<AsyncProc> procs = new ArrayList<>();
        downloadContexts.forEach(context->{
            try {
                AsyncProc proc = new AsyncProc(context);
                procs.add(proc);
                new Thread(proc).start();
                if(attrDownloadFilesViaMultiSessions==false) {
                    while (proc.isCompleted() == false)
                        Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        try {
            for(AsyncProc proc: procs) {
                while(proc.isCompleted()==false)
                    Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(attrCompression) {
            compress();
        }
        wrapup();
    };

    private String parseDir(@NonNull final String file) {
        String sep = File.separator;
        int idx = file.lastIndexOf(sep);
        if(idx==-1) {
            return "";
        }
        return file.substring(0, idx);
    }

    private void setError() {
        setError("no reason");
    }

    private void setError(@NonNull final String error) {
        log.error(error);
        status = Status.error;
        errString = error;
    }

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

    public String getErrString() {
        return errString;
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
