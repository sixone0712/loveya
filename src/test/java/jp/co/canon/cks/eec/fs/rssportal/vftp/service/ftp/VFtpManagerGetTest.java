package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import jp.co.canon.cks.eec.fs.rssportal.vftp.FileDownloadStatus;
import jp.co.canon.cks.eec.fs.rssportal.vftp.VFtpManager;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get.GetFileParamList;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("vftptest3")
public class VFtpManagerGetTest {
    @Autowired
    VFtpManager manager;

    @Test
    public void test_001() {
        String requestNo;

        GetFileParamList list = new GetFileParamList();
        list.add("OTS01_FS", "/VROOT/SSS/Optional/AAAA", "abcdefg.txt");
        requestNo = manager.requestDownload(list.toArray(), "a.zip");
        Assertions.assertNotNull(requestNo);

        FileDownloadStatus sts = manager.requestDownloadStatus(requestNo);

        while (sts.getStatus() != FileDownloadStatus.Status.COMPLETED
                && sts.getStatus() != FileDownloadStatus.Status.FAILED) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            sts = manager.requestDownloadStatus(requestNo);
        }
    }
}