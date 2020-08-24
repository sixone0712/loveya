package jp.co.canon.ckbs.eec.fs.collect.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileServiceCollectExceptionTest {
    @Test
    void test_001(){
        FileServiceCollectException e = new FileServiceCollectException(500, "Server Error");
        Assertions.assertEquals(500, e.getCode());
        Assertions.assertEquals("Server Error", e.getMessage());
    }
}
