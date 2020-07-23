package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import lombok.Getter;
import lombok.Setter;

public class CreateVFtpCompatDownloadRequestParam {
    @Getter @Setter
    String filename;

    @Getter @Setter
    boolean archive;
}
