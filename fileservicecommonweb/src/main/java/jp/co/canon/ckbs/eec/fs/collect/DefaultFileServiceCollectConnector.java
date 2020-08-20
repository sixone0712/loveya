package jp.co.canon.ckbs.eec.fs.collect;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.*;
import jp.co.canon.ckbs.eec.fs.collect.service.LogFileList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class DefaultFileServiceCollectConnector implements FileServiceCollectConnector{

    RestTemplate restTemplate;
    String host;
    String prefix;

    public DefaultFileServiceCollectConnector(String host, RestTemplate restTemplate){
        this.restTemplate = restTemplate;
        this.host = host;
        this.prefix = String.format("http://%s", this.host);
    }

    /*
    FTP INTERFACE
    */
    public LogFileList getFtpFileList(String machine,
                               String category,
                               String from,
                               String to,
                               String keyword,
                               String path){

        String url = this.prefix + "/fsc/ftp/files?machine={machine}&category={category}&from={from}&to={to}&keyword={keyword}&path={path}";
        try {
            ResponseEntity<LogFileList> res =
                    restTemplate.getForEntity(url, LogFileList.class, machine, category, from, to, keyword, path);
            return res.getBody();
        } catch (RestClientException e){
            log.error("getFtpFileList RestClientException occurred ({})", e.getMessage());
            LogFileList logFileList = new LogFileList();
            logFileList.setErrorCode("500 RestClientException");
            logFileList.setErrorMessage(e.getMessage());
            return logFileList;
        }
    }

    FtpDownloadRequestResponse createFtpDownloadRequest(String machine, CreateFtpDownloadRequestParam param){
        String url = this.prefix + "/fsc/ftp/download/{machine}";
        try {
            ResponseEntity<FtpDownloadRequestResponse> res =
                    restTemplate.postForEntity(url, param, FtpDownloadRequestResponse.class, machine);
            return res.getBody();
        } catch (RestClientException e){
            log.error("createFtpDownloadRequest RestClientException occurred ({})", e.getMessage());
            FtpDownloadRequestResponse response = new FtpDownloadRequestResponse();
            response.setErrorCode("500 RestClientException");
            response.setErrorMessage(e.getMessage());
            return response;
        }
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
        try {
            ResponseEntity<FtpDownloadRequestListResponse> res =
                    restTemplate.getForEntity(url, FtpDownloadRequestListResponse.class, machine, requestNo);

            return res.getBody();
        } catch (RestClientException e){
            log.error("getFtpDownloadRequestList RestClientException occurred ({})", e.getMessage());
            FtpDownloadRequestListResponse response = new FtpDownloadRequestListResponse();
            response.setErrorCode("500 RestClientException");
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    public void cancelAndDeleteRequest(String machine, String requestNo){
        String url = "/fsc/ftp/download/{machine}/{requestNo}";
        try {
            restTemplate.delete(url, machine, requestNo);
        } catch (RestClientException e){
            log.error("cancelAndDeleteRequest RestClientException occurred ({})", e.getMessage());
            e.printStackTrace();
        }
    }

    /*
        VFTP INTERFACE
     */

    /* SSS LIST */
    public VFtpSssListRequestResponse createVFtpSssListRequest(String machine, String directory) {
        String url = this.prefix + "/fsc/vftp/sss/list/{machine}";
        CreateVFtpListRequestParam param = new CreateVFtpListRequestParam();
        param.setDirectory(directory);
        try {
            ResponseEntity<VFtpSssListRequestResponse> res =
                    restTemplate.postForEntity(url, param, VFtpSssListRequestResponse.class, machine);

            return res.getBody();
        } catch (RestClientException e){
            log.error("createVFtpSssListRequest RestClientException occurred ({})", e.getMessage());
            VFtpSssListRequestResponse response = new VFtpSssListRequestResponse();
            response.setErrorCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    public VFtpSssListRequestResponse getVFtpSssListRequest(String machine, String requestNo){
        String url = this.prefix + "/fsc/vftp/sss/list/{machine}/{requestNo}";
        try {
            ResponseEntity<VFtpSssListRequestResponse> res =
                    restTemplate.getForEntity(url, VFtpSssListRequestResponse.class, machine, requestNo);

            return res.getBody();
        } catch (RestClientException e){
            log.error("getVFtpSssListRequest RestClientException occurred ({})", e.getMessage());
            VFtpSssListRequestResponse response = new VFtpSssListRequestResponse();
            response.setErrorCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    public void cancelAndDeleteVFtpSssListRequest(String machine, String requestNo){
        String url = this.prefix + "/fsc/vftp/sss/list/{machine}/{requestNo}";
        try {
            restTemplate.delete(url, machine, requestNo);
        } catch (RestClientException e){
            log.error("cancelAndDeleteVFtpSssListRequest RestClientException occurred ({})", e.getMessage());
            e.printStackTrace();
        }
    }

    /* SSS DOWNLOAD */
    public VFtpSssDownloadRequestResponse createVFtpSssDownloadRequest(String machine, String directory, String[] fileList, boolean archive){
        String url = this.prefix + "/fsc/vftp/sss/download/{machine}";

        CreateVFtpSssDownloadRequestParam param = new CreateVFtpSssDownloadRequestParam();
        param.setDirectory(directory);
        param.setFileList(fileList);
        param.setArchive(archive);
        try {
            ResponseEntity<VFtpSssDownloadRequestResponse> res =
                    restTemplate.postForEntity(url, param, VFtpSssDownloadRequestResponse.class, machine);

            return res.getBody();
        } catch (RestClientException e){
            log.error("createVFtpSssDownloadRequest RestClientException occurred ({})", e.getMessage());
            VFtpSssDownloadRequestResponse response = new VFtpSssDownloadRequestResponse();
            response.setErrorCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    public VFtpSssDownloadRequestResponse getVFtpSssDownloadRequest(String machine, String requestNo){
        String url = this.prefix + "/fsc/vftp/sss/download/{machine}/{requestNo}";

        try {
            ResponseEntity<VFtpSssDownloadRequestResponse> res =
                    restTemplate.getForEntity(url, VFtpSssDownloadRequestResponse.class, machine, requestNo);

            return res.getBody();
        } catch (RestClientException e){
            log.error("getVFtpSssDownloadRequest RestClientException occurred ({})", e.getMessage());
            VFtpSssDownloadRequestResponse response = new VFtpSssDownloadRequestResponse();
            response.setErrorCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    public void cancelAndDeleteVFtpSssDownloadRequest(String machine, String requestNo){
        String url = this.prefix + "/fsc/vftp/sss/download/{machine}/{requestNo}";
        try {
            restTemplate.delete(url, machine, requestNo);
        } catch (RestClientException e){
            log.error("getVFtpSssDownloadRequest RestClientException occurred ({})", e.getMessage());
            e.printStackTrace();
        }
    }

    /* COMPAT DOWNLOAD */
    public VFtpCompatDownloadRequestResponse createVFtpCompatDownloadRequest(String machine, String filename, boolean archive){
        String url = this.prefix + "/fsc/vftp/compat/download/{machine}";

        CreateVFtpCompatDownloadRequestParam param = new CreateVFtpCompatDownloadRequestParam();
        param.setFilename(filename);
        param.setArchive(archive);
        try {
            ResponseEntity<VFtpCompatDownloadRequestResponse> res =
                    restTemplate.postForEntity(url, param, VFtpCompatDownloadRequestResponse.class, machine);

            return res.getBody();
        } catch (RestClientException e){
            log.error("createVFtpCompatDownloadRequest RestClientException occurred ({})", e.getMessage());
            VFtpCompatDownloadRequestResponse response = new VFtpCompatDownloadRequestResponse();
            response.setErrorCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    public VFtpCompatDownloadRequestResponse getVFtpCompatDownloadRequest(String machine, String requestNo){
        String url = this.prefix + "/fsc/vftp/compat/download/{machine}/{requestNo}";
        try {
            ResponseEntity<VFtpCompatDownloadRequestResponse> res =
                    restTemplate.getForEntity(url, VFtpCompatDownloadRequestResponse.class, machine, requestNo);

            return res.getBody();
        } catch (RestClientException e){
            log.error("getVFtpCompatDownloadRequest RestClientException occurred ({})", e.getMessage());
            VFtpCompatDownloadRequestResponse response = new VFtpCompatDownloadRequestResponse();
            response.setErrorCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    public void cancelAndDeleteVFtpCompatDownloadRequest(String machine, String requestNo){
        String url = this.prefix + "/fsc/vftp/compat/download/{machine}/{requestNo}";
        try {
            restTemplate.delete(url, machine, requestNo);
        } catch (RestClientException e){
            log.error("cancelAndDeleteVFtpCompatDownloadRequest RestClientException occurred ({})", e.getMessage());
            e.printStackTrace();
        }
    }
}
