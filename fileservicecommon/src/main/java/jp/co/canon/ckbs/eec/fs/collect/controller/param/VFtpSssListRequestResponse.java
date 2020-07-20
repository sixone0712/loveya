package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;
import lombok.Getter;
import lombok.Setter;

public class VFtpSssListRequestResponse extends VFtpSssListRequest {
    @Getter @Setter
    String errorCode;

    @Getter @Setter
    String errorMessage;

    public void fromRequest(VFtpSssListRequest request){
        this.setRequestNo(request.getRequestNo());
        this.setMachine(request.getMachine());
        this.setDirectory(request.getDirectory());
        this.setTimestamp(request.getTimestamp());
        this.setCompletedTime(request.getCompletedTime());
        this.setFileList(request.getFileList());
        this.setStatus(request.getStatus());
    }
}
