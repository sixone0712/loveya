package jp.co.canon.ckbs.eec.fs.configuration.legacy.toolinfo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LogDataTest {
    @Test
    void test_001(){
        LogData logData = new LogData();
        logData.setKind("001");
        Assertions.assertEquals("001", logData.getKind());
        logData.setRef("02");
        Assertions.assertEquals("02", logData.getRef());
        logData.setPath("10.1.36.118");
        Assertions.assertEquals("10.1.36.118", logData.getPath());
    }
}
