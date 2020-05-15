package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import org.junit.jupiter.api.Test;

import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.list.ListSubRequest;

public class FtpWorkerTest {
    @Test
    public void test_001() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setName("OTS_01");
        serverInfo.setHost("10.1.36.118");
        serverInfo.setPort(22001);
        serverInfo.setUsername("trkang");
        serverInfo.setPassword("1234");

        SubRequestList list = new SubRequestList();

        list.addReady(new ListSubRequest("OTS_01", "/VROOT/SSS/Optional", "AAAA"));

        FtpWorker worker = new FtpWorker(list, serverInfo);
        worker.start();

        try {
            worker.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Test complete");
    }
}