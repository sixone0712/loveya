package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpCompatDownloadRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
public class CompatDownloadRequestRepository {
    File downloadDir;

    ObjectRepository<VFtpCompatDownloadRequest> requestObjectRepository;
    boolean stopPurgeThread = false;

    public CompatDownloadRequestRepository(File downloadDir){
        this.downloadDir = downloadDir;
        requestObjectRepository = new ObjectRepository<>(this.downloadDir, VFtpCompatDownloadRequest.class);
    }

    String getRequestFileName(String requestNo){
        return String.format("%s/%s.json", requestNo, requestNo);
    }

    void deleteRequest(VFtpCompatDownloadRequest request){
        if (request == null){
            return;
        }
        requestObjectRepository.delete(getRequestFileName(request.getRequestNo()));
        try {
            FileUtils.deleteDirectory(new File(downloadDir, request.getRequestNo()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(VFtpCompatDownloadRequest request){
        log.trace("save request({})", request.getRequestNo());
        requestObjectRepository.save(getRequestFileName(request.getRequestNo()), request);
    }

    public VFtpCompatDownloadRequest get(String requestNo){
        return requestObjectRepository.load(getRequestFileName(requestNo));
    }

    public void stop(){
        stopPurgeThread = true;
    }
}
