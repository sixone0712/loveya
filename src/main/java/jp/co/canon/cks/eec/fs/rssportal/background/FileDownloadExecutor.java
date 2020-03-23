package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.manage.DownloadInfoModel;
import jp.co.canon.cks.eec.fs.manage.DownloadListModel;
import jp.co.canon.cks.eec.fs.manage.FileInfoModel;
import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.ServiceException;
import jp.co.canon.cks.eec.fs.portal.bussiness.TedFileServiceModelImpl;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.model.FileInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.SimpleFormatter;

public class FileDownloadExecutor {

    private final Log log = LogFactory.getLog(getClass());
    private static final String file_format = "%s/%s/%s";

    private enum Status {
        idle, running, done, error
    };

    private static int mUniqueKey = 1;
    private String mId;
    private Status mStatus = Status.idle;
    private List<DownloadForm> mDlList;
    private boolean mIsRunning = false;
    private FileServiceManage mServiceManager;
    private FileServiceModel mService; // 'mService' will be removed.
    private int mTotalFiles = -1;
    private int mDownloadFiles = -1;
    private String mPath = null;


    public FileDownloadExecutor(@NonNull final FileServiceManage serviceManager, @NonNull final List<DownloadForm> request) {
        mServiceManager = serviceManager;

        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        mId = "dl"+(mUniqueKey++)+"-"+String.valueOf(stamp.getTime());
        mDlList = request;
    }

    private Runnable mRunner = () -> {

        mIsRunning = true;
        mDownloadFiles = 0;
        mTotalFiles = 0;
        mDlList.forEach(form -> mTotalFiles+=form.getFiles().size());

        mService = new TedFileServiceModelImpl();   // It's dummy interface, FIXME
        mDlList.forEach( dlItem -> {
            String[] files = new String[dlItem.getFiles().size()];
            long[] sizes = new long[dlItem.getFiles().size()];
            Calendar[] dates = new Calendar[dlItem.getFiles().size()];

            for(int i=0; i<dlItem.getFiles().size(); ++i) {
                FileInfo f = dlItem.getFiles().get(i);
                files[i] = f.getName();
                sizes[i] = f.getSize();
                dates[i] = convertStringToCalendar(f.getDate());
            }

            try {
                String reqNo = mServiceManager.registRequest(
                        dlItem.getSystem(),
                        null,
                        dlItem.getTool(),
                        null,
                        dlItem.getLogType(),
                        files,
                        sizes,
                        dates);
                log.warn("registRequest reqNo="+reqNo);

                DownloadListModel dlList = mServiceManager.createDownloadList(dlItem.getSystem(), dlItem.getTool(), reqNo);
                DownloadInfoModel[] dlInfos = dlList.getDownloadInfos();
                log.warn("dlInfos="+dlInfos.length);

                if(dlInfos.length>0) {
                    for(DownloadInfoModel info: dlInfos) {
                        FileInfoModel[] fileInfos = info.getFiles();
                        log.warn("downloadInfo "+info.getRequestNo()+" files="+fileInfos.length);
                        if(fileInfos.length>0) {
                            for(FileInfoModel file: fileInfos) {
                                String fileUrl = mServiceManager.download(null, dlItem.getSystem(), dlItem.getTool(), reqNo, file.getName());
                                log.warn("fileUrl="+fileUrl);
                            }
                        }
                    }
                }

            } catch (RemoteException e) {
                mStatus = Status.error;
                log.error("FileServiceModel.registRequest occurs service exception");
                e.printStackTrace();
            }
            mDownloadFiles += dlItem.getFiles().size();
            log.warn("====download files="+mDownloadFiles);
        });

        mStatus = Status.done;
        log.warn("download done");

        // Compress files
        mPath = "Congrat!";
        mIsRunning = false;
    };

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
