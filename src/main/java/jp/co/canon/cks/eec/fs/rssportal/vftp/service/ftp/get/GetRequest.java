package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jp.co.canon.cks.eec.fs.rssportal.background.Compressor;
import jp.co.canon.cks.eec.fs.rssportal.vftp.FileDownloadStatus;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.FileItem;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.Request;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.ServerInfo;
import lombok.Getter;
import lombok.Setter;

public class GetRequest implements PropertyChangeListener {
    private Request request = new Request();
    /* 주요 상태 */
    private @Getter @Setter String requestNo;
    private @Getter Calendar createdTime;
    private @Getter Calendar completedTime;
    private @Getter int requestFileCount = 0;
    private @Getter int downloadFileCount = 0;
    private @Getter Status status = Status.NONE;
    private @Setter String requestDir;
    private @Setter String compressFilename;
    private @Getter @Setter String downloadPath;
    private @Getter @Setter String downloadFilename;

    private @Getter ArrayList<FileItem> downloadedFileList = new ArrayList<>();
    private String[] errorList = null;

    private Map<String, GetFileItemList> fileItemListMap = new HashMap<>();

    private Map<String, ServerInfo> serverInfoMap = new HashMap<>();

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private boolean stopped = false;

    public GetRequest(){
        this.createdTime = Calendar.getInstance();
    }

    private GetFileItemList getListForServer(String name) {
        GetFileItemList list = fileItemListMap.get(name);
        if (list == null) {
            list = new GetFileItemList();
            fileItemListMap.put(name, list);
        }
        return list;
    }

    private void setStatus(Status status){
        boolean statusChanged = this.status != status;
        synchronized(this){
            this.status = status;
        }
        if (statusChanged){
            propertyChangeSupport.firePropertyChange("statusChanged", null, null);
        }
    }

    private void addDownloadedFile(FileItem item){
        synchronized(this){
            downloadedFileList.add(item);
            downloadFileCount++;    
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    private void addServerInfo(ServerInfo serverInfo) {
        String serverName = serverInfo.getName();
        if (serverInfoMap.get(serverName) == null) {
            serverInfoMap.put(serverName, serverInfo);
        }
    }

    public void stop(){
        stopped = true;
        request.stop();
    }

    public void add(ServerInfo serverInfo, String path, String filename){
        addServerInfo(serverInfo);
        GetFileItemList list = getListForServer(serverInfo.getName());
        list.addReady(new GetFileItem(path, filename, requestDir + "/down"));
        requestFileCount++;
    }

    public void execute() {
        for (String serverName : fileItemListMap.keySet()) {
            ServerInfo serverInfo = serverInfoMap.get(serverName);
            GetFileItemList list = getListForServer(serverName);

            GetSubRequest subreq = new GetSubRequest(serverName, list);
            subreq.addPropertyChangeListener(this);
            request.addSubRequest(serverInfo, subreq);
        }
        request.addPropertyChangeListener(this);
        this.setStatus(Status.PROCESSING_DOWNLOAD);
        request.execute();
    }

    private void processCompress(){
        if (compressFilename != null){
            Thread compressThread = new Thread(new Runnable(){
            
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Compressor compressor = new Compressor();
                    File destFile = new File(new File(requestDir), compressFilename);
                    
                    compressor.compress(requestDir + "/down", destFile.getAbsolutePath());
                    
                    GetRequest.this.setStatus(Status.COMPLETED);
                    propertyChangeSupport.firePropertyChange("completed", null, null);
                    GetRequest.this.setDownloadPath(requestDir + "/down");
                    GetRequest.this.setDownloadFilename(compressFilename);
                }
            });
            compressThread.start();
            return;
        }
        this.setStatus(Status.COMPLETED);
        propertyChangeSupport.firePropertyChange("completed", null, null);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("downloaded")){
            Object o = evt.getNewValue();
            if (o instanceof FileItem){
                this.addDownloadedFile((FileItem)o);
            }
            return;
        }
        if (evt.getPropertyName().equals("completed")){
            synchronized(this){
                completedTime = Calendar.getInstance();
                errorList = request.getErrorList();
            }
            if (errorList.length == 0){
                this.setStatus(Status.PROCESSING_COMPRESS);
                processCompress();
                /* 다음 단계는 신규 Thread를 생성하여 파일 압축, 또는 로그 머지 */
/*                Thread compressThread = new Thread(new Runnable(){
                
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Compressor compressor = new Compressor();
//                        compressor.compress(requestDir + "/");
                    }
                });
                compressThread.run(); */
                return;
            }
            this.setStatus(Status.FAILED);
            propertyChangeSupport.firePropertyChange("completed", null, null);
            return;
        }
    }

    public FileDownloadStatus convertToFileDownloadStatus() {
        FileDownloadStatus sts = new FileDownloadStatus();
        sts.setRequestNo(requestNo);
        sts.setRequestFileCount(requestFileCount);
        sts.setDownloadFileCount(downloadFileCount);
        switch(this.status){
            case NONE:
                sts.setStatus(FileDownloadStatus.Status.NONE);
                break;
            case COMPLETED:
                sts.setStatus(FileDownloadStatus.Status.COMPLETED);
                break;
            case FAILED:
                sts.setStatus(FileDownloadStatus.Status.FAILED);
                break;
            case PROCESSING_COMPRESS:
                sts.setStatus(FileDownloadStatus.Status.PROCESSING_COMPRESS);
                break;
            case PROCESSING_DOWNLOAD:
                sts.setStatus(FileDownloadStatus.Status.PROCESSING_DOWNLOAD);
                break;
            default:
                break;
        }
        sts.setDownloadPath(this.getDownloadPath());
        sts.setDownloadFileName(this.getDownloadFilename());
        
        return sts;
    }

    public static enum Status {
        NONE,
        PROCESSING_DOWNLOAD,
        PROCESSING_COMPRESS,
        COMPLETED,
        FAILED,
    }
}