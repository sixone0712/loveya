package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.list;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ListSubRequestTest {
    public void privateFunctionAccessSample() {
        ListSubRequest req = new ListSubRequest("AAA", "/VROOT/SSS/Optional", "AAAAA");
        Method method;

        try {
            method = req.getClass().getDeclaredMethod("getToken", String.class);
            method.setAccessible(true);

            try {
                String[] r = null;
                r = (String[])method.invoke(req.getClass(), "labcdef -> aaa");
                
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
    public void test_getset(){
        ListSubRequest request = new ListSubRequest("AAA", "/VROOT/SSS/Optional", "AAAAA");
        String serverName = request.getServerName();
        String path = request.getPath();
        String directory = request.getDirectory();

        Assertions.assertEquals("AAA", serverName);
        Assertions.assertEquals("/VROOT/SSS/Optional", path);
        Assertions.assertEquals("AAAAA", directory);
    }
}