package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileDownloader {

    private static final int MAX_THREADS_AT_ONCE = 5;

    /* Downloader status */
    private static final String STS_INVALID_ID = "invalid-id";
    private static final String STS_IN_PROGRESS = "in-progress";
    private static final String STS_DONE = "done";

    private final Log log = LogFactory.getLog(getClass());
    private HashMap<String, FileDownloadHolder> mHolders = new HashMap<>();

    private static FileDownloader instance = null;
    static {
        instance = new FileDownloader();
    }

    public static FileDownloader getInstance() {
        return instance;
    }

    private FileDownloader() {/* Singleton. Prevent to call the constructor. */}



    public String addRequest(@NonNull final List<DownloadForm> dlList) {
        log.warn("addRequest( request-size="+dlList.size()+")");
        if(mHolders.size()>=MAX_THREADS_AT_ONCE) {
            log.warn("addRequest(): thread full");
            return null;
        }
        FileDownloadHolder holder = new FileDownloadHolder(dlList);
        mHolders.put(holder.getId(), holder);

        holder.start();
        log.warn("jobid="+holder.getId()+" has been started");
        return holder.getId();
    }

    public List<String> getTotalFileList(@NonNull final String dlId) {
        if(mHolders.containsKey(dlId)==false) {
            return null;
        }
        return mHolders.get(dlId).getFileList();
    }

    public List<String> getCompletedFileList(final String dlId) {
        if(mHolders.containsKey(dlId)==false) {
            return null;
        }
        FileDownloadHolder holder = mHolders.get(dlId);
        // FIXME
        return null;
    }

    public int getCompletedFileCount() {
        // FIXME
        return 1000;
    }

    public String getStatus(@NonNull final String dlId) {

        if(mHolders.containsKey(dlId)==false) {
            return STS_INVALID_ID;
        }
        FileDownloadHolder holder = mHolders.get(dlId);
        if(holder.isRunning()) {
            return STS_IN_PROGRESS;
        } else {
            return STS_DONE;
        }
    }

    public boolean isValidId(@NonNull final String dlId) {
        return mHolders.containsKey(dlId)?true:false;
    }

}
