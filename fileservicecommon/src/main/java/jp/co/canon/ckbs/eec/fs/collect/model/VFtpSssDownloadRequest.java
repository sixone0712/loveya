package jp.co.canon.ckbs.eec.fs.collect.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class VFtpSssDownloadRequest {
    public enum Status {
        WAIT,
        EXECUTING,
        EXECUTED,
        CANCEL,
        ERROR
    }

    @Getter @Setter
    String machine;

    @Getter @Setter
    String requestNo;

    @Getter @Setter
    String directory;

    Map<String, RequestFileInfo> fileInfoMap = new HashMap<>();

    @Getter @Setter
    boolean archive;

    @Getter @Setter
    long timestamp;

    @Getter @Setter
    long completedTime;

    @Getter
    Status status = Status.WAIT;

    @Getter @Setter
    String archiveFileName;

    @Getter @Setter
    String archiveFilePath;

    public void setFileList(RequestFileInfo[] fileList){
        fileInfoMap.clear();
        for(RequestFileInfo info : fileList){
            fileInfoMap.put(info.getName(), info);
        }
    }

    public synchronized void setStatus(Status status){
        if (this.status == Status.WAIT || this.status == Status.EXECUTING){
            this.status = status;
        }
    }

    public RequestFileInfo[] getFileList(){
        return fileInfoMap.values().toArray(new RequestFileInfo[0]);
    }

    public void downloaded(String filename, long size){
        RequestFileInfo info = fileInfoMap.get(filename);
        if (info != null){
            info.setSize(size);
            info.setDownloaded(true);
            if (archive == false){
                info.setDownloadPath(this.requestNo + "/" + filename);
            }
        }
    }
}
