package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import jp.co.canon.ckbs.eec.fs.collect.model.RequestFileInfo;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpCompatDownloadRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VFtpCompatDownloadRequestResponseTest {
    @Test
    void test_001(){
        VFtpCompatDownloadRequestResponse response = new VFtpCompatDownloadRequestResponse();
        response.setErrorCode(404);
        Assertions.assertEquals(404, response.getErrorCode());
        response.setErrorMessage("not found");
        Assertions.assertEquals("not found", response.getErrorMessage());

        VFtpCompatDownloadRequest req = new VFtpCompatDownloadRequest();
        req.setMachine("MPA_1");
        req.setFile(new RequestFileInfo("aaa.txt"));
        response.setRequest(req);

        Assertions.assertEquals(req, response.getRequest());
    }
}
