package jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc;

import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloadContext;

public interface FileDownloadHandler {

    String createDownloadRequest();
    void cancelDownloadRequest();
    FileDownloadInfo getDownloadedFiles();
    String getFtpAddress();

}
