package jp.co.canon.cks.eec.fs.rssportal.vftp;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jp.co.canon.cks.eec.fs.rssportal.vftp.FileDownloadStatus.Status;

public class FileDownloadStatusTest {
    @Test
    public void test_getset(){
        FileDownloadStatus sts = new FileDownloadStatus();

        sts.setStatus(Status.PROCESSING_DOWNLOAD);
        Assertions.assertEquals(Status.PROCESSING_DOWNLOAD, sts.getStatus());

        sts.setRequestNo("request_001");
        Assertions.assertEquals("request_001", sts.getRequestNo());

        sts.setRequestFileCount(10);
        Assertions.assertEquals(10, sts.getRequestFileCount());

        sts.setDownloadFileCount(5);
        Assertions.assertEquals(5, sts.getDownloadFileCount());

        sts.setDownloadFileName("abcdefg.log");
        Assertions.assertEquals("abcdefg.log", sts.getDownloadFileName());

        sts.setDownloadPath("/VROOT");

        File f = sts.createDownloadFile();
        Assertions.assertNull(f);
    }

    @Test
    public void test_001(){
        FileDownloadStatus sts = new FileDownloadStatus();
        File f = sts.createDownloadFile();

        Assertions.assertNull(f);
    }

}