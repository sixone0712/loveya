package jp.co.canon.ckbs.eec.fs.collect.model;

import jp.co.canon.ckbs.eec.fs.collect.service.VFtpFileInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VFtpSssListRequestTest {
    @Test
    void test_001(){
        VFtpSssListRequest request = new VFtpSssListRequest();

        request.setDirectory("IP_AS_RAW-2020");
        Assertions.assertEquals("IP_AS_RAW-2020", request.getDirectory());

        VFtpFileInfo info = new VFtpFileInfo();
        info.setFileName("20200000");
        Assertions.assertEquals("20200000", info.getFileName());
        info.setFileSize(500);
        Assertions.assertEquals(500, info.getFileSize());
        info.setFileType("F");
        Assertions.assertEquals("F", info.getFileType());

        VFtpFileInfo[] infoArr = new VFtpFileInfo[]{info};
        request.setFileList(infoArr);

        Assertions.assertArrayEquals(infoArr, request.getFileList());
    }
}
