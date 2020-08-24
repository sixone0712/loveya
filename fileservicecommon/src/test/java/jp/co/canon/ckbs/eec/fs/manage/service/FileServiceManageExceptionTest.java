package jp.co.canon.ckbs.eec.fs.manage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileServiceManageExceptionTest {
    @Test
    void test_001(){
        FileServiceManageException e = new FileServiceManageException(404, "not found");
        Assertions.assertEquals(404, e.getCode());
        Assertions.assertEquals("not found", e.getMessage());
    }
}
