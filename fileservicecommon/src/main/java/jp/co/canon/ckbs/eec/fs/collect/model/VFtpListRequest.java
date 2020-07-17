package jp.co.canon.ckbs.eec.fs.collect.model;

import lombok.Getter;
import lombok.Setter;

public class VFtpListRequest {
    public enum Status{
        WAIT,
        EXECUTING,
        CANCEL,
        ERROR,
        EXECUTED
    }

    @Getter @Setter
    String requestNo;

    @Getter @Setter
    String machine;

    @Getter @Setter
    String directory;

    @Getter
    Status status = Status.WAIT;


}
