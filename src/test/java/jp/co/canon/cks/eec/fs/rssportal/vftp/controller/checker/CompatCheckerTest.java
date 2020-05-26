package jp.co.canon.cks.eec.fs.rssportal.vftp.controller.checker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CompatCheckerTest {
    @Test
    public void test_compatChecker_001(){
        CompatChecker checker = CompatChecker.fromFilename(null);
        Assertions.assertNull(checker);

        checker = CompatChecker.fromFilename("ABCDEFG");
        Assertions.assertNull(checker);

        checker = CompatChecker.fromFilename("20200525_112233-20200526112233.log");
        Assertions.assertNull(checker);

        checker = CompatChecker.fromFilename("20200525_112233-20200526_112233.log");
        Assertions.assertNotNull(checker);

        checker = CompatChecker.fromFilename("20200525_112233-20200526_112233-UI_CONS.log");
        Assertions.assertNotNull(checker);

        checker = CompatChecker.fromFilename("20200525_112233-20200526_112233-L_CONS.log");
        Assertions.assertNotNull(checker);

        checker = CompatChecker.fromFilename("20200525_112233-20200526_112233-DE_AAA.log");
        Assertions.assertNotNull(checker);

        checker = CompatChecker.fromFilename("20200525_112233-20200526_112233-UI_CONS-UI_CONS.log");
        Assertions.assertNull(checker);

        checker = CompatChecker.fromFilename("20200525_112233-20200526_112233-L_CONS-L_CONS.log");
        Assertions.assertNull(checker);

        checker = CompatChecker.fromFilename("20200525_112233-20200526_112233-DE_AAA-DE_AAA.log");
        Assertions.assertNull(checker);

        checker = CompatChecker.fromFilename("20200525_112233-20200526_112233-O_AAA.log");
        Assertions.assertNull(checker);

        checker = CompatChecker.fromFilename("20200525_112233.log");
        Assertions.assertNull(checker);

        checker = CompatChecker.fromFilename("20200525_112233-1-2-3-4-5.log");
        Assertions.assertNull(checker);
    }

    @Test
    public void test_compatChecker_002(){
        CompatChecker checker = CompatChecker.fromFilename("20200525_112233-20200526_112233-DE_AAA.log");
        Assertions.assertNotNull(checker);

        String name = checker.getDeviceName();
        Assertions.assertEquals("AAA", name);
    }
}