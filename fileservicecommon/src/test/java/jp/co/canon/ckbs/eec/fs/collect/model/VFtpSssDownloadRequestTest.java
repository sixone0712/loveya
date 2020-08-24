package jp.co.canon.ckbs.eec.fs.collect.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VFtpSssDownloadRequestTest {
    @Test
    void test_001(){
        VFtpSssDownloadRequest request = new VFtpSssDownloadRequest();

        request.setDirectory("IP_AS_RAW");
        Assertions.assertEquals("IP_AS_RAW", request.getDirectory());

        request.setArchive(false);
        Assertions.assertFalse(request.isArchive());

        request.setArchiveFileName("ABC.zip");
        Assertions.assertEquals("ABC.zip", request.getArchiveFileName());

        request.setArchiveFilePath("ABC/ABC.zip");
        Assertions.assertEquals("ABC/ABC.zip", request.getArchiveFilePath());

        RequestFileInfo[] infos = new RequestFileInfo[]{new RequestFileInfo("asdf.txt")};
        request.setFileList(infos);
        Assertions.assertArrayEquals(infos, request.getFileList());

        request.downloaded("aaa.txt", 100);
        request.downloaded("asdf.txt", 200);
    }

    @Test
    void test_002(){
        VFtpSssDownloadRequest request = new VFtpSssDownloadRequest();

        request.setDirectory("IP_AS_RAW");
        Assertions.assertEquals("IP_AS_RAW", request.getDirectory());

        request.setArchive(true);
        Assertions.assertTrue(request.isArchive());

        request.setArchiveFileName("ABC.zip");
        Assertions.assertEquals("ABC.zip", request.getArchiveFileName());

        request.setArchiveFilePath("ABC/ABC.zip");
        Assertions.assertEquals("ABC/ABC.zip", request.getArchiveFilePath());

        RequestFileInfo[] infos = new RequestFileInfo[]{new RequestFileInfo("asdf.txt")};
        request.setFileList(infos);
        Assertions.assertArrayEquals(infos, request.getFileList());

        request.downloaded("aaa.txt", 100);
        request.downloaded("asdf.txt", 200);
    }
}
