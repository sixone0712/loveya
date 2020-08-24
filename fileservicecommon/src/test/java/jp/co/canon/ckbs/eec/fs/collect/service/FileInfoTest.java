package jp.co.canon.ckbs.eec.fs.collect.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileInfoTest {
    @Test
    void test_001(){
        FileInfo info = new FileInfo();
        info.setType("F");
        Assertions.assertEquals("F", info.getType());
        info.setFilename("abc.txt");
        Assertions.assertEquals("abc.txt", info.getFilename());
        info.setTimestamp("11223344");
        Assertions.assertEquals("11223344", info.getTimestamp());
        info.setSize(123456);
        Assertions.assertEquals(123456, info.getSize());
    }
}
