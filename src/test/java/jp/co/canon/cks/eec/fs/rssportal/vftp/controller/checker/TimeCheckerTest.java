package jp.co.canon.cks.eec.fs.rssportal.vftp.controller.checker;

import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TimeCheckerTest {
    @Test
    void test_checkDateString_001(){
        Date rDate = TimeChecker.checkDateString("");
        Assertions.assertNull(rDate);

        rDate = TimeChecker.checkDateString("20200525_1122331111111");
        Assertions.assertNotNull(rDate);

        rDate = TimeChecker.checkDateString("202005251112231");
        Assertions.assertNull(rDate);
        
    }

    @Test
    void test_isValidFromTimeToTimeString_001(){
        boolean r;
        r = TimeChecker.isValidFromTimeToTimeString("", "");
        Assertions.assertFalse(r);

        r = TimeChecker.isValidFromTimeToTimeString("20200525_112233", "");
        Assertions.assertFalse(r);

        r = TimeChecker.isValidFromTimeToTimeString("20200525_112233", "20200525_112333");
        Assertions.assertTrue(r);
    }
}