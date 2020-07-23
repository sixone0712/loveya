package jp.co.canon.ckbs.eec.service;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public abstract class BaseFtpCommand implements Command{
    String host;
    int port;
    String ftpmode;
    String user;
    String password;
}
