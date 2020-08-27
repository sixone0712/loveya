package jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NetworkDLTest {
    @Test
    void test_001(){
        NetworkDL dl = new NetworkDL("user", "password", "prefix", "ftpmode");

        Assertions.assertEquals("user", dl.getUser());
        Assertions.assertEquals("password", dl.getPassword());
        Assertions.assertEquals("prefix", dl.getUrlPrefix());
        Assertions.assertEquals("passive", dl.getFtpmode());

        dl.setUser("user2");
        dl.setPassword("password2");
        dl.setUrlPrefix("prefix2");
        dl.setFtpmode("active");

        Assertions.assertEquals("user2", dl.getUser());
        Assertions.assertEquals("password2", dl.getPassword());
        Assertions.assertEquals("prefix2", dl.getUrlPrefix());
        Assertions.assertEquals("active", dl.getFtpmode());

        dl.setFtpmode(null);
        Assertions.assertEquals("passive", dl.getFtpmode());
    }
}
