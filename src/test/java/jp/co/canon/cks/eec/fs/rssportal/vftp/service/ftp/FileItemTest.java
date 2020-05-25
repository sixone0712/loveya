package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileItemTest {
    @Test
    public void test_getset(){
        FileItem item = new FileItem();

        item.setServer("ABC");
        String server = item.getServer();
        Assertions.assertEquals("ABC", server);

        item.setPath("/VROOT/COMPAT/Optional");
        String path = item.getPath();
        Assertions.assertEquals("/VROOT/COMPAT/Optional", path);

        item.setFilename("abcdefg.log");
        String filename = item.getFilename();
        Assertions.assertEquals("abcdefg.log", filename);

        item.setFilesize(100);
        long filesize = item.getFilesize();
        Assertions.assertEquals(100, filesize);
    }
}