package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.TedFileServiceModel;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.model.FileInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FileDownloadHolder {

    private final Log log = LogFactory.getLog(getClass());
    private static final String FILE_FORMAT = "%s/%s/%s";

    private static int mUniqueKey = 1;
    private String mId;
    private List<DownloadForm> mDlList;
    private boolean mIsRunning = false;
    private FileServiceModel mService = null;
    private int mTotalFiles = -1;
    private int mDownloadFiles = -1;

    public FileDownloadHolder(@NonNull final List<DownloadForm> request) {
        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        mId = "dl"+(mUniqueKey++)+"-"+String.valueOf(stamp.getTime());
        mDlList = request;
    }

    private Runnable mRunner = () -> {

        mIsRunning = true;
        mDownloadFiles = 0;
        mDlList.forEach(form -> mTotalFiles+=form.getFiles().size());

        mService = new TedFileServiceModel();   // It's dummy interface, FIXME
        mDlList.forEach( dlItem -> {
            if(false) {
                List<String> files = new ArrayList<>();
                List<Long> sizes = new ArrayList<>();

                dlItem.getFiles().forEach(file -> {
                    files.add(file.getName());
                    sizes.add(file.getSize());
                });

//                mService.registRequest( // FIXME
//                        dlItem.getSystem(),
//                        null,
//                        dlItem.getTool(),
//                        null,
//                        dlItem.getLogType(),
//                        files.toArray(),
//                        sizes.toArray(),
//
//                        )
            }
        });

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
                list.add(String.format(FILE_FORMAT, form.getTool(), form.getLogType(), fileInfo.getName()));
            });
        }
        return list;
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
