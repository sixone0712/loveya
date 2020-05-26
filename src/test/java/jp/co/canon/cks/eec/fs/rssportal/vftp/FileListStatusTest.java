package jp.co.canon.cks.eec.fs.rssportal.vftp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileListStatusTest {
    @Test
    public void test_getset(){
        FileListStatus sts = new FileListStatus();

        sts.setRequestNo("request_list");
        Assertions.assertEquals("request_list", sts.getRequestNo());

        sts.setPath("/VROOT/SSS/Optional");
        Assertions.assertEquals("/VROOT/SSS/Optional", sts.getPath());
        
        Assertions.assertNull(sts.getFilelist());

        sts.setDirectory("AAABBBCCCDDD");
        Assertions.assertEquals("AAABBBCCCDDD", sts.getDirectory());

    }
}