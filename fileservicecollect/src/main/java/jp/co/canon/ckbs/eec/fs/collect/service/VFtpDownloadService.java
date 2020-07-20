package jp.co.canon.ckbs.eec.fs.collect.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class VFtpDownloadService {
    /*
    @Value("${fileservice.collect.vftp.downloadDirectory}")
    String vftpDownloadDirectory;

    @Value("${fileservice.collect.vftp.requestDirectory}")
    String vftpRequestDirectory;
     */

    @PostConstruct
    private void postConstruct(){

    }

    public void stopAll(){

    }

    public String addDownloadRequest(){

        return null;
    }

    public String[] getDownloadRequest(String machine, String requestNo){
        return null;
    }

    public void cancelDownloadRequest(String machine, String requestNo){

    }
}
