package jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpCompatDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpCompatDownloadRequest;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnector;
import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloadContext;

public class VFtpCompatFileDownloadHandler implements FileDownloadHandler {

    private final FileServiceManageConnector connector;
    private final String machine;
    private final String category;
    private boolean achieve;

    public VFtpCompatFileDownloadHandler(FileServiceManageConnector connector, String machine, String category) {
        this.connector = connector;
        this.machine = machine;
        this.category = category;
        achieve = true;
    }

    @Override
    public String createDownloadRequest() {
        VFtpCompatDownloadRequestResponse response =
                connector.createVFtpCompatDownloadRequest(machine, category, achieve);
        if(response.getErrorMessage()!=null) {
            return null;
        }
        return response.getRequest().getRequestNo();
    }

    @Override
    public void cancelDownloadRequest(String requestNo) {
        connector.cancelAndDeleteVFtpCompatDownloadRequest(machine, requestNo);
    }

    @Override
    public FileDownloadInfo getDownloadedFiles(String requestNo) {
        VFtpCompatDownloadRequest request = getDownloadRequest(requestNo);
        if(request==null) {
            return null;
        }

        FileDownloadInfo info = new FileDownloadInfo();
        info.setRequestFiles(1);
        info.setRequestBytes(0);
        VFtpCompatDownloadRequest.Status status = request.getStatus();

        if(status==VFtpCompatDownloadRequest.Status.ERROR) {
            info.setError(true);
        } else if(request.getFile().isDownloaded()) {
            info.setDownloadFiles(1);
            info.setDownloadBytes(request.getFile().getSize());
            info.setFinish(true);
        }
        return info;
    }

    @Override
    public String getFtpAddress(String requestNo) {
        VFtpCompatDownloadRequest request = getDownloadRequest(requestNo);
        if(request==null || request.getStatus()!= VFtpCompatDownloadRequest.Status.EXECUTED) {
            return null;
        }
        return request.getArchiveFilePath();
    }

    private VFtpCompatDownloadRequest getDownloadRequest(String requestNo) {
        VFtpCompatDownloadRequestResponse response = connector.getVFtpCompatDownloadRequest(machine, requestNo);
        if(response.getErrorMessage()!=null) {
            return null;
        }
        return response.getRequest();
    }
}
