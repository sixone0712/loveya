package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jp.co.canon.cks.eec.util.ftp.FTP;

public class SubRequestListTest {
    @Test
    public void test_001(){
        SubRequestList requestList = new SubRequestList();
        SubRequest subRequest = new SubRequest(){
        
            @Override
            public void processRequest(FtpWorker worker, FTP ftp) throws Exception {
                // TODO Auto-generated method stub
                
            }
        };

        requestList.addReady(subRequest);

        subRequest = requestList.readyToProgress();
        Assertions.assertNotNull(subRequest);

        requestList.progressToCompleted(subRequest);

        subRequest = requestList.readyToProgress();
        Assertions.assertNull(subRequest);

        requestList.progressToCompleted(null);
    }
}