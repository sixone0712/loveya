package jp.co.canon.cks.eec.fs.rssportal.model.vftp;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpSssListRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssDownloadRequest;
import lombok.Getter;
import lombok.Setter;

public class VFtpSssListRequestResponseExtends extends VFtpSssListRequestResponse {
    @Getter @Setter
    String fabName;
}
