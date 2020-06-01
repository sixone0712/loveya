package jp.co.canon.cks.eec.fs.rssportal.model;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RSSFileInfoBeanListTest {
    @Test
    public void test_getset(){
        RSSFileInfoBeanList list = new RSSFileInfoBeanList();

        list.setTotalCnt(10);
        Assertions.assertEquals(10, list.getTotalCnt());

        ArrayList<RSSFileInfoBean> alist = new ArrayList<>();
        RSSFileInfoBean bean = new RSSFileInfoBean();

        bean.setFileId(111);
        bean.setFileStatus("status1");
        bean.setLogId("logid1");
        bean.setFileName("filename1");
        bean.setFileSize(1024);
        bean.setFileDate("202006010011");
        bean.setFilePath("/AAA/BBB");
        bean.setFile(true);

        alist.add(bean);
        list.setFileList(alist);

        Assertions.assertEquals(alist, list.getFileList());

        String str = list.toString();
        Assertions.assertNotNull(str);
        
    }
}