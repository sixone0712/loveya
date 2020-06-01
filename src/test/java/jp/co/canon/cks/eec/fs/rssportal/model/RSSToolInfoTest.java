package jp.co.canon.cks.eec.fs.rssportal.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RSSToolInfoTest {
    @Test
    public void test_getset(){
        RSSToolInfo info = new RSSToolInfo();

        info.setStructId("structId");
        Assertions.assertEquals("structId", info.getStructId());

        info.setTargetname("targetname");
        Assertions.assertEquals("targetname", info.getTargetname());

        info.setTargettype("targettype");
        Assertions.assertEquals("targettype", info.getTargettype());

        info.setCollectServerId("1");
        Assertions.assertEquals("1", info.getCollectServerId());

        info.setCollectHostName("collectHostName");
        Assertions.assertEquals("collectHostName", info.getCollectHostName());

        String str = info.toString();
        Assertions.assertNotNull(str);
    }
}