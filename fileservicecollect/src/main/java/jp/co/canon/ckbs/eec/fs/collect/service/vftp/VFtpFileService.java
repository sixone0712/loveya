package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import jp.co.canon.ckbs.eec.fs.collect.model.RequestFileInfo;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpCompatDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;
import jp.co.canon.ckbs.eec.fs.collect.service.FileServiceCollectException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class VFtpFileService {
    @Autowired
    VFtpDownloadService downloadService;

    @Autowired
    VFtpListService listService;

    public VFtpSssListRequest addSssListRequest(String machine, String directory) throws FileServiceCollectException {
        VFtpSssListRequest request = new VFtpSssListRequest();
        request.setMachine(machine);
        request.setDirectory(directory);
        return listService.addListRequest(request);
    }

    public VFtpSssListRequest getSssListRequest(String machine, String requestNo) throws FileServiceCollectException {
        VFtpSssListRequest request = listService.getListRequest(machine, requestNo);
        if (request == null){
            log.error("request is not found ({}, {})", machine, requestNo);
            throw new FileServiceCollectException(400, "request is not found ("+machine+","+requestNo+")");
        }
        return request;
    }

    public void cancelAndDeleteSssListRequest(String machine, String requestNo){
        listService.cancelAndDeleteSssListRequest(machine, requestNo);
    }

    public VFtpSssDownloadRequest addSssDownloadRequest(String machine, String directory, String[] fileList, boolean archive) throws FileServiceCollectException {
        VFtpSssDownloadRequest request = new VFtpSssDownloadRequest();
        request.setMachine(machine);
        request.setDirectory(directory);
        request.setArchive(archive);
        List<RequestFileInfo> requestFileInfoList = new ArrayList<>();
        for (String filename : fileList){
            RequestFileInfo fileInfo = new RequestFileInfo();
            fileInfo.setName(filename);
            requestFileInfoList.add(fileInfo);
        }
        request.setFileList(requestFileInfoList.toArray(new RequestFileInfo[0]));

        return downloadService.addSssDownloadRequest(request);
    }

    public VFtpSssDownloadRequest getSssDownloadRequest(String machine, String requestNo) throws FileServiceCollectException {
        VFtpSssDownloadRequest request = downloadService.getSssDownloadRequest(machine, requestNo);
        if (request == null){
            log.error("request is not found ({}, {})", machine, requestNo);
            throw new FileServiceCollectException(400, "request is not found ("+machine+","+requestNo+")");
        }
        return request;
    }

    public void cancelAndDeleteSssDownloadRequest(String machine, String requestNo){
        downloadService.cancelAndDeleteSssDownloadRequest(machine, requestNo);
    }

    public VFtpCompatDownloadRequest addCompatDownloadRequest(String machine, String filename, boolean archive) throws FileServiceCollectException {
        VFtpCompatDownloadRequest request = new VFtpCompatDownloadRequest();
        request.setMachine(machine);
        RequestFileInfo fileInfo = new RequestFileInfo();
        fileInfo.setName(filename);
        request.setFile(fileInfo);
        request.setArchive(archive);

        return downloadService.addCompatDownloadRequest(request);
    }

    public VFtpCompatDownloadRequest getCompatDownloadRequest(String machine, String requestNo) throws FileServiceCollectException {
        VFtpCompatDownloadRequest request = downloadService.getCompatDownloadRequest(machine, requestNo);
        if (request == null){
            log.error("request is not found ({}, {})", machine, requestNo);
            throw new FileServiceCollectException(400, "request is not found ("+machine+","+requestNo+")");
        }
        return request;
    }

    public void cancelAndDeleteCompatDownloadRequest(String machine, String requestNo){
        downloadService.cancelAndDeleteCompatDownloadRequest(machine, requestNo);
    }

}
