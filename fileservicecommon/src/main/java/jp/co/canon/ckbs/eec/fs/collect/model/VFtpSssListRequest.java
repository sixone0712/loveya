package jp.co.canon.ckbs.eec.fs.collect.model;

import jp.co.canon.ckbs.eec.fs.collect.service.VFtpFileInfo;
import lombok.Getter;
import lombok.Setter;

public class VFtpSssListRequest {
    public enum Status{
        WAIT,
        EXECUTING,
        EXECUTED,
        CANCEL,
        ERROR
    }

    @Getter @Setter
    String requestNo;

    @Getter @Setter
    String machine;

    @Getter @Setter
    String directory;

    @Getter
    Status status = Status.WAIT;

    @Getter @Setter
    VFtpFileInfo[] fileList;

    @Getter @Setter
    long timestamp;

    @Getter @Setter
    long completedTime;

    public void setStatus(Status status){
        if (this.status == Status.WAIT || this.status == Status.EXECUTING){
            this.status = status;
        }
    }

}
