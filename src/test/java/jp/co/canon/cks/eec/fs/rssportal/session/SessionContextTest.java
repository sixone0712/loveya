package jp.co.canon.cks.eec.fs.rssportal.session;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;

public class SessionContextTest {
    @Test
    public void test_getset(){
        SessionContext ctx = new SessionContext();

        UserVo user = new UserVo();

        ctx.setDesc("desc");
        Assertions.assertEquals("desc", ctx.getDesc());

        ctx.setAuthorized(true);
        Assertions.assertEquals(true, ctx.isAuthorized());

        ctx.setUser(user);
        Assertions.assertEquals(user, ctx.getUser());
    }
}