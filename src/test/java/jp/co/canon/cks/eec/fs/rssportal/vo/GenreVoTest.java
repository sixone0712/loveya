package jp.co.canon.cks.eec.fs.rssportal.vo;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GenreVoTest {
    @Test
    public void test_getset(){
        GenreVo vo = new GenreVo();

        vo.setId(1234);
        Assertions.assertEquals(1234, vo.getId());

        vo.setName("11223344");
        Assertions.assertEquals("11223344", vo.getName());

        vo.setCategory("CAT01");
        Assertions.assertEquals("CAT01", vo.getCategory());

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