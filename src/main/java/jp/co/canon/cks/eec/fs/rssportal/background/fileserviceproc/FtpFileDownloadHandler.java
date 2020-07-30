package jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestListResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.model.RequestFileInfo;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FtpFileDownloadHandler implements FileDownloadHandler {

    private Log log = LogFactory.getLog(getClass());
    private final FileServiceManageConnector connector;
    private final String machine;
    private final String category;
    private final String[] files;
    private boolean achieve;

    private String request;

    public FtpFileDownloadHandler(FileServiceManageConnector connector, String machine, String category, String[] files) {
        this.connector = connector;
        this.machine = machine;
        this.category = category;
        this.files = files;
        achieve = true;
    }

    @Override
    public String createDownloadRequest() {
        FtpDownloadRequestResponse response = connector.createFtpDownloadRequest(
                machine, category, achieve, files);
        if(response.getErrorCode()!=null) {
            log.error("createDownloadRequest: error  "+response.getErrorMessage());
            return null;
        }
        request = response.getRequestNo();
        return request;
    }

    @Override
    public void cancelDownloadRequest() {
        if(request!=null) {
            connector.cancelAndDeleteRequest(machine, request);
        }
    }

    @Override
    public FileDownloadInfo getDownloadedFiles() {
        if(request==null) {
            log.error("getDownloadedFiles: null request");
            return null;
        }
        FtpDownloadRequest download = getDownloadRequest();
        if(download!=null) {
            FileDownloadInfo info = new FileDownloadInfo();

            long totalSize = 0, downloadSize = 0;
            for(RequestFileInfo file: download.getFileInfos()) {
                totalSize += file.getSize();
                if(file.isDownloaded()) {
                    downloadSize += file.getSize();
                }
            }
            info.setRequestBytes(totalSize);
            info.setDownloadBytes(downloadSize);
            info.setRequestFiles(download.getTotalFileCount());
            info.setDownloadFiles(download.getDownloadedFileCount());
            log.info("### "+download.getDownloadedFileCount()+"/"+download.getTotalFileCount());

            if(download.getStatus()==FtpDownloadRequest.Status.ERROR) {
                log.error("getDownloadedFiles: error "+download.getErrorMessage());
                info.setError(true);
            } else if(download.getStatus()==FtpDownloadRequest.Status.EXECUTED) {
                info.setFinish(true);
            }
            return info;
        }
        return null;
    }

    @Override
    public String getFtpAddress() {
        if(request==null) {
            log.error("getFtpAddress: null request");
            return null;
        }
        FtpDownloadRequest download = getDownloadRequest();
        if(download!=null)
            return download.getArchiveFilePath();
        return null;
    }

    private FtpDownloadRequest getDownloadRequest() {
        if(request==null)
            return null;
        FtpDownloadRequestListResponse response = connector.getFtpDownloadRequestList(machine, request);
        if(response!=null) {
            if(response.getErrorCode()!=null) {
                log.error("getDownloadRequest: error  "+response.getErrorCode());
                return null;
            }
            for (FtpDownloadRequest download : response.getRequestList()) {
                if (download.getRequestNo().equals(request)) {
                    return download;
                }
            }
        }
        log.error("getDownloadRequest: failed to get response");
        return null;
    }
}
