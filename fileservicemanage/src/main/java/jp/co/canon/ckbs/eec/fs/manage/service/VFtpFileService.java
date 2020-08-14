package jp.co.canon.ckbs.eec.fs.manage.service;

import jp.co.canon.ckbs.eec.fs.collect.FileServiceCollectConnector;
import jp.co.canon.ckbs.eec.fs.collect.FileServiceCollectConnectorFactory;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpCompatDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpSssListRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpSssDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.RequestFileInfo;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpCompatDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssDownloadRequest;
import jp.co.canon.ckbs.eec.fs.manage.service.configuration.ConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class VFtpFileService {
    @Autowired
    ConfigurationService configurationService;

    @Autowired
    FileServiceCollectConnectorFactory connectorFactory;

    public VFtpSssListRequestResponse createVFtpSssListRequest(String machine, String directory) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            log.error("cannot find file service host for {}", machine);
            throw new FileServiceManageException(400, "Parameter(machine) is not valid.");
        }

        FileServiceCollectConnector connector = connectorFactory.getConnector(host);
        VFtpSssListRequestResponse res = connector.createVFtpSssListRequest(machine, directory);
        return res;
    }

    public VFtpSssListRequestResponse getVFtpSssListRequest(String machine, String requestNo) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            log.error("cannot find file service host for {}", machine);
            throw new FileServiceManageException(400, "Parameter(machine) is not valid.");
        }

        FileServiceCollectConnector connector = connectorFactory.getConnector(host);
        VFtpSssListRequestResponse res = connector.getVFtpSssListRequest(machine, requestNo);
        return res;
    }

    public void cancelAndDeleteVFtpSssListRequest(String machine, String requestNo) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            log.error("cannot find file service host for {}", machine);
            throw new FileServiceManageException(400, "Parameter(machine) is not valid.");
        }

        FileServiceCollectConnector connector = connectorFactory.getConnector(host);
        connector.cancelAndDeleteVFtpSssListRequest(machine, requestNo);
    }

    void updateVFtpSssDownloadRequestArchiveFilePath(VFtpSssDownloadRequest request){
        if (request != null){
            if (request.isArchive()){
                if (request.getArchiveFilePath() != null){
                    request.setArchiveFilePath(
                            configurationService.getFileServiceDownloadUrlPath(request.getMachine(),
                                    request.getArchiveFilePath()));
                }
                return;
            }
            for(RequestFileInfo info : request.getFileList()){
                if (info.getDownloadPath() != null){
                    info.setDownloadPath(
                            configurationService.getFileServiceDownloadUrlPath(request.getMachine(),
                                    info.getDownloadPath()));
                }
            }
        }
    }

    public VFtpSssDownloadRequestResponse createVFtpSssDownloadRequest(String machine, String directory, String[] fileList, boolean archive) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            log.error("cannot find file service host for {}", machine);
            throw new FileServiceManageException(400, "Parameter(machine) is not valid.");
        }

        FileServiceCollectConnector connector = connectorFactory.getConnector(host);
        VFtpSssDownloadRequestResponse res = connector.createVFtpSssDownloadRequest(machine, directory, fileList, archive);
        updateVFtpSssDownloadRequestArchiveFilePath(res.getRequest());
        return res;
    }

    public VFtpSssDownloadRequestResponse getVFtpSssDownloadRequest(String machine, String requestNo) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            log.error("cannot find file service host for {}", machine);
            throw new FileServiceManageException(400, "Parameter(machine) is not valid.");
        }

        FileServiceCollectConnector connector = connectorFactory.getConnector(host);
        VFtpSssDownloadRequestResponse res = connector.getVFtpSssDownloadRequest(machine, requestNo);
        updateVFtpSssDownloadRequestArchiveFilePath(res.getRequest());
        return res;
    }

    public void cancelAndDeleteVFtpSssDownloadRequest(String machine, String requestNo) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            log.error("cannot find file service host for {}", machine);
            throw new FileServiceManageException(400, "Parameter(machine) is not valid.");
        }

        FileServiceCollectConnector connector = connectorFactory.getConnector(host);
        connector.cancelAndDeleteVFtpSssDownloadRequest(machine, requestNo);
    }

    void updateVFtpCompatDownloadRequestArchiveFilePath(VFtpCompatDownloadRequest request){
        if (request != null){
            if (request.isArchive()){
                if (request.getArchiveFilePath() != null){
                    request.setArchiveFilePath(
                            configurationService.getFileServiceDownloadUrlPath(request.getMachine(),
                                    request.getArchiveFilePath()));
                }
                return;
            }
            if (request.getFile().getDownloadPath() != null){
                request.getFile().setDownloadPath(
                        configurationService.getFileServiceDownloadUrlPath(request.getMachine(),
                                request.getFile().getDownloadPath()));
            }
        }
    }
    public VFtpCompatDownloadRequestResponse createVFtpCompatDownloadRequest(String machine, String filename, boolean archive) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            log.error("cannot find file service host for {}", machine);
            throw new FileServiceManageException(400, "Parameter(machine) is not valid.");
        }

        FileServiceCollectConnector connector = connectorFactory.getConnector(host);
        VFtpCompatDownloadRequestResponse res = connector.createVFtpCompatDownloadRequest(machine, filename, archive);
        updateVFtpCompatDownloadRequestArchiveFilePath(res.getRequest());
        return res;
    }

    public VFtpCompatDownloadRequestResponse getVFtpCompatDownloadRequest(String machine, String requestNo) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            log.error("cannot find file service host for {}", machine);
            throw new FileServiceManageException(400, "Parameter(machine) is not valid.");
        }

        FileServiceCollectConnector connector = connectorFactory.getConnector(host);
        VFtpCompatDownloadRequestResponse res = connector.getVFtpCompatDownloadRequest(machine, requestNo);
        updateVFtpCompatDownloadRequestArchiveFilePath(res.getRequest());
        return res;
    }

    public void cancelAndDeleteVFtpCompatDownloadRequest(String machine, String requestNo) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            log.error("cannot find file service host for {}", machine);
            throw new FileServiceManageException(400, "Parameter(machine) is not valid.");
        }

        FileServiceCollectConnector connector = connectorFactory.getConnector(host);
        connector.cancelAndDeleteVFtpCompatDownloadRequest(machine, requestNo);
    }
}
