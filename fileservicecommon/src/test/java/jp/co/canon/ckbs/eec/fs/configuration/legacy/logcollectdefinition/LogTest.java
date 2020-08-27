package jp.co.canon.ckbs.eec.fs.configuration.legacy.logcollectdefinition;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LogTest {
    @Test
    void test_001(){
        Log log = new Log();
        log.setKind("kind");
        Assertions.assertEquals("kind", log.getKind());
        log.setUser("user");
        Assertions.assertEquals("user", log.getUser());
        log.setPassword("password");
        Assertions.assertEquals("password", log.getPassword());
        log.setFtpmode("passive");
        Assertions.assertEquals("passive", log.getFtpmode());
        log.setUrls("urls");
        Assertions.assertEquals("urls", log.getUrls());
    }
}
