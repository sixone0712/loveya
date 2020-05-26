package jp.co.canon.cks.eec.fs.rssportal.vftp.controller.checker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SSSCheckerTest {
    @Test
    public void test_SSSChecker_001(){
        SSSChecker checker = null;

        checker = SSSChecker.fromDirectoryName(null);
        Assertions.assertNull(checker);

        checker = SSSChecker.fromDirectoryName("IP_AS_RAW-20200525_111111-20200526_111111");
        Assertions.assertNotNull(checker);

        checker = SSSChecker.fromDirectoryName("IP_AS_RAW_ERR-20200525_111111-20200526_111111");
        Assertions.assertNotNull(checker);

        checker = SSSChecker.fromDirectoryName("IP_AS_BMP-20200525_111111-20200526_111111");
        Assertions.assertNotNull(checker);

        checker = SSSChecker.fromDirectoryName("IP_AS_BMP_ERR-20200525_111111-20200526_111111");
        Assertions.assertNotNull(checker);

        checker = SSSChecker.fromDirectoryName("IP_AS_RAW-20200525_111111-202111");
        Assertions.assertNull(checker);


        checker = SSSChecker.fromDirectoryName("IP_AS-20200525_111111-20200526_111111");
        Assertions.assertNull(checker);

        checker = SSSChecker.fromDirectoryName("IP_AS-20200525_111111");
        Assertions.assertNull(checker);

        checker = SSSChecker.fromDirectoryName("IP_AS_RAW-20200525_111111-20200526_111111-DE_ABC_DE_DEF");
        Assertions.assertNull(checker);

        checker = SSSChecker.fromDirectoryName("IP_AS_RAW-20200525_111111-20200526_111111-DE_ABC");
        Assertions.assertNotNull(checker);

        String name = checker.getDeviceName();
        Assertions.assertEquals("ABC", name);
    }
}