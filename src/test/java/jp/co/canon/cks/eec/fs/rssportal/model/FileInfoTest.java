package jp.co.canon.cks.eec.fs.rssportal.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileInfoTest {
    @Test
    public void test_getset(){
        FileInfo info = new FileInfo("aaaa", 1024, "20200601");

        Assertions.assertEquals("aaaa", info.getName());
        Assertions.assertEquals(1024, info.getSize());
        Assertions.assertEquals("20200601", info.getDate());

        FileInfo info2 = new FileInfo("aaaaa", 10240, "20200602", 11223);
        Assertions.assertEquals(11223, info2.getMilliTime());
    }
}