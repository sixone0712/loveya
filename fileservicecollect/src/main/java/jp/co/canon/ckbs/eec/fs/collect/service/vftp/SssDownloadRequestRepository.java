package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssDownloadRequest;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class SssDownloadRequestRepository {
    File downloadDir;

    ObjectRepository<VFtpSssDownloadRequest> requestObjectRepository;
    boolean stopPurgeThread = false;

    public SssDownloadRequestRepository(File downloadDir){
        this.downloadDir = downloadDir;
        requestObjectRepository = new ObjectRepository<>(this.downloadDir, VFtpSssDownloadRequest.class);
    }

    String getRequestFileName(String requestNo){
        return String.format("%s/%s.json", requestNo, requestNo);
    }

    void deleteRequest(VFtpSssDownloadRequest request){
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

    public void save(VFtpSssDownloadRequest request){
        requestObjectRepository.save(getRequestFileName(request.getRequestNo()), request);
    }

    public VFtpSssDownloadRequest get(String requestNo){
        return requestObjectRepository.load(getRequestFileName(requestNo));
    }

    public void stop(){
        stopPurgeThread = true;
    }
}

