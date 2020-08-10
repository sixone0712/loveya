package jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpSssDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.RequestFileInfo;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssDownloadRequest;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnector;

public class VFtpSssFileDownloadHandler implements FileDownloadHandler {

    private final FileServiceManageConnector connector;
    private final String machine;
    private final String directory;
    private final String[] files;
    private boolean achieve;

    private String request;

    public VFtpSssFileDownloadHandler(FileServiceManageConnector connector, String machine, String directory, String[] files) {
        this.connector = connector;
        this.machine = machine;
        this.directory = directory;
        this.files = files;
        achieve = true;
    }

    @Override
    public String createDownloadRequest() {
        VFtpSssDownloadRequestResponse response = connector.createVFtpSssDownloadRequest(machine, directory, files, true);
        if(response.getErrorMessage()!=null)
            return null;
        request = response.getRequest().getRequestNo();
        return request;
    }

    @Override
    public void cancelDownloadRequest() {
        if(request!=null)
            connector.cancelAndDeleteVFtpSssDownloadRequest(machine, request);
    }

    @Override
    public FileDownloadInfo getDownloadedFiles() {
        if(request==null)
            return null;
        VFtpSssDownloadRequest download = getDownloadRequest();
        if(download!=null) {
            FileDownloadInfo info = new FileDownloadInfo();
            info.setRequestFiles(files.length);
            info.setRequestBytes(0);
            if(download.getStatus()==VFtpSssDownloadRequest.Status.ERROR) {
                info.setError(true);
            } else if(download.getStatus()==VFtpSssDownloadRequest.Status.EXECUTED) {
                info.setDownloadFiles(files.length);
                info.setDownloadBytes(0);
                info.setFinish(true);
            } else {
                long files=0, bytes=0;
                for(RequestFileInfo file: download.getFileList()) {
                    if(file.isDownloaded()) {
                        ++files;
                        bytes += file.getSize();
                    }
                }
                info.setDownloadFiles(files);
                info.setDownloadBytes(bytes);
            }
            return info;
        }
        return null;
    }

    @Override
    public String getFtpAddress() {
        if(request==null)
            return null;
        VFtpSssDownloadRequest download = getDownloadRequest();
        if(download!=null && download.getStatus()==VFtpSssDownloadRequest.Status.EXECUTED) {
            return download.getArchiveFilePath();
        }
        return null;
    }

    private VFtpSssDownloadRequest getDownloadRequest() {
        VFtpSssDownloadRequestResponse response = connector.getVFtpSssDownloadRequest(machine, request);
        if(response==null || response.getErrorMessage()!=null)
            return null;
        return response.getRequest();
    }
}
