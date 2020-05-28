package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.list;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.co.canon.cks.eec.fs.rssportal.vftp.FileListStatus;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.FileItem;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.Request;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.ServerInfo;
import lombok.Getter;
import lombok.Setter;

public class ListRequest implements PropertyChangeListener {
    private Request request = new Request();
    /* 주요 상태 */
    private @Getter @Setter String requestNo;
    private @Getter Calendar createdTime;
    private @Getter Calendar completedTime;
    private @Getter @Setter String path;
    private @Getter @Setter String directory;
    private @Getter Status status = Status.NONE;
    private boolean stopped = false;

    /* 결과 정보 */
    private @Getter ArrayList<FileItem> filelist = new ArrayList<>();
    private String[] errorList = null;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public ListRequest() {
        this.createdTime = Calendar.getInstance();        
    }

    public void stop(){
        stopped = true;
        request.stop();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void add(ServerInfo serverInfo) {
        ListSubRequest subreq = new ListSubRequest(serverInfo.getName(), path, directory);
        subreq.addPropertyChangeListener(this);
        request.addSubRequest(serverInfo, subreq);
    }

    public void execute() {
        request.addPropertyChangeListener(this);
        this.setStatus(Status.PROCESSING);
        request.execute();
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

    private void addFileItem(FileItem item){
        synchronized(this){
            filelist.add(item);
        }
    }

    private void notifyCompleted(String[] errorList){
        completedTime = Calendar.getInstance();        
        if (errorList.length == 0){
            this.setStatus(Status.COMPLETED);
            propertyChangeSupport.firePropertyChange("completed", null, null);
            return;
        }
        this.setStatus(Status.FAILED);
        propertyChangeSupport.firePropertyChange("completed", null, null);
        return;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("listresult")){
            Object evtSrc = evt.getSource();
            if (evtSrc instanceof ListSubRequest){
                Object nv = evt.getNewValue();
                if (nv instanceof List<?>){
                    List<?> l = (List<?>)nv;
                    for(Object x : l){
                        if (x instanceof FileItem){
                            this.addFileItem((FileItem)x);
                        }
                    }
                }
            }
            return;
        }
        if (evt.getPropertyName().equals("completed")){
            errorList = request.getErrorList();
            notifyCompleted(errorList);
            return;
        }
    }

    public FileListStatus convertToFileListStatus() {
        FileListStatus sts = new FileListStatus();
        sts.setRequestNo(requestNo);
        sts.setPath(path);
        sts.setDirectory(directory);
        sts.setFilelist(filelist.toArray(new FileItem[0]));
        switch(status){
            default:
            case NONE:  
                sts.setStatus(FileListStatus.Status.NONE);
                break;
            case COMPLETED:
                sts.setStatus(FileListStatus.Status.COMPLETED);
                break;
            case FAILED:
                sts.setStatus(FileListStatus.Status.FAILED);
                break;
            case PROCESSING:
                sts.setStatus(FileListStatus.Status.PROCESSING);
                break;
        }
        return sts;
    }

    public static enum Status {
        NONE,
        PROCESSING,
        COMPLETED,
        FAILED,
    }
}