package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CreateVFtpListRequestParamTest {
    @Test
    void test_001(){
        CreateVFtpListRequestParam param = new CreateVFtpListRequestParam();
        param.setDirectory("DIR");
        Assertions.assertEquals("DIR", param.getDirectory());
    }
}
