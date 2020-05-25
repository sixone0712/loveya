package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServerInfoMapTest {
    @Test
    public void test_0001(){
        ServerInfoMap infoMap = new ServerInfoMap();

        boolean r;
        r = infoMap.exists("OTS1");
        Assertions.assertFalse(r);

        ServerInfo info = new ServerInfo();
        info.setName("OTS1");
        info.setHost("10.1.36.118");

        infoMap.add("OTS1", info);
        r = infoMap.exists("OTS1");
        Assertions.assertTrue(r);

        infoMap.add("OTS1", info);

        ServerInfo info2 = infoMap.getServerInfo("OTS1");
        Assertions.assertEquals(info, info2);
    }
}