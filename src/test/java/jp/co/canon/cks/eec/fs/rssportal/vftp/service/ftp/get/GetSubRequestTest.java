package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get;

import org.junit.jupiter.api.Test;

import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.FtpWorker;
import jp.co.canon.cks.eec.util.ftp.FTP;

public class GetSubRequestTest {
    @Test
    public void test_processRequestException() {
        FTP ftp = new FTP("10.1.36.118", 22001);
        GetFileItemList fileList = new GetFileItemList();
        GetFileItem item = new GetFileItem("/VROOT/COMPAT/Optional", "1234567.log", ".");
        fileList.addReady(item);

        GetSubRequest request = new GetSubRequest("OTS01", fileList);
        FtpWorker nullWorker = new FtpWorker(null, null);
        try {
            request.processRequest(nullWorker, ftp);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_workerStop() {
        FTP ftp = new FTP("10.1.36.118", 22001);
        GetFileItemList fileList = new GetFileItemList();
        GetFileItem item = new GetFileItem("/VROOT/COMPAT/Optional", "1234567.log", ".");
        fileList.addReady(item);

        GetSubRequest request = new GetSubRequest("OTS01", fileList);
        FtpWorker nullWorker = new FtpWorker(null, null);
        nullWorker.stopWorker();
        try {
            request.processRequest(nullWorker, ftp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}