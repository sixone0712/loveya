package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.manage.FileInfoModel;
import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.manage.FileServiceManageServiceLocator;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceUsedSOAP;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Component
public class FileDownloader extends Thread {

    /* Downloader status */
    private static final String STS_INVALID_ID = "invalid-id";
    private static final String STS_IN_PROGRESS = "in-progress";
    private static final String STS_ERROR = "error";
    private static final String STS_DONE = "done";

    private final DownloadMonitor monitor;
    private HashMap<String, FileDownloadExecutor> executorList;
    private FileServiceModel service;
    private FileServiceManage serviceManage;

    @Value("${rssportal.collect.cacheBase}")
    private String downloadCacheDir;

    @Value("${rssportal.collect.resultBase}")
    private String downloadResultDir;

    @Autowired
    private FileDownloader(@NonNull DownloadMonitor monitor) {
        log.info("initialize FileDownloader");
        this.monitor = monitor;
        service = new FileServiceUsedSOAP(DownloadConfig.FCS_SERVER_ADDR);
        FileServiceManageServiceLocator serviceLocator = new FileServiceManageServiceLocator();
        try {
            serviceManage = serviceLocator.getFileServiceManage();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        executorList = new HashMap<>();
    }

    public String addRequest(@NonNull final List<DownloadForm> dlList) {
        log.info("addRequest( request-size="+dlList.size()+")");

        FileDownloadExecutor executor = new FileDownloadExecutor("manual","", this, dlList, true);
        executor.setMonitor(monitor);
        executorList.put(executor.getId(), executor);

        executor.start();
        log.warn("jobid="+executor.getId()+" has been started");
        return executor.getId();
    }

    public boolean cancelRequest(@NonNull final String downloadId) {
        if(!isValidId(downloadId)) {
            log.error("cancelRequest/ invalid downloadId "+downloadId);
            return false;
        }
        FileDownloadExecutor executor = executorList.get(downloadId);
        executor.stop();
        return true;
    }

    public String getStatus(@NonNull final String dlId) {

        if(executorList.containsKey(dlId)==false) {
            return STS_INVALID_ID;
        }
        FileDownloadExecutor executor = executorList.get(dlId);
        String status = executor.getStatus();
        if(status.equalsIgnoreCase("error")) {
            return STS_ERROR;
        } else if(status.equalsIgnoreCase("complete")) {
            return STS_DONE;
        } else {
            return STS_IN_PROGRESS;
        }
    }

    public boolean isValidId(@NonNull final String dlId) {
        return executorList.containsKey(dlId)?true:false;
    }

    public String getDownloadInfo(@NonNull final String dlId) {
        if(executorList.containsKey(dlId)==false) {
            return null;
        }
        FileDownloadExecutor executor = executorList.get(dlId);
        if(executor.isRunning()==true) {
            return null;
        }
        return executor.getDownloadPath();
    }

    public String getBaseDir(@NonNull final String dlId) {
        if(executorList.containsKey(dlId)==false)
            return null;
        FileDownloadExecutor executor = executorList.get(dlId);
        if(executor.isRunning())
            return null;
        return executor.getBaseDir();
    }

    public int getTotalFiles(@NonNull final String dlId) {
        if(isValidId(dlId)==false) {
            return 0;
        }
        return executorList.get(dlId).getTotalFiles();
    }

    public int getDownloadFiles(@NonNull final String dlId) {
        if(isValidId(dlId)==false) {
            return 0;
        }
        return executorList.get(dlId).getDownloadFiles();
    }
    
    public List<String> getFabs(@NonNull final String dlId) {
        if(!isValidId(dlId))
            return null;
        return executorList.get(dlId).getFabs();
    }

    public DownloadForm createDownloadFileList(@NonNull String fab, @NonNull String tool,
                                               @NonNull String type, @NonNull String typeStr,
                                               @NonNull Calendar from, @NonNull Calendar to) {
        DownloadForm form = new DownloadForm("FS_P#A", fab, tool, type, typeStr);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            FileInfoModel[] fileInfos = serviceManage.createFileList(tool, type, from, to, "", "");
            for(FileInfoModel file: fileInfos) {
                dateFormat.setTimeZone(file.getTimestamp().getTimeZone());
                String time = dateFormat.format(file.getTimestamp().getTime());
                form.addFile(file.getName(), file.getSize(), time, file.getTimestamp().getTimeInMillis());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
        return form;
    }

    public FileServiceModel getService() {
        return this.service;
    }

    public FileServiceManage getServiceManage() {
        return this.serviceManage;
    }

    public String getDownloadCacheDir() {
        return downloadCacheDir;
    }

    public String getDownloadResultDir() {
        return downloadResultDir;
    }


    private final Log log = LogFactory.getLog(getClass());
}
