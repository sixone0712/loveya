package jp.co.canon.ckbs.eec.fs.collect.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FtpRequestTest {
    @Test
    void test_001(){
        FtpRequest request = new FtpRequest();
        request.setStatus(FtpRequest.Status.CANCEL);
        Assertions.assertEquals(FtpRequest.Status.CANCEL, request.getStatus());
        request.setStatus(FtpRequest.Status.EXECUTING);
        Assertions.assertEquals(FtpRequest.Status.CANCEL, request.getStatus());

    }
}
