package jp.co.canon.cks.eec.fs.rssportal.vo;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserVoTest {
    @Test
    public void test_getset(){
        UserVo vo = new UserVo();

        vo.setId(112);
        Assertions.assertEquals(112, vo.getId());

        vo.setUsername("username");
        Assertions.assertEquals("username", vo.getUsername());

        vo.setPassword("password");
        Assertions.assertEquals("password", vo.getPassword());

        Date dt = Calendar.getInstance().getTime();

        vo.setCreated(dt);
        Assertions.assertEquals(dt, vo.getCreated());

        vo.setModified(dt);
        Assertions.assertEquals(dt, vo.getModified());

        vo.setLastAccess(dt);
        Assertions.assertEquals(dt, vo.getLastAccess());

        vo.setValidity(true);
        Assertions.assertEquals(true, vo.isValidity());

        vo.setPermissions("permissions");
        Assertions.assertEquals("permissions", vo.getPermissions());

        String str = vo.toString();
        Assertions.assertNotNull(str);
    }
}