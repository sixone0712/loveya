package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssDownloadRequest;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class SssDownloadRequestRepository {
    File requestDir;
    File downloadDir;

    ObjectRepository<VFtpSssDownloadRequest> requestObjectRepository;
    boolean stopPurgeThread = false;

    public SssDownloadRequestRepository(File requestDir, File downloadDir){
        this.requestDir = requestDir;
        this.downloadDir = downloadDir;
        requestObjectRepository = new ObjectRepository<>(this.requestDir, VFtpSssDownloadRequest.class);

        Thread requestPurgeThread = new Thread(()->{
            while(!stopPurgeThread){
                deleteExpiredRequests();
                try {
                    Thread.sleep(60*60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        requestPurgeThread.start();
    }

    void deleteRequest(VFtpSssDownloadRequest request){
        if (request == null){
            return;
        }
        requestObjectRepository.delete(request.getRequestNo() + ".json");
        try {
            FileUtils.deleteDirectory(new File(downloadDir, request.getRequestNo()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void deleteExpiredRequest(long baseTime, VFtpSssDownloadRequest request){
        if (request == null){
            return;
        }
        if (request.getCompletedTime() < baseTime){
            deleteRequest(request);
        }
    }

    void deleteExpiredRequests(){
        long baseTime = System.currentTimeMillis() - 24*60*60*1000;
        File[] files = this.requestDir.listFiles();
        for (File f : files){
            VFtpSssDownloadRequest request = requestObjectRepository.load(f);
            deleteExpiredRequest(baseTime, request);
        }
    }

    public void save(VFtpSssDownloadRequest request){
        requestObjectRepository.save(request.getRequestNo() + ".json", request);
    }

    public VFtpSssDownloadRequest get(String requestNo){
        return requestObjectRepository.load(requestNo + ".json");
    }

    public void stop(){
        stopPurgeThread = true;
    }
}

