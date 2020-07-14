package jp.co.canon.ckbs.eec.fs.collect;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestListResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.CreateFtpDownloadRequestParam;
import jp.co.canon.ckbs.eec.fs.collect.service.LogFileList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class FileServiceCollectConnector {

    RestTemplate restTemplate;
    String host;
    String prefix;

    public FileServiceCollectConnector(String host){
        this.restTemplate = new RestTemplate();
        this.host = host;
        this.prefix = String.format("http://%s", this.host);
    }

    public LogFileList getFtpFileList(String machine,
                               String category,
                               String from,
                               String to,
                               String keyword,
                               String path){

        String url = this.prefix + "/fsc/ftp/files?machine={machine}&category={category}&from={from}&to={to}&keyword={keyword}&path={path}";

        ResponseEntity<LogFileList> res =
                restTemplate.getForEntity(url, LogFileList.class, machine, category, from, to, keyword, path);
        return res.getBody();
    }

    public FtpDownloadRequestResponse createFtpDownloadRequest(String machine, CreateFtpDownloadRequestParam param){
        String url = this.prefix + "/fsc/ftp/download/{machine}";

        ResponseEntity<FtpDownloadRequestResponse> res =
                restTemplate.postForEntity(url, param, FtpDownloadRequestResponse.class, machine);
        return res.getBody();
    }

    public FtpDownloadRequestResponse createFtpDownloadRequest(String machine, String category, boolean archive, String[] fileList){

        CreateFtpDownloadRequestParam param = new CreateFtpDownloadRequestParam();
        param.setCategory(category);
        param.setArchive(archive);
        param.setFileList(fileList);

        return createFtpDownloadRequest(machine, param);
    }

    String createUrlForGetFtpDownloadRequestList(String machine, String requestNo){
        if (machine == null){
            return this.prefix + "/fsc/ftp/download";
        }
        if (requestNo == null){
            return this.prefix + "/fsc/ftp/download/{machine}";
        }
        return this.prefix + "/fsc/ftp/download/{machine}/{requestNo}";
    }

    public FtpDownloadRequestListResponse getFtpDownloadRequestList(String machine, String requestNo){
        String url = createUrlForGetFtpDownloadRequestList(machine, requestNo);

        ResponseEntity<FtpDownloadRequestListResponse> res =
                restTemplate.getForEntity(url, FtpDownloadRequestListResponse.class, machine, requestNo);

        return res.getBody();
    }

    public void cancelAndDeleteRequest(String machine, String requestNo){
        String url = "/fsc/ftp/download/{machine}/{requestNo}";
        restTemplate.delete(url, machine, requestNo);
    }
}
