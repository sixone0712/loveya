package jp.co.canon.cks.eec.fs.rssportal.ftptest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.SocketTimeoutException;
import java.util.List;

import org.junit.jupiter.api.Test;

import jp.co.canon.cks.eec.util.ftp.FTP;
import jp.co.canon.cks.eec.util.ftp.FTPException;

public class FtpTest {
    /*
    @Test
    public void test_001() {
        boolean connected = false;
        FTP ftp;
        ftp = new FTP("10.1.36.118", 22001);

        try {
            ftp.connect();
            connected = true;
        } catch (FTPException e) {
            // TODO Auto-generated catch block
            connected = false;
            e.printStackTrace();
        }

        ftp.close();

        assertEquals(true, connected);
    }

    @Test
    public void test_002() {
        FTP ftp;
        ftp = new FTP("10.1.36.118", 22001);

        try {
            ftp.connect();
        } catch (FTPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            ftp.login("trkang", "1234");
        } catch (SocketTimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FTPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            ftp.cd("/VROOT/COMPAT/Optional");
        } catch (SocketTimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FTPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ftp.close();
    }

    @Test
    public void test_003(){
        FTP ftp;
        ftp = new FTP("10.1.36.118", 21);

        try {
            ftp.connect();
            ftp.login("trkang", "123456");
            ftp.cd("/EQVM87/001");
            List<String> r = ftp.ls("");
            for(String s : r){
                System.out.println(s);
            }
            ftp.close();
        } catch (FTPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    */
}