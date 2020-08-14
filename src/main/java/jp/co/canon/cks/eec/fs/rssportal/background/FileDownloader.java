package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.ckbs.eec.fs.collect.service.FileInfo;
import jp.co.canon.ckbs.eec.fs.collect.service.LogFileList;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnector;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnectorFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private final FileServiceManageConnectorFactory connectorFactory;
    private final HashMap<String, FileDownloadExecutor> executorList;
    private FileServiceManageConnector connector;

    @Value("${rssportal.collect.cacheBase}")
    private String downloadCacheDir;

    @Value("${rssportal.collect.resultBase}")
    private String downloadResultDir;

    @Value("${rssportal.file-collect-service.addr}")
    private String fileCollectServiceAddr;

    @Value("${rssportal.file-collect-service.retry}")
    private int fileServiceRetryCount;

    @Value("${rssportal.file-collect-service.retry-interval}")
    private int fileServiceRetryInterval;

    @Value("${rssportal.file-service-manager.addr}")
    private String fileServiceAddress;

    @Autowired
    private FileDownloader(DownloadMonitor monitor, FileServiceManageConnectorFactory connectorFactory) {
        log.info("initialize FileDownloader");
        this.monitor = monitor;
        this.connectorFactory = connectorFactory;
        executorList = new HashMap<>();
    }

    public String addRequest(CollectType collectType, final List<DownloadRequestForm> dlList) {
        log.info("addRequest( request-size="+dlList.size()+")");

        FileDownloadExecutor executor = new FileDownloadExecutor(collectType.name(),"", this, dlList, true);
        executor.setMonitor(monitor);
        executor.setAttrDownloadFilesViaMultiSessions(true);
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

        if(!executorList.containsKey(dlId)) {
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

    public long getLastUpdateTime(@NonNull String downloadId) {
        if(!executorList.containsKey(downloadId))
            return -1;
        return executorList.get(downloadId).getLastUpdate();
    }

    public boolean isValidId(@NonNull final String dlId) {
        return executorList.containsKey(dlId);
    }

    public String getDownloadInfo(@NonNull final String dlId) {
        if(!executorList.containsKey(dlId)) {
            return null;
        }
        FileDownloadExecutor executor = executorList.get(dlId);
        if(executor.isRunning()) {
            return null;
        }
        return executor.getDownloadPath();
    }

    public String getBaseDir(@NonNull final String dlId) {
        if(!executorList.containsKey(dlId))
            return null;
        FileDownloadExecutor executor = executorList.get(dlId);
        if(executor.isRunning())
            return null;
        return executor.getBaseDir();
    }

    public int getTotalFiles(@NonNull final String dlId) {
        if(!isValidId(dlId)) {
            return 0;
        }
        return executorList.get(dlId).getTotalFiles();
    }

    public long getDownloadFiles(@NonNull final String dlId) {
        if(!isValidId(dlId)) {
            return 0;
        }
        return executorList.get(dlId).getDownloadFiles();
    }
    
    public List<String> getFabs(@NonNull final String dlId) {
        if(!isValidId(dlId))
            return null;
        return executorList.get(dlId).getFabs();
    }

    public CollectType getFtpType(String dlId) {
        if(!isValidId(dlId))
            return null;
        String ftpType = executorList.get(dlId).getFtpType();
        return CollectType.valueOf(ftpType);
    }

    public boolean createFtpDownloadFileList(
            final List<DownloadRequestForm> formList,
            @NonNull String fab, @NonNull String tool,
            @NonNull String type, @NonNull String typeStr,
            @Nullable Calendar from, @Nullable Calendar to, String dir) throws InterruptedException {

        if(from==null) {
            from = Calendar.getInstance();
            from.set(1970, 1, 1);
        }
        if(to==null) {
            to = Calendar.getInstance();
            to.set(3000, 12,31);
        }
        FtpDownloadRequestForm form = new FtpDownloadRequestForm(fab, tool, type, typeStr);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        int retry = 0;
        while(retry<fileServiceRetryCount) {
            Thread.sleep(1); // job interrupt point
            try {
                LogFileList logFileList = getConnector().getFtpFileList(tool, type, dateFormat.format(from.getTime()),
                        dateFormat.format(to.getTime()), "", dir);
                for(FileInfo logFile:logFileList.getList()) {
                    String fileName = logFile.getFilename();
                    if(fileName==null || fileName.equals("") || fileName.endsWith(".") || fileName.endsWith("..")) {
                        continue;
                    }
                    if(true || logFile.getType().equalsIgnoreCase("D")) {
                        long current = System.currentTimeMillis();
                        if(current>=from.getTimeInMillis() && current<=to.getTimeInMillis()) {
                            if (!createFtpDownloadFileList(formList, fab, tool, type, typeStr, from, to, fileName)) {
                                log.warn("failed to createFileList(dir=" + fileName + ")");
                                return false;
                            }
                        }
                    } else {
                        Date date = dateFormat.parse(logFile.getTimestamp());
                        form.addFile(fileName, logFile.getSize(), logFile.getTimestamp(), date.getTime());
                    }
                }
                break;
            } catch (ParseException e) {
                log.error("error on parsing logFile date");
                return false;
            }
        }
        formList.add(form);
        return true;
    }

    public boolean isBetween(String time, String start, String end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date dtTime = dateFormat.parse(time);
            Date dtStart = dateFormat.parse(start);
            Date dtEnd = dateFormat.parse(end);
            if((dtTime.after(dtStart) && dtTime.before(dtEnd)) || dtTime.compareTo(dtStart)==0 || dtTime.compareTo(dtEnd)==0) {
                return true;
            }
        } catch (ParseException e) {
            log.error("dateTime parse error");
        }
        return false;
    }

    public FileServiceManageConnector getConnector() {
        if(connector==null) {
            log.info("file-service-manage.addr="+fileServiceAddress);
            connector = connectorFactory.getConnector(fileServiceAddress);
        }
        return connector;
    }

    public String getDownloadCacheDir() {
        return downloadCacheDir;
    }

    public String getDownloadResultDir() {
        return downloadResultDir;
    }
    
    public int getFileServiceRetryCount() { return fileServiceRetryCount; }
    public int getFileServiceRetryInterval() { return fileServiceRetryInterval; }

    private final Log log = LogFactory.getLog(getClass());
}
