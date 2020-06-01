package jp.co.canon.cks.eec.fs.rssportal.vo;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommandVoTest {
    @Test
    public void test_getset(){
        CommandVo vo = new CommandVo();

        vo.setId(1011);
        Assertions.assertEquals(1011, vo.getId());

        vo.setCmd_name("AABB");
        Assertions.assertEquals("AABB", vo.getCmd_name());

        vo.setCmd_type("CCDD");
        Assertions.assertEquals("CCDD", vo.getCmd_type());

        Date dt = Calendar.getInstance().getTime();
        vo.setCreated(dt);
        Assertions.assertEquals(dt, vo.getCreated());

        vo.setModified(dt);
        Assertions.assertEquals(dt, vo.getModified());

        vo.setValidity(true);
        Assertions.assertEquals(true, vo.isValidity());

        String str = vo.toString();
        Assertions.assertNotNull(str);
    }
}