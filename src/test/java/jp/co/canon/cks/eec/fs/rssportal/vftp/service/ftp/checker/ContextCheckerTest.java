package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.checker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jp.co.canon.cks.eec.fs.rssportal.vftp.controller.checker.ContextChecker;

public class ContextCheckerTest {
    @Test
    public void test_001(){
        ContextChecker checker = ContextChecker.fromString("DE_");
        Assertions.assertEquals(null, checker);
    }
}