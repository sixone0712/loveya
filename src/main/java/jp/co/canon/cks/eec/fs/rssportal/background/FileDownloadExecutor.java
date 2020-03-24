package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.portal.bean.RequestInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.RequestListBean;
import jp.co.canon.cks.eec.fs.portal.bussiness.*;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.model.FileInfo;
import jp.co.canon.cks.eec.util.ftp.FTP;
import jp.co.canon.cks.eec.util.ftp.FTPException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.lang.NonNull;

import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileDownloadExecutor implements DownloadConfig {

    private final Log log = LogFactory.getLog(getClass());
    private static final String file_format = "%s/%s/%s";

    private static final String user_name = "eecAdmin";     // FIXME
    private static final String ftp_root = "./ftp_data";
    private static final String ftp_cache_dir = "cache";

    private enum Status {
        idle, running, done, error
    };

    private static int mUniqueKey = 1;
    private String downloadId;
    private Status mStatus = Status.idle;
    private List<DownloadForm> downloadForms;
    private List<FileDownloadContext> downloadContexts;
    private String baseDir;

    private boolean mIsRunning = false;
    private FileServiceManage mServiceManager;
    private FileServiceModel mService;
    private int totalFiles = -1;
    private int downloadFiles = -1;
    private String mPath = null;


    public FileDownloadExecutor(@NonNull final FileServiceManage serviceManager, @NonNull final List<DownloadForm> request) {
        mServiceManager = serviceManager;
        mService = new FileServiceUsedSOAP("10.1.36.118:8080");

        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        downloadId = "dl"+(mUniqueKey++)+"-"+String.valueOf(stamp.getTime());
        downloadForms = request;
        downloadContexts = new ArrayList<>();
        baseDir = Paths.get(DownloadConfig.ROOT_PATH, downloadId).toString();
    }

    private void initialize() {
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
                    log.error("error! wrong achieve files");
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
                    log.error("error! could not get next entry");
                    e.printStackTrace();
                }
                zipFile.delete();
            }
            downloadFiles += context.getFileCount();
        }
    }

    private void compress() {
        log.info(downloadId+": compress()");
        Compressor comp = new Compressor();
        String zipDir = Paths.get(DownloadConfig.ZIP_PATH, downloadId, "test.zip").toString();
        if(comp.compress(baseDir, zipDir)) {
            mPath = zipDir;
        }
    }

    private void wrapup() {
        log.info(downloadId+": wrapup()");
    }

    private Runnable runner = () -> {
        initialize();
        downloadContexts.forEach(context->{
            (new Thread(new doAsyncProc(context))).start();
        });
        while(downloadFiles!=totalFiles) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        compress();
        wrapup();
    };

    public String getId() {
        return downloadId;
    }

    public void start() {
        log.warn("file download start ("+ downloadForms.size()+")");
        dumpFileList();
        (new Thread(runner)).start();
    }

    public void stop() {
        // FIXME
    }

    public boolean isRunning() {
        return mIsRunning;
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


}
