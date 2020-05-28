package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.list;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jp.co.canon.cks.eec.fs.rssportal.vftp.FileListStatus;

public class ListRequestTest {
    @Test
    public void test_001() {
        ListRequest req = new ListRequest();
        req.stop();
    }

    @Test
    public void test_002() {
        Method method;
        ListRequest req = new ListRequest();
        FileListStatus sts;
        try {
            method = ListRequest.class.getDeclaredMethod("setStatus", ListRequest.Status.class);
            method.setAccessible(true);
            method.invoke(req, ListRequest.Status.NONE);

            sts = req.convertToFileListStatus();

            Assertions.assertEquals(FileListStatus.Status.NONE, sts.getStatus());

            method.invoke(req, ListRequest.Status.FAILED);

            sts = req.convertToFileListStatus();

            Assertions.assertEquals(FileListStatus.Status.FAILED, sts.getStatus());

        } catch (Exception e) {
            Assertions.fail();
        } 
    }

    @Test
    public void test_003() {
        Method method;
        ListRequest req = new ListRequest();
        try {
            method = ListRequest.class.getDeclaredMethod("notifyCompleted", String[].class);
            method.setAccessible(true);
            String[] strArr = new String[]{"error1", "error2"};
            method.invoke(req, (Object)strArr);
            Assertions.assertEquals(ListRequest.Status.FAILED, req.getStatus());            
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void test_004(){
        ListRequest req = new ListRequest();
        ListSubRequest subreq = new ListSubRequest("", "path", "directory");
        PropertyChangeEvent evt = new PropertyChangeEvent(subreq, "another", null, "another");
        req.propertyChange(evt);

        evt = new PropertyChangeEvent(subreq, "listresult", null, "another");
        req.propertyChange(evt);

        evt = new PropertyChangeEvent("another", "listresult", null, null);
        req.propertyChange(evt);
    }
}