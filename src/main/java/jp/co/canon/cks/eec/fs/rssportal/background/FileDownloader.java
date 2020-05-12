package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.manage.FileServiceManageServiceLocator;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceUsedSOAP;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.xml.rpc.ServiceException;
import java.util.HashMap;
import java.util.List;

@Component
public class FileDownloader extends Thread {

    private static final int MAX_THREADS_AT_ONCE = 5;

    /* Downloader status */
    private static final String STS_INVALID_ID = "invalid-id";
    private static final String STS_IN_PROGRESS = "in-progress";
    private static final String STS_ERROR = "error";
    private static final String STS_DONE = "done";

    private final DownloadMonitor monitor;
    private HashMap<String, FileDownloadExecutor> mHolders;
    private FileServiceModel mService;
    private FileServiceManage mServiceManager;

    @Autowired
    private FileDownloader(@NonNull DownloadMonitor monitor) {
        log.info("initialize FileDownloader");
        this.monitor = monitor;
        mService = new FileServiceUsedSOAP(DownloadConfig.FCS_SERVER_ADDR);
        FileServiceManageServiceLocator serviceLocator = new FileServiceManageServiceLocator();
        try {
            mServiceManager = serviceLocator.getFileServiceManage();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        mHolders = new HashMap<>();
    }

    public String addRequest(@NonNull final List<DownloadForm> dlList) {
        log.info("addRequest( request-size="+dlList.size()+")");

        if(false) {
            if (mHolders.size() >= MAX_THREADS_AT_ONCE) {
                log.warn("addRequest(): thread full");
                return null;
            }
        }

        FileDownloadExecutor holder = new FileDownloadExecutor("manual","", mServiceManager, mService, dlList, true);
        holder.setMonitor(monitor);
        mHolders.put(holder.getId(), holder);

        holder.start();
        log.warn("jobid="+holder.getId()+" has been started");
        return holder.getId();
    }

    public boolean cancelRequest(@NonNull final String downloadId) {
        if(!isValidId(downloadId)) {
            log.error("cancelRequest/ invalid downloadId "+downloadId);
            return false;
        }
        FileDownloadExecutor holder = mHolders.get(downloadId);
        holder.stop();
        return true;
    }

    public String getStatus(@NonNull final String dlId) {

        if(mHolders.containsKey(dlId)==false) {
            return STS_INVALID_ID;
        }
        FileDownloadExecutor holder = mHolders.get(dlId);
        String status = holder.getStatus();
        if(status.equalsIgnoreCase("error")) {
            return STS_ERROR;
        } else if(status.equalsIgnoreCase("complete")) {
            return STS_DONE;
        } else {
            return STS_IN_PROGRESS;
        }
    }

    public boolean isValidId(@NonNull final String dlId) {
        return mHolders.containsKey(dlId)?true:false;
    }

    public String getDownloadInfo(@NonNull final String dlId) {

        if(mHolders.containsKey(dlId)==false) {
            return null;
        }

        FileDownloadExecutor holder = mHolders.get(dlId);
        if(holder.isRunning()==true) {
            return null;
        }
        return holder.getDownloadPath();
    }

    public int getTotalFiles(@NonNull final String dlId) {
        if(isValidId(dlId)==false) {
            return 0;
        }
        return mHolders.get(dlId).getTotalFiles();
    }

    public int getDownloadFiles(@NonNull final String dlId) {
        if(isValidId(dlId)==false) {
            return 0;
        }
        return mHolders.get(dlId).getDownloadFiles();
    }
    
    public List<String> getFabs(@NonNull final String dlId) {
        if(!isValidId(dlId))
            return null;
        return mHolders.get(dlId).getFabs();
    }

    private final Log log = LogFactory.getLog(getClass());
}
