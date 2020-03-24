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

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileDownloadExecutor implements DownloadConfig {

    private final Log log = LogFactory.getLog(getClass());
    private static final String file_format = "%s/%s/%s";

    private static final String user_name = "eecAdmin";     // FIXME

    private enum Status {
        idle, init, download, compress, complete, error
    };

    private static int mUniqueKey = 1;
    private String downloadId;
    private Status status;
    private String errString;
    private List<DownloadForm> downloadForms;
    private List<FileDownloadContext> downloadContexts;
    private String baseDir;

    private FileServiceManage mServiceManager;
    private FileServiceModel mService;
    private int totalFiles = -1;
    private int downloadFiles = -1;
    private String mPath = null;


    public FileDownloadExecutor(@NonNull final FileServiceManage serviceManager, @NonNull final List<DownloadForm> request) {
        status = Status.idle;
        mServiceManager = serviceManager;
        mService = new FileServiceUsedSOAP(DownloadConfig.FCS_SERVER_ADDR);

        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        downloadId = "DL"+(mUniqueKey++)+String.valueOf(stamp.getTime());
        downloadForms = request;
        downloadContexts = new ArrayList<>();
        baseDir = Paths.get(DownloadConfig.ROOT_PATH, downloadId).toString();
    }

    private void initialize() {
        status = Status.init;
        log.info(downloadId+": initialize()");
        for(DownloadForm form: downloadForms) {
            downloadContexts.add(new FileDownloadContext(downloadId, form));
        }
        totalFiles = 0;
        downloadContexts.forEach(context -> totalFiles +=context.getFileCount());
        downloadFiles = 0;
    }

    private class doAsyncProc implements Runnable {

        final private FileDownloadContext context;

        private doAsyncProc(@NonNull FileDownloadContext context) {
            this.context = context;
        }

        @Override
        public void run() {

            log.info(downloadId+": doAsyncProc()");

            try {
                regist();
                download();
                transfer();
                extract();

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
            while(true) {
                RequestListBean requestList = mService.createDownloadList(context.getSystem(), null, null);
                for (int i = 0; i < requestList.getRequestListCount(); ++i) {
                    RequestInfoBean reqInfo = requestList.getRequestInfo(i);
                    if (reqInfo.getRequestNo().equals(context.getRequestNo())) {
                        String achieveUrl = mServiceManager.download(user_name, context.getSystem(), context.getTool(),
                                context.getRequestNo(), reqInfo.getArchiveFileName());
                        log.info("download " + reqInfo.getArchiveFileName() + "(url=" + achieveUrl + ")");
                        context.setAchieveUrl(achieveUrl);
                        break;
                    }
                }
                if(context.isDownloadComplete()) {
                    break;
                }
                Thread.sleep(300);
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
                        int size;
                        while((size=zis.read(buf))>0) {
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
            downloadFiles += context.getFileCount();
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

    private Runnable runner = () -> {

        initialize();

        status = Status.download;
        downloadContexts.forEach(context->(new Thread(new doAsyncProc(context))).start());

        while(downloadFiles!=totalFiles) {
            if(status==Status.error) {
                return;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        compress();
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

    private void dumpFileList() {
        if(downloadForms !=null) {
            for(DownloadForm form: downloadForms) {
                log.warn(String.format("tool: %s logType: %s", form.getTool(), form.getLogType()));
                for(FileInfo file: form.getFiles()) {
                    log.warn(String.format("  %s (%d)", file.getName(), file.getSize()));
                }
            }
        } else {
            log.error("null dlList");
        }
    }

    public String getId() {
        return downloadId;
    }

    public void start() {
        log.warn("file download start ("+ downloadForms.size()+")");
        dumpFileList();
        (new Thread(runner)).start();
    }

    public void stop() {
        // TBD
    }

    private void setError() {
        setError("no reason");
    }

    private void setError(@NonNull final String error) {
        log.error(error);
        status = Status.error;
        errString = error;
    }

    public boolean isRunning() {
        return (status==Status.complete || status==Status.error)?true:false;
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
        return downloadFiles;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public String getErrString() {
        return errString;
    }
}
