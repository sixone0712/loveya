package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpCompatDownloadRequest;
import lombok.Getter;
import lombok.Setter;

public class VFtpCompatDownloadRequestResponse {
    @Getter @Setter
    int errorCode;

    @Getter @Setter
    String errorMessage;

    @Getter @Setter
    VFtpCompatDownloadRequest request;
}
