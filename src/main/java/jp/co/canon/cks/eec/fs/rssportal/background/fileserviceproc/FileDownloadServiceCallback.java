package jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc;

@FunctionalInterface
public interface FileDownloadServiceCallback {
    void call(FileDownloadServiceProc process);
}
