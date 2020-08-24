package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CreateFtpDownloadRequestParamTest {
    @Test
    void test_001(){
        CreateFtpDownloadRequestParam param = new CreateFtpDownloadRequestParam();
        param.setArchive(true);
        Assertions.assertTrue(param.isArchive());

        param.setArchive(false);
        Assertions.assertFalse(param.isArchive());

        param.setCategory("001");
        Assertions.assertEquals("001", param.getCategory());

        param.setCategory("002");
        Assertions.assertEquals("002", param.getCategory());

        String[] in = new String[0];
        param.setFileList(in);
        String[] r = param.getFileList();
        Assertions.assertArrayEquals(in, r);
    }
}
