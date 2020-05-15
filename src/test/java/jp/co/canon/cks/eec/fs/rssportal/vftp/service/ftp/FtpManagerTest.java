package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jp.co.canon.cks.eec.fs.rssportal.vftp.FileDownloadStatus;
import jp.co.canon.cks.eec.fs.rssportal.vftp.FileListStatus;
import jp.co.canon.cks.eec.fs.rssportal.vftp.VFtpManager;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get.GetFileParamList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class FtpManagerTest {
    @Autowired
    VFtpManager manager;
    @Test
    public void test_001() {
        String requestNo;
        requestNo = manager.requestFileList(null, "/VROOT/SSS/Optional", "AAAA");
        if (requestNo != null) {
            FileListStatus sts;
            sts = manager.requestFileListStatus(requestNo);
            while (sts.getStatus() != FileListStatus.Status.COMPLETED
                    && sts.getStatus() != FileListStatus.Status.FAILED) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                sts = manager.requestFileListStatus(requestNo);
            }
        }
    }

    @Test
    public void test_002() {
        String requestNo;
        GetFileParamList list = new GetFileParamList();
        list.add("OTS_01", "/VROOT/SSS/Optional/AAAA", "aaa.txt");
        requestNo = manager.requestDownload(list.toArray(), "AAAA.zip");
        if (requestNo != null){
            FileDownloadStatus sts;
            sts = manager.requestDownloadStatus(requestNo);
            while(sts.getStatus() != FileDownloadStatus.Status.COMPLETED 
                    && sts.getStatus() != FileDownloadStatus.Status.FAILED) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e){

                }
                sts = manager.requestDownloadStatus(requestNo);
            }
        }        
    }
}