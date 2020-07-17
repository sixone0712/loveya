package jp.co.canon.ckbs.eec.fs.collect.model;

import lombok.Getter;
import lombok.Setter;

public class VFtpCompatDownloadRequest {
    @Getter @Setter
    String requestNo;

    @Getter @Setter
    String filename;

    @Getter @Setter
    boolean archive;
}
