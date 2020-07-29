package jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc;

import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloadContext;

public interface FileDownloadHandler {

    String createDownloadRequest();
    void cancelDownloadRequest(String requestNo);
    FileDownloadInfo getDownloadedFiles(String requestNo);
    String getFtpAddress(String requestNo);

}
