package jp.co.canon.ckbs.eec.service;

public abstract class BaseFtpCommand implements Command{
    String host;
    int port;
    String ftpmode;
    String user;
    String password;
}
