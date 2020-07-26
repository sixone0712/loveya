package jp.co.canon.ckbs.eec.fs.collect.model;

import lombok.Getter;
import lombok.Setter;

public class VFtpCompatDownloadRequest {
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
    RequestFileInfo file;

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

    public synchronized void setStatus(Status status){
        if (this.status == Status.WAIT || this.status == Status.EXECUTING){
            this.status = status;
        }
    }

    public void downloaded(String filename, long size){
        if (file.getName().equals(filename)){
            file.setSize(size);
            file.setDownloaded(true);
            if (archive == false){
                file.setDownloadPath(this.requestNo + "/" + filename);
            }
        }
    }
}
