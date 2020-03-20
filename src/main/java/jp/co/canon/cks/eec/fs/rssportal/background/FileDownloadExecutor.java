package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.ServiceException;
import jp.co.canon.cks.eec.fs.portal.bussiness.TedFileServiceModelImpl;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.model.FileInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
    private FileServiceModel mService = null;
    private int mTotalFiles = -1;
    private int mDownloadFiles = -1;
    private String mPath = null;

    public FileDownloadExecutor(@NonNull final List<DownloadForm> request) {
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
            String[] dates = new String[dlItem.getFiles().size()];

            for(int i=0; i<dlItem.getFiles().size(); ++i) {
                FileInfo f = dlItem.getFiles().get(i);
                files[i] = f.getName();
                sizes[i] = f.getSize();
                dates[i] = f.getDate();
            }

            try {
                mService.registRequest( // FIXME
                        dlItem.getSystem(),
                        null,
                        dlItem.getTool(),
                        null,
                        dlItem.getLogType(),
                        files,
                        sizes,
                        dates);
            } catch (ServiceException e) {
                e.printStackTrace();
                log.error("FileServiceModel.registRequest occurs service exception");
                mStatus = Status.error;
            }
            mDownloadFiles += dlItem.getFiles().size();
            log.warn("====download files="+mDownloadFiles);
        });

        mStatus = Status.done;
        log.warn("download done");

        // Compress files
        mPath = "Congrat!";

        if(false) {
            /* FIXME */
            log.warn("download start");
            something(20000);
            log.warn("download done");
            /* FIXME */
        }

        mIsRunning = false;
    };

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

    /* something is dummy for something */
    private void something(long l) {
        log.warn("fall a sleep for "+l+" msec");
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
