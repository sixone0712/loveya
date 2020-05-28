package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jp.co.canon.cks.eec.fs.rssportal.vftp.FileDownloadStatus;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.ServerInfo;

public class GetRequestTest {

    @Test
    public void test_001(){
        GetRequest request = new GetRequest();

        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setName("OTS01");
        serverInfo.setHost("10.1.36.118");
        serverInfo.setPort(22001);
        serverInfo.setUsername("trkang");
        serverInfo.setPassword("123456");

        request.add(serverInfo, "/VROOT/SSS/Optional/AAAA", "abcdefg.txt");
        request.add(serverInfo, "/VROOT/SSS/Optional/AAAA", "abcdef.txt");
        request.execute();


        FileDownloadStatus sts = request.convertToFileDownloadStatus();
        while(sts.getStatus() != FileDownloadStatus.Status.COMPLETED && sts.getStatus() != FileDownloadStatus.Status.FAILED){
            sts = request.convertToFileDownloadStatus();
        }
        request.stop();
    }

    @Test
    public void test_002(){
        Method method;
        GetRequest req = new GetRequest();
        FileDownloadStatus sts;
        try {
            method = GetRequest.class.getDeclaredMethod("setStatus", GetRequest.Status.class);
            method.setAccessible(true);
            method.invoke(req, GetRequest.Status.NONE);

            sts = req.convertToFileDownloadStatus();

            Assertions.assertEquals(FileDownloadStatus.Status.NONE, sts.getStatus());

            method.invoke(req, GetRequest.Status.FAILED);

            sts = req.convertToFileDownloadStatus();

            Assertions.assertEquals(FileDownloadStatus.Status.FAILED, sts.getStatus());

        } catch (Exception e) {
            Assertions.fail();
        } 
    }
}