package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import lombok.Getter;
import lombok.Setter;

public class CreateVFtpSssDownloadRequestParam {
    @Getter @Setter
    String directory;

    @Getter @Setter
    String[] fileList;

    @Getter @Setter
    boolean archive;
}
