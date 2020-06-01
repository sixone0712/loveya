package jp.co.canon.cks.eec.fs.rssportal.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RSSFileInfoBeanResponseTest {
    @Test
    public void test_getset(){
        RSSFileInfoBeanResponse bean = new RSSFileInfoBeanResponse();
        
        bean.setStructId("structId");
        Assertions.assertEquals("structId", bean.getStructId());
        
        bean.setTargetName("targetName");
        Assertions.assertEquals("targetName", bean.getTargetName());

        bean.setLogName("logName");
        Assertions.assertEquals("logName", bean.getLogName());

        bean.setFileId(111);
        bean.setFileStatus("status1");
        bean.setLogId("logid1");
        bean.setFileName("filename1");
        bean.setFileSize(1024);
        bean.setFileDate("202006010011");
        bean.setFilePath("/AAA/BBB");
        bean.setFile(true);

        String str = bean.toString();
        Assertions.assertNotNull(str);
    }
}