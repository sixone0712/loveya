package jp.co.canon.ckbs.eec.service;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;

public class TestMain {
    public static void main(String[] args){
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect("10.1.31.242", 21);
            boolean rc = ftpClient.login("ckbs", "ckbs");

            ftpClient.changeWorkingDirectory("/LOG/002");
//            FTPFile[] ftpFiles = ftpClient.listDirectories();
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for(FTPFile f : ftpFiles){
                System.out.println(f.getName());
            }
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
