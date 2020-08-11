package jp.co.canon.ckbs.eec.fs.collect.model;

import lombok.Getter;
import lombok.Setter;

public class FtpRequest {
    public enum Status{
        EXECUTED,
        ERROR,
        CANCEL,
        WAIT,
        EXECUTING
    }

    @Getter @Setter
    String machine;

    @Getter @Setter
    String requestNo;

    @Getter @Setter
    long timestamp;

    @Getter @Setter
    long completedTime;

    @Getter
    Status status = Status.WAIT;

    public synchronized void setStatus(Status status){
        if (this.status == Status.WAIT || this.status == Status.EXECUTING){
            this.status = status;
        }
    }
}
