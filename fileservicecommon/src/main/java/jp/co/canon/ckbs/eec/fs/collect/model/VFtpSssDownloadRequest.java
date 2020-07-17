package jp.co.canon.ckbs.eec.fs.collect.model;

import lombok.Getter;
import lombok.Setter;

public class VFtpSssDownloadRequest {
    @Getter @Setter
    String requestNo;

    @Getter @Setter
    String directory;

    @Getter @Setter
    String[] fileList;

    @Getter @Setter
    boolean archive;

}
