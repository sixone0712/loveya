package jp.co.canon.ckbs.eec.fs.collect.service;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VFtpFileService {
    @Autowired
    VFtpDownloadService downloadService;

    @Autowired
    VFtpListService listService;

    public String addSssListRequest(String machine, String directory){
        VFtpListRequest request = new VFtpListRequest();
        request.setMachine(machine);
        request.setDirectory(directory);

        return null;
    }

}
