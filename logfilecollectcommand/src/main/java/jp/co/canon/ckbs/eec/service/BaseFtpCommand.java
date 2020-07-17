package jp.co.canon.ckbs.eec.service;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public abstract class BaseFtpCommand implements Command{
    String host;
    int port;
    String ftpmode;
    String user;
    String password;
/*
    FTPClient ftpClient;

    void init(){
        ftpClient = new FTPClient();
    }

    boolean connect(){
        try {
            System.out.println("STATUS:TRY_CONNECT");
            ftpClient.connect(host, port);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    boolean login(){
        try {
            System.out.println("STATUS:TRY_LOGIN");
            return ftpClient.login(user, password);
        } catch (IOException e) {
            return false;
        }
    }

    void setFtpmode(){
        if (!ftpmode.equalsIgnoreCase("active")){
            ftpClient.enterLocalPassiveMode();
        }
    }
 */

}
