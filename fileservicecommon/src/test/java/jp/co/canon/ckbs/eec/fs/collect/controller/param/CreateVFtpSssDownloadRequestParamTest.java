package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CreateVFtpSssDownloadRequestParamTest {
    @Test
    void test_001(){
        CreateVFtpSssDownloadRequestParam param = new CreateVFtpSssDownloadRequestParam();
        param.setArchive(true);
        Assertions.assertTrue(param.isArchive());
        param.setDirectory("DIRECTORY");
        Assertions.assertEquals("DIRECTORY", param.getDirectory());

        String[] in = new String[0];
        param.setFileList(in);
        Assertions.assertArrayEquals(param.getFileList(), in);
    }
}
