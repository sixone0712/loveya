package jp.co.canon.cks.eec.fs.rssportal.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RSSLogInfoBeanTest {
    @Test
    public void test_getset(){
        RSSLogInfoBean bean = new RSSLogInfoBean();

        bean.setLogType(1);
        Assertions.assertEquals(1, bean.getLogType());

        bean.setFileListForwarding("fff");
        Assertions.assertEquals("fff", bean.getFileListForwarding());

        bean.setLogName("logName");
        Assertions.assertEquals("logName", bean.getLogName());

        bean.setCode("code");
        Assertions.assertEquals("code", bean.getCode());

        String str = bean.toString();
        Assertions.assertNotNull(str);
    }
}