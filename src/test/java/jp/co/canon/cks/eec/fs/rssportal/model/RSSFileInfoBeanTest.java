package jp.co.canon.cks.eec.fs.rssportal.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RSSFileInfoBeanTest {
    @Test
    public void test_getset(){
        RSSFileInfoBean bean = new RSSFileInfoBean();

        bean.setFileId(111);
        Assertions.assertEquals(111, bean.getFileId());
        
        bean.setFileStatus("status1");
        Assertions.assertEquals("status1", bean.getFileStatus());

        bean.setLogId("logid1");
        Assertions.assertEquals("logid1", bean.getLogId());

        bean.setFileName("filename1");
        Assertions.assertEquals("filename1", bean.getFileName());

        bean.setFileSize(1024);
        Assertions.assertEquals(1024, bean.getFileSize());

        bean.setFileDate("202006010011");
        Assertions.assertEquals("202006010011", bean.getFileDate());

        bean.setFilePath("/AAA/BBB");
        Assertions.assertEquals("/AAA/BBB", bean.getFilePath());

        bean.setFile(true);
        Assertions.assertEquals(true, bean.isFile());

        Assertions.assertEquals(1, bean.getSizeKB());

        try {
            bean.compareTo("ABC");
        } catch(Exception e){

        }

        Assertions.assertEquals(0, bean.compareTo(bean));

        String str = bean.toString();
        Assertions.assertNotNull(str);
    }
}