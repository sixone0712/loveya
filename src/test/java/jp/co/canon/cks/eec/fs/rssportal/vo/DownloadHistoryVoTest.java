package jp.co.canon.cks.eec.fs.rssportal.vo;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DownloadHistoryVoTest {
    @Test
    public void test_getset(){
        DownloadHistoryVo vo = new DownloadHistoryVo();

        vo.setId(11112);
        Assertions.assertEquals(11112, vo.getId());

        vo.setDl_user("user");
        Assertions.assertEquals("user", vo.getDl_user());

        Date dt = Calendar.getInstance().getTime();
        
        vo.setDl_date(dt);
        Assertions.assertEquals(dt, vo.getDl_date());

        vo.setDl_type("DLTYPE");
        Assertions.assertEquals("DLTYPE", vo.getDl_type());

        vo.setDl_filename("DLFILENAME");
        Assertions.assertEquals("DLFILENAME", vo.getDl_filename());

        vo.setDl_status("dlstatus");
        Assertions.assertEquals("dlstatus", vo.getDl_status());

        String str = vo.toString();
        Assertions.assertNotNull(str);
    }
}