package jp.co.canon.ckbs.eec.fs.manage.service;

import jp.co.canon.ckbs.eec.fs.collect.DefaultFileServiceCollectConnector;
import jp.co.canon.ckbs.eec.fs.collect.FileServiceCollectConnector;
import jp.co.canon.ckbs.eec.fs.collect.FileServiceCollectConnectorFactory;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestListResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.model.RequestFileInfo;
import jp.co.canon.ckbs.eec.fs.collect.service.LogFileList;
import jp.co.canon.ckbs.eec.fs.configuration.Category;
import jp.co.canon.ckbs.eec.fs.manage.service.configuration.ConfigurationService;
import jp.co.canon.ckbs.eec.fs.manage.service.configuration.Machine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FtpFileService {
    @Autowired
    ConfigurationService configurationService;

    @Autowired
    FileServiceCollectConnectorFactory connectorFactory;

    @PostConstruct
    void postConstruct(){

    }

    public Machine[] getMachineList(){
        return configurationService.getMachineList();
    }

    String findFirstMachine(){
        Machine[] machines = this.getMachineList();
        if (machines.length == 0){
            return null;
        }
        return machines[0].getName();
    }

    public Category[] getCategories(String machine){
        if (machine == null){
            machine = findFirstMachine();
        }
        return configurationService.getCategories(machine);
    }

    public LogFileList getFtpFileList(String machine,
                                      String category,
                                      String from,
                                      String to,
                                      String keyword,
                                      String path) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            throw new FileServiceManageException(400, "unknown machine name");
        }
        FileServiceCollectConnector connector = connectorFactory.getConnector(host);
        return connector.getFtpFileList(machine, category, from, to, keyword, path);
    }

    void convertFtpDownloadRequest(FtpDownloadRequest res){
        String prefix = configurationService.getFileServiceDownloadUrlPrefix(res.getMachine());
        if (res.isArchive()){
            if (res.getArchiveFilePath() != null){
                res.setArchiveFilePath(prefix + "/" + res.getArchiveFilePath());
            }
            return;
        }
        for(RequestFileInfo info : res.getFileInfos()){
            if (info.getDownloadPath() != null){
                info.setDownloadPath(prefix + "/" +info.getDownloadPath());
            }
        }
    }

    public FtpDownloadRequestResponse createFtpDownloadRequest(String machine, String category, boolean archive, String[] fileList) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            throw new FileServiceManageException(400, "unknown machine name");
        }
        FileServiceCollectConnector connector = connectorFactory.getConnector(host);
        FtpDownloadRequestResponse res = connector.createFtpDownloadRequest(machine, category, archive, fileList);

        convertFtpDownloadRequest(res);

        return res;
    }

    String[] getHostsForMachine(String machine) throws FileServiceManageException{
        if (machine == null){
            return configurationService.getAllFileServiceHost();
        }
        String[] hosts = new String[1];
        hosts[0] = configurationService.getFileServiceHost(machine);
        if (hosts[0] == null){
            throw new FileServiceManageException(400, "unknown machine name");
        }
        return hosts;
    }

    public FtpDownloadRequestListResponse getFtpDownloadRequestList(String machine, String requestNo) throws FileServiceManageException {
        String[] hosts = getHostsForMachine(machine);
        if (hosts.length == 0){
            throw new FileServiceManageException(400, "unknown machine name");
        }

        ArrayList<GetRequestThread> threadArrayList = new ArrayList<>();
        ArrayList<FtpDownloadRequestListResponse> responseList = new ArrayList<>();
        for(String host : hosts){
            FileServiceCollectConnector connector = connectorFactory.getConnector(host);

            GetRequestThread th = new GetRequestThread(connector, machine, requestNo);
            threadArrayList.add(th);
            th.start();
        }

        try {
            for (GetRequestThread th : threadArrayList) {
                th.join();
            }
        } catch (Exception e){

        }

        int errorCount = 0;
        List<FtpDownloadRequest> downloadRequestList = new ArrayList<>();
        List<String> errorCodeList = new ArrayList<>();
        for(GetRequestThread th : threadArrayList){
            FtpDownloadRequestListResponse r = th.getResponse();
            if (r == null){
                errorCount++;
                continue;
            }
            for (FtpDownloadRequest req : r.getRequestList()){
                downloadRequestList.add(req);
            }
            if (r.getErrorCode() != null){
                errorCodeList.add(r.getErrorCode());
            }
        }

        for(FtpDownloadRequest req : downloadRequestList){
            convertFtpDownloadRequest(req);
        }

        FtpDownloadRequestListResponse response = new FtpDownloadRequestListResponse();
        response.setRequestList(downloadRequestList.toArray(new FtpDownloadRequest[0]));
        if (errorCodeList.size() > 0){
            String errCode = String.join(";", errorCodeList);
            response.setErrorCode(errCode);
        }
        if (errorCount > 0){
            if (response.getErrorCode() != null){
                response.setErrorCode(response.getErrorCode() + ";Excution Exception");
            } else {
                response.setErrorCode("Execution Exception");
            }
        }
        return response;

    }

    public void cancelAndDeleteRequest(String machine, String requestNo) throws FileServiceManageException {
        String host = configurationService.getFileServiceHost(machine);
        if (host == null){
            throw new FileServiceManageException(400, "unknown machine name");
        }
        FileServiceCollectConnector connector = connectorFactory.getConnector(host);
        connector.cancelAndDeleteRequest(machine, requestNo);
    }

    class GetRequestThread extends Thread {
        FileServiceCollectConnector connector;
        String machine;
        String requestNo;
        FtpDownloadRequestListResponse response;

        String exceptionMessage;

        public GetRequestThread(FileServiceCollectConnector connector, String machine, String requestNo){
            this.connector = connector;
            this.machine = machine;
            this.requestNo = requestNo;
        }

        @Override
        public void run() {
            try {
                response = connector.getFtpDownloadRequestList(machine, requestNo);
            } catch (Exception e){
                exceptionMessage = e.getMessage();
            }
        }

        FtpDownloadRequestListResponse getResponse(){
            return response;
        }

        String getExceptionMessage(){
            return exceptionMessage;
        }
    }

}
