package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CreateVFtpCompatDownloadRequestParamTest {
    @Test
    void test_001(){
        CreateVFtpCompatDownloadRequestParam param = new CreateVFtpCompatDownloadRequestParam();

        param.setArchive(true);
        param.setFilename("IP_AS_RAW");

        Assertions.assertTrue(param.isArchive());
        Assertions.assertEquals("IP_AS_RAW", param.getFilename());
    }
}
