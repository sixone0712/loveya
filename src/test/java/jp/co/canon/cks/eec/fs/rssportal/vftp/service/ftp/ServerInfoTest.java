package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServerInfoTest {
    @Test
    public void test_getset(){
        ServerInfo info = new ServerInfo();

        info.setName("OTS1");
        String name = info.getName();
        Assertions.assertEquals("OTS1", name);

        info.setHost("10.1.36.118");
        String host = info.getHost();
        Assertions.assertEquals("10.1.36.118", host);

        info.setPort(2222);
        int port = info.getPort();
        Assertions.assertEquals(2222, port);

        info.setUsername("sam");
        String username = info.getUsername();
        Assertions.assertEquals("sam", username);

        info.setPassword("1234");
        String password = info.getPassword();
        Assertions.assertEquals("1234", password);

        info.setFtpMode("passive");
        String ftpMode = info.getFtpMode();
        Assertions.assertEquals("passive", ftpMode);
    }

    @Test
    public void test_equals(){
        ServerInfo info1 = new ServerInfo();
        ServerInfo info2 = new ServerInfo();
        info1.setName("ABC");
        info2.setName("ABC2");
        Assertions.assertFalse(info1.equals(info2));

        info2.setName("ABC");
        info1.setHost("10.1.36.118");
        info2.setHost("10.1.36.116");
        Assertions.assertFalse(info1.equals(info2));

        info2.setHost("10.1.36.118");
        info1.setPort(22001);
        info2.setPort(21);
        Assertions.assertFalse(info1.equals(info2));

        info2.setPort(22001);
        info1.setUsername("trkang");
        info2.setUsername("sam");
        Assertions.assertFalse(info1.equals(info2));

        info2.setUsername("trkang");
        info1.setPassword("123456");
        info2.setPassword("111111");
        Assertions.assertFalse(info1.equals(info2));

        info2.setPassword("123456");
        Assertions.assertTrue(info1.equals(info2));
    }


}