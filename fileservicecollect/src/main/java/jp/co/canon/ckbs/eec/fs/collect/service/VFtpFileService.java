package jp.co.canon.ckbs.eec.fs.collect.service;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public VFtpSssListRequest getSssListRequest(String machine, String requestNo){
        return listService.getListRequest(machine, requestNo);
    }

    public void cancelAndDeleteSssListRequest(String machine, String requestNo){
        listService.cancelAndDeleteSssListRequest(machine, requestNo);
    }

}
