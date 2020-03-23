package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.manage.DownloadInfoModel;
import jp.co.canon.cks.eec.fs.manage.DownloadListModel;
import jp.co.canon.cks.eec.fs.manage.FileInfoModel;
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
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FileDownloadExecutor {

    private final Log log = LogFactory.getLog(getClass());
    private static final String file_format = "%s/%s/%s";

    private static final String user_name = "eecAdmin";
    private static final String ftp_root = "./ftp_data";
    private static final String ftp_cache_dir = "cache";

    private enum Status {
        idle, running, done, error
    };

    private static int mUniqueKey = 1;
    private String mId;
    private Status mStatus = Status.idle;
    private List<DownloadForm> mDlList;
    private List<FileDownloadInfo> mDownloadInfos;
    private List<CustomURL> mUrlList;

    private boolean mIsRunning = false;
    private FileServiceManage mServiceManager;
    private FileServiceModel mService;
    private int mTotalFiles = -1;
    private int mDownloadFiles = -1;
    private String mPath = null;


    public FileDownloadExecutor(@NonNull final FileServiceManage serviceManager, @NonNull final List<DownloadForm> request) {
        mServiceManager = serviceManager;
        mService = new FileServiceUsedSOAP("10.1.36.118:8080");

        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        mId = "dl"+(mUniqueKey++)+"-"+String.valueOf(stamp.getTime());
        mDlList = request;
        mDownloadInfos = new ArrayList<>();
        for(DownloadForm req: request) {
            mDownloadInfos.add(new FileDownloadInfo(req));
        }
        mUrlList = new ArrayList<>();
    }

    private Runnable mRunner = () -> {

        mIsRunning = true;
        mDownloadFiles = 0;
        mTotalFiles = 0;
        mDownloadInfos.forEach(inf -> mTotalFiles+=inf.getFiles().size());

        mDownloadInfos.forEach( inf -> {
            String[] files = new String[inf.getFiles().size()];
            long[] sizes = new long[inf.getFiles().size()];
            Calendar[] dates = new Calendar[inf.getFiles().size()];

            for(int i=0; i<inf.getFiles().size(); ++i) {
                FileInfo f = inf.getFiles().get(i);
                files[i] = f.getName();
                sizes[i] = f.getSize();
                dates[i] = convertStringToCalendar(f.getDate());
            }

            try {
                String reqNo = mServiceManager.registRequest(
                        inf.getSystem(),
                        user_name,
                        inf.getTool(),
                        "",
                        inf.getLogType(),
                        files,
                        sizes,
                        dates);
                log.warn("registRequest reqNo=" + reqNo);

                while(!inf.isDownloadComplete()) {
                    RequestListBean requestList = mService.createDownloadList(inf.getSystem(), null, null);
                    for (int i = 0; i < requestList.getRequestListCount(); ++i) {
                        RequestInfoBean reqInfo = requestList.getRequestInfo(i);
                        if (reqInfo.getRequestNo().equals(reqNo)) {
                            String url = mServiceManager.download(user_name, inf.getSystem(), inf.getTool(), reqNo, reqInfo.getArchiveFileName());
                            log.warn("download " + reqInfo.getArchiveFileName() + "(url=" + url + ")");
                            inf.setUrl(url);
                            break;
                        }
                    }
                    Thread.sleep(300);
                }

            } catch (RemoteException e) {
                mStatus = Status.error;
                log.error("FileServiceModel.registRequest occurs service exception");
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mDownloadFiles += inf.getFiles().size();
            log.warn("====download files="+mDownloadFiles);
        });

        mStatus = Status.done;
        log.warn("download done");

        doFtpProc();

        // Compress files
        mPath = "Congrat!";
        mIsRunning = false;
    };

    private File getCacheDir() {
        File dir = new File("./ftp_data/cache");
        if(dir.exists()==false) {
            dir.mkdirs();
        }
        return dir;
    }

    private void flushCache(File cacheDir) {
        try {
            FileUtils.deleteDirectory(cacheDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getLogSubDir(@NonNull FileDownloadInfo logInf) {
        return logInf.getTool()+"/"+logInf.getLogType();
    }

    private void doFtpProc() {

        log.warn("doFtpProc()");

        File dir = new File(ftp_root, ftp_cache_dir);
        if(dir.exists()) {
            flushCache(dir);
        }
        if(dir.exists()==false) {
            dir.mkdirs();
        }

        for(FileDownloadInfo inf: mDownloadInfos) {

            CustomURL url = inf.getUrl();
            File toolDir = new File(dir, getLogSubDir(inf));
            toolDir.mkdirs();

            if (inf.isDownloadComplete()==false || url == null) {
                log.warn("download hasn't completed");
                continue;
            }

            File cache = new File(toolDir, url.getLastFileName());

            FTP ftp = new FTP(url.getHost(), url.getPort());
            try {
                if(cache.createNewFile()==false) {
                    log.warn("creating file failed");
                    continue;
                }
                OutputStream outs = new FileOutputStream(cache);

                ftp.connect();
                ftp.login(url.getLoginUser(), url.getLoginPassword());
                ftp.binary();

                String ftpMode = url.getFtpMode();
                if (ftpMode != null && ftpMode.isEmpty() == false && ftpMode.equals("active")) {
                    ftp.setDataConnectionMode(1);
                } else {
                    ftp.setDataConnectionMode(2);
                }
                InputStream is = ftp.openFileStream(url.getFile());

                byte[] buffer = new byte[256];
                while (true) {
                    int size = is.read(buffer);
                    if (size == -1) {
                        break;
                    }
                    outs.write(buffer);
                    outs.flush();
                }
                outs.close();
                inf.setLocalPath(cache);
                log.warn("ftp-proc: "+url.getLastFileName()+" download done");

            } catch (FTPException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void doCompress() {
        // compress all
    }

    private Calendar convertStringToCalendar(@NonNull final String str) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat fmter = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            cal.setTime(fmter.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }

    public String getId() {
        return mId;
    }

    public void start() {
        log.warn("file download start ("+mDlList.size()+")");
        dumpFileList();
        (new Thread(mRunner)).start();
    }

    public void stop() {
        // FIXME
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public List<String> getFileList() {
        List<String> list = new ArrayList<>();
        for(DownloadForm form: mDlList) {
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
        return mDownloadFiles;
    }

    public int getTotalFiles() {
        return mTotalFiles;
    }

    private void dumpFileList() {
        if(mDlList!=null) {
            for(DownloadForm form: mDlList) {
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
