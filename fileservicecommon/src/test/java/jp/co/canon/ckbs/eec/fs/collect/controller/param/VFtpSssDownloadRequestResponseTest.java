package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssDownloadRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VFtpSssDownloadRequestResponseTest {
    @Test
    void test_001(){
        VFtpSssDownloadRequestResponse res = new VFtpSssDownloadRequestResponse();
        res.setErrorCode(100);
        Assertions.assertEquals(100, res.getErrorCode());
        res.setErrorMessage("abcde");
        Assertions.assertEquals("abcde", res.getErrorMessage());

        VFtpSssDownloadRequest req = new VFtpSssDownloadRequest();
        res.setRequest(req);

        Assertions.assertEquals(req, res.getRequest());
    }
}
