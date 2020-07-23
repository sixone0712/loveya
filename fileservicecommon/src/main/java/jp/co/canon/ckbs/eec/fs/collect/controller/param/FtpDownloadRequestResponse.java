package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import lombok.Getter;
import lombok.Setter;

public class FtpDownloadRequestResponse extends FtpDownloadRequest{
    @Getter @Setter
    String errorCode;

    public static FtpDownloadRequestResponse fromRequest(FtpDownloadRequest req){
        FtpDownloadRequestResponse r = new FtpDownloadRequestResponse();

        r.setRequestNo(req.getRequestNo());
        r.setMachine(req.getMachine());
        r.setCategory(req.getCategory());
        r.setTimestamp(req.getTimestamp());
        r.setStatus(req.getStatus());
        r.setResult(req.getResult());
        r.setCompletedTime(req.getCompletedTime());
        r.setArchive(req.isArchive());
        r.setFileInfos(req.getFileInfos());
        r.setDirectory(req.getDirectory());
        r.setErrorMessage(req.getErrorMessage());
        r.setErrorCode(null);
        return r;
    }
}
