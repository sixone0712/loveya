package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssDownloadRequest;
import lombok.Getter;
import lombok.Setter;

public class VFtpSssDownloadRequestResponse {
    @Getter @Setter
    int errorCode;

    @Getter @Setter
    String errorMessage;

    @Getter @Setter
    VFtpSssDownloadRequest request;
}
