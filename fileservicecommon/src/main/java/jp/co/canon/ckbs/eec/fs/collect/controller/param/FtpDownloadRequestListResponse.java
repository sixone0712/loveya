package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import lombok.Getter;
import lombok.Setter;

public class FtpDownloadRequestListResponse {
    @Getter @Setter
    FtpDownloadRequest[] requestList;

    @Getter @Setter
    String errorCode;

    @Getter @Setter
    String errorMessage;

}
