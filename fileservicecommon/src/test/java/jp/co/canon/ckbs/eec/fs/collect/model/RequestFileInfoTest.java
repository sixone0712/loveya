package jp.co.canon.ckbs.eec.fs.collect.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RequestFileInfoTest {
    @Test
    void test_001(){
        RequestFileInfo info = new RequestFileInfo();
        Assertions.assertNotNull(info);
    }

    @Test
    void test_002(){
        RequestFileInfo info = new RequestFileInfo("12345D/abc.txt");
        Assertions.assertEquals("abc.txt", info.netFileName());

        info.setSize(12345);
        Assertions.assertEquals(12345, info.getSize());

        info.setDownloadPath("AAA/abc.zip");
        Assertions.assertEquals("AAA/abc.zip", info.getDownloadPath());
    }
}
