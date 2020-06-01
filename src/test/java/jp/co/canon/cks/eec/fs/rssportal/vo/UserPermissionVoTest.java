package jp.co.canon.cks.eec.fs.rssportal.vo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserPermissionVoTest {
    @Test
    public void test_getset(){
        UserPermissionVo vo = new UserPermissionVo();

        vo.setId(191);
        Assertions.assertEquals(191, vo.getId());

        vo.setPermname("121");
        Assertions.assertEquals("121", vo.getPermname());

        vo.setValidity(true);
        Assertions.assertTrue(vo.isValidity());

        String voStr = vo.toString();
        Assertions.assertNotNull(voStr);
    }
}