package jp.co.canon.ckbs.eec.fs.manage.service;

import jp.co.canon.ckbs.eec.fs.collect.FileServiceCollectConnector;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpCompatDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpListRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpSssDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssDownloadRequest;
import jp.co.canon.ckbs.eec.fs.manage.service.configuration.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class VFtpFileService {
    @Autowired
    ConfigurationService configurationService;

    @PostConstruct
    void postConstruct(){

    }

    public VFtpListRequestResponse createVFtpSssListRequest(String machine, String directory) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            throw new FileServiceManageException("", "unknown machine name");
        }

        FileServiceCollectConnector connector = new FileServiceCollectConnector(host);
        VFtpListRequestResponse res = connector.createVFtpSssListRequest(machine, directory);
        return res;
    }

    public VFtpListRequestResponse getVFtpSssListRequest(String machine, String requestNo) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            throw new FileServiceManageException("", "unknown machine name");
        }

        FileServiceCollectConnector connector = new FileServiceCollectConnector(host);
        VFtpListRequestResponse res = connector.getVFtpSssListRequest(machine, requestNo);
        return res;
    }

    public void cancelAndDeleteVFtpSssListRequest(String machine, String requestNo) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            throw new FileServiceManageException("", "unknown machine name");
        }

        FileServiceCollectConnector connector = new FileServiceCollectConnector(host);
        connector.cancelAndDeleteVFtpSssListRequest(machine, requestNo);
    }

    public VFtpSssDownloadRequestResponse createVFtpSssDownloadRequest(String machine, String directory, String[] fileList, boolean archive) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            throw new FileServiceManageException("", "unknown machine name");
        }

        FileServiceCollectConnector connector = new FileServiceCollectConnector(host);
        VFtpSssDownloadRequestResponse res = connector.createVFtpSssDownloadRequest(machine, directory, fileList, archive);
        return res;
    }

    public VFtpSssDownloadRequestResponse getVFtpSssDownloadRequest(String machine, String requestNo) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            throw new FileServiceManageException("", "unknown machine name");
        }

        FileServiceCollectConnector connector = new FileServiceCollectConnector(host);
        VFtpSssDownloadRequestResponse res = connector.getVFtpSssDownloadRequest(machine, requestNo);
        return res;
    }

    public void cancelAndDeleteVFtpSssDownloadRequest(String machine, String requestNo) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            throw new FileServiceManageException("", "unknown machine name");
        }

        FileServiceCollectConnector connector = new FileServiceCollectConnector(host);
        connector.cancelAndDeleteVFtpSssDownloadRequest(machine, requestNo);
    }

    public VFtpCompatDownloadRequestResponse createVFtpCompatDownloadRequest(String machine, String filename, boolean archive) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            throw new FileServiceManageException("", "unknown machine name");
        }

        FileServiceCollectConnector connector = new FileServiceCollectConnector(host);
        VFtpCompatDownloadRequestResponse res = connector.createVFtpCompatDownloadRequest(machine, filename, archive);
        return res;
    }

    public VFtpCompatDownloadRequestResponse getVFtpCompatDownloadRequest(String machine, String requestNo) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            throw new FileServiceManageException("", "unknown machine name");
        }

        FileServiceCollectConnector connector = new FileServiceCollectConnector(host);
        VFtpCompatDownloadRequestResponse res = connector.getVFtpCompatDownloadRequest(machine, requestNo);
        return res;
    }

    public void cancelAndDeleteVFtpCompatDownloadRequest(String machine, String requestNo) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            throw new FileServiceManageException("", "unknown machine name");
        }

        FileServiceCollectConnector connector = new FileServiceCollectConnector(host);
        connector.cancelAndDeleteVFtpCompatDownloadRequest(machine, requestNo);
    }
}
