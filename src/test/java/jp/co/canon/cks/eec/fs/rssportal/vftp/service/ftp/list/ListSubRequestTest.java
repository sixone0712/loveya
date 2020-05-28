package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.list;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.FileItem;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.FtpWorker;
import jp.co.canon.cks.eec.util.ftp.FTP;

public class ListSubRequestTest {
    public void privateFunctionAccessSample() {
        ListSubRequest req = new ListSubRequest("AAA", "/VROOT/SSS/Optional", "AAAAA");
        Method method;

        try {
            method = req.getClass().getDeclaredMethod("getToken", String.class);
            method.setAccessible(true);

            try {
                String[] r = null;
                r = (String[]) method.invoke(req.getClass(), "labcdef -> aaa");

            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_getset() {
        ListSubRequest request = new ListSubRequest("AAA", "/VROOT/SSS/Optional", "AAAAA");
        String serverName = request.getServerName();
        String path = request.getPath();
        String directory = request.getDirectory();

        Assertions.assertEquals("AAA", serverName);
        Assertions.assertEquals("/VROOT/SSS/Optional", path);
        Assertions.assertEquals("AAAAA", directory);
    }

    @Test
    public void test_processRequestException() {
        FTP ftp = new FTP("10.1.36.118", 22001);
        ListSubRequest request = new ListSubRequest("AAA", "/VROOT/SSS/Optional", "AAAAA");
        FtpWorker nullWorker = new FtpWorker(null, null);

        try {
            request.processRequest(nullWorker, ftp);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_workerStopped() {
        FTP ftp = new FTP("10.1.36.118", 22001);
        ListSubRequest request = new ListSubRequest("AAA", "/VROOT/SSS/Optional", "AAAAA");
        FtpWorker nullWorker = new FtpWorker(null, null);
        nullWorker.stopWorker();
        try {
            request.processRequest(nullWorker, ftp);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_emptyDirectory() {
        FTP ftp = new FTP("10.1.36.118", 22001);
        ListSubRequest request = new ListSubRequest("AAA", "/VROOT/SSS/Optional", null);
        FtpWorker nullWorker = new FtpWorker(null, null);
        nullWorker.stopWorker();
        try {
            ftp.connect();
            ftp.login("trkang", "123456");
            request.processRequest(nullWorker, ftp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ftp.close();
    }

    @Test
    public void test_getToken() {
        Method method;
        try {
            method = ListSubRequest.class.getDeclaredMethod("getToken", String.class);
            method.setAccessible(true);

            String[] r = null;
            r = (String[]) method.invoke(ListSubRequest.class, "l b c d e f g h li j k");
            r = (String[]) method.invoke(ListSubRequest.class, "l b c d e f g h laaa -> jk");
            r = (String[]) method.invoke(ListSubRequest.class, "l b c d e f g  ");
    
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_createFileItemFromLsLine(){
        Method method;
        try {
            ListSubRequest req = new ListSubRequest("AAA", "/VROOT/SSS/Optional", "AAAAA");
            method = ListSubRequest.class.getDeclaredMethod("createFileItemFromLsLine", String.class);
            method.setAccessible(true);

            FileItem item = null;
            item = (FileItem) method.invoke(req, "a b c");
    
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}