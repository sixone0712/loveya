package jp.co.canon.ckbs.eec.fs.collect.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VFtpCompatDownloadRequestTest {
    @Test
    void test_001(){
        VFtpCompatDownloadRequest request = new VFtpCompatDownloadRequest();
        request.setArchive(false);
        Assertions.assertFalse(request.isArchive());

        request.setArchiveFileName("abc.zip");
        Assertions.assertEquals("abc.zip", request.getArchiveFileName());

        request.setArchiveFilePath("aaa/abc.zip");
        Assertions.assertEquals("aaa/abc.zip", request.getArchiveFilePath());

        request.setFile(new RequestFileInfo("asdf.txt"));
        Assertions.assertEquals("asdf.txt", request.getFile().getName());

        request.downloaded("aaa.txt", 100);
        request.downloaded("asdf.txt", 200);
    }

    @Test
    void test_002(){
        VFtpCompatDownloadRequest request = new VFtpCompatDownloadRequest();
        request.setArchive(true);
        Assertions.assertTrue(request.isArchive());

        request.setArchiveFileName("abc.zip");
        Assertions.assertEquals("abc.zip", request.getArchiveFileName());

        request.setArchiveFilePath("aaa/abc.zip");
        Assertions.assertEquals("aaa/abc.zip", request.getArchiveFilePath());

        request.setFile(new RequestFileInfo("asdf.txt"));
        Assertions.assertEquals("asdf.txt", request.getFile().getName());

        request.downloaded("aaa.txt", 100);
        request.downloaded("asdf.txt", 200);
    }
}
