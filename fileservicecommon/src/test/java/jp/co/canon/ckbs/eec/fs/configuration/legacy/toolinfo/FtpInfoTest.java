package jp.co.canon.ckbs.eec.fs.configuration.legacy.toolinfo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FtpInfoTest {
    @Test
    void test_001(){
        FtpInfo ftpInfo = new FtpInfo();
        ftpInfo.setId("1");
        Assertions.assertEquals("1", ftpInfo.getId());
        ftpInfo.setUser("root");
        Assertions.assertEquals("root", ftpInfo.getUser());
        ftpInfo.setPassword("password");
        Assertions.assertEquals("password", ftpInfo.getPassword());
        ftpInfo.setPort(22001);
        Assertions.assertEquals(22001, ftpInfo.getPort());

    }
}
