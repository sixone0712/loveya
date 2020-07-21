package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;
import lombok.Getter;
import lombok.Setter;

public class VFtpSssListRequestResponse {
    @Getter @Setter
    int errorCode;

    @Getter @Setter
    String errorMessage;

    @Getter @Setter
    VFtpSssListRequest request;
}
