package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VFtpSssListRequestResponseTest {
    @Test
    void test_001(){
        VFtpSssListRequestResponse res = new VFtpSssListRequestResponse();
        res.setErrorCode(400);
        Assertions.assertEquals(400, res.getErrorCode());

        res.setErrorMessage("aaaaa");
        Assertions.assertEquals("aaaaa", res.getErrorMessage());

        VFtpSssListRequest req = new VFtpSssListRequest();
        res.setRequest(req);
        Assertions.assertEquals(req, res.getRequest());
    }
}
