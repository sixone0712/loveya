package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import org.junit.jupiter.api.Test;

import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.list.ListSubRequest;
import jp.co.canon.cks.eec.util.ftp.FTP;
import jp.co.canon.cks.eec.util.ftp.FTPException;

public class FtpWorkerTest {
    @Test
    public void test_001() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setName("OTS_01");
        serverInfo.setHost("10.1.36.118");
        serverInfo.setPort(22001);
        serverInfo.setUsername("trkang");
        serverInfo.setPassword("123456");

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
    }

    @Test
    public void test_002() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setName("OTS_01");
        serverInfo.setHost("10.1.36.118");
        serverInfo.setPort(22000);
        serverInfo.setUsername("trkang");
        serverInfo.setPassword("123456");

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
    }

    @Test
    public void test_003() {
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
    }

    @Test
    public void test_004() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setName("OTS_01");
        serverInfo.setHost("10.1.36.118");
        serverInfo.setPort(22001);
        serverInfo.setUsername("trkang");
        serverInfo.setPassword("123456");

        SubRequestList list = new SubRequestList();

        list.addReady(new SubRequest() {
            @Override
            public void processRequest(FtpWorker worker, FTP ftp) throws Exception {
                throw new FTPException("ftp error~~~.");
            }
        });

        FtpWorker worker = new FtpWorker(list, serverInfo);
        worker.start();

        try {
            worker.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}