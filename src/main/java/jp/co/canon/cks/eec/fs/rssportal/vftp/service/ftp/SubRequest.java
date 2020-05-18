package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import jp.co.canon.cks.eec.util.ftp.FTP;

public abstract class SubRequest {
    public abstract void processRequest(FTP ftp);
}