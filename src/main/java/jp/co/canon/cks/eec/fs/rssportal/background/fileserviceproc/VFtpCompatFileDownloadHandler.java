package jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpCompatDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpCompatDownloadRequest;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnector;

public class VFtpCompatFileDownloadHandler implements FileDownloadHandler {

    private final FileServiceManageConnector connector;
    private final String machine;
    private final String command;
    private boolean achieve;

    private String request;

    public VFtpCompatFileDownloadHandler(FileServiceManageConnector connector, String machine, String command) {
        this.connector = connector;
        this.machine = machine;
        this.command = command;
        achieve = true;
    }

    @Override
    public String createDownloadRequest() {
        VFtpCompatDownloadRequestResponse response =
                connector.createVFtpCompatDownloadRequest(machine, command, achieve);
        if(response.getErrorMessage()!=null) {
            return null;
        }
        request = response.getRequest().getRequestNo();
        return request;
    }

    @Override
    public void cancelDownloadRequest() {
        if(request!=null)
            connector.cancelAndDeleteVFtpCompatDownloadRequest(machine, request);
    }

    @Override
    public FileDownloadInfo getDownloadedFiles() {
        if(request==null)
            return null;

        VFtpCompatDownloadRequest download = getDownloadRequest();
        if(download==null)
            return null;

        FileDownloadInfo info = new FileDownloadInfo();
        info.setRequestFiles(1);
        info.setRequestBytes(0);
        VFtpCompatDownloadRequest.Status status = download.getStatus();

        if(status==VFtpCompatDownloadRequest.Status.ERROR) {
            info.setError(true);
        } else if(status==VFtpCompatDownloadRequest.Status.EXECUTED) {
            info.setDownloadFiles(1);
            info.setDownloadBytes(download.getFile().getSize());
            info.setFinish(true);
        }
        return info;
    }

    @Override
    public String getFtpAddress() {
        if(request==null)
            return null;
        VFtpCompatDownloadRequest download = getDownloadRequest();
        if(download==null || download.getStatus()!= VFtpCompatDownloadRequest.Status.EXECUTED)
            return null;
        return download.getArchiveFilePath();
    }

    private VFtpCompatDownloadRequest getDownloadRequest() {
        VFtpCompatDownloadRequestResponse response = connector.getVFtpCompatDownloadRequest(machine, request);
        if(response.getErrorMessage()!=null) {
            return null;
        }
        return response.getRequest();
    }
}
