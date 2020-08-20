package jp.co.canon.ckbs.eec.fs.manage;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.*;
import jp.co.canon.ckbs.eec.fs.collect.service.LogFileList;
import jp.co.canon.ckbs.eec.fs.manage.service.CategoryList;
import jp.co.canon.ckbs.eec.fs.manage.service.MachineList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class DefaultFileServiceManageConnector implements FileServiceManageConnector{
    RestTemplate restTemplate;
    String host;
    String prefix;

    public DefaultFileServiceManageConnector(String host, RestTemplate restTemplate){
        this.restTemplate = restTemplate;
        this.host = host;
        this.prefix = String.format("http://%s", this.host);
    }

    @Override
    public MachineList getMachineList(){
        String url = this.prefix + "/fsm/machines";
        try {
            ResponseEntity<MachineList> res =
                    restTemplate.getForEntity(url, MachineList.class);

            return res.getBody();
        } catch (RestClientException e){
            log.error("getMachineList RestClientException occurred({})", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public CategoryList getCategoryList(){
        String url = this.prefix + "/fsm/ftp/categories";
        try {
            ResponseEntity<CategoryList> res =
                    restTemplate.getForEntity(url, CategoryList.class);
            return res.getBody();
        } catch (RestClientException e){
            log.error("getCategoryList RestClientException occurred({})", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public CategoryList getCategoryList(String machine){
        String url = this.prefix + "/fsm/ftp/categories?machine={machine}";
        try {
            ResponseEntity<CategoryList> res =
                    restTemplate.getForEntity(url, CategoryList.class, machine);
            return res.getBody();
        } catch (RestClientException e){
            log.error("getCategoryList RestClientException occurred({})", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /*
    FTP INTERFACE
    */
    @Override
    public LogFileList getFtpFileList(String machine,
                                      String category,
                                      String from,
                                      String to,
                                      String keyword,
                                      String path){

        String url = this.prefix + "/fsm/ftp/files?machine={machine}&category={category}&from={from}&to={to}&keyword={keyword}&path={path}";
        try {
            ResponseEntity<LogFileList> res =
                    restTemplate.getForEntity(url, LogFileList.class, machine, category, from, to, keyword, path);
            return res.getBody();
        } catch (RestClientException e){
            log.error("getFtpFileList RestClientException occurred({})", e.getMessage());
            LogFileList logFileList = new LogFileList();
            logFileList.setErrorCode("500 RestClientException");
            logFileList.setErrorMessage(e.getMessage());
            return logFileList;
        }
    }

    FtpDownloadRequestResponse createFtpDownloadRequest(String machine, CreateFtpDownloadRequestParam param){
        String url = this.prefix + "/fsm/ftp/download/{machine}";
        try {
            ResponseEntity<FtpDownloadRequestResponse> res =
                    restTemplate.postForEntity(url, param, FtpDownloadRequestResponse.class, machine);
            return res.getBody();
        } catch (RestClientException e){
            log.error("createFtpDownloadRequest RestClientException occurred({})", e.getMessage());
            FtpDownloadRequestResponse response = new FtpDownloadRequestResponse();
            response.setErrorCode("500 RestClientException");
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    @Override
    public FtpDownloadRequestResponse createFtpDownloadRequest(String machine, String category, boolean archive, String[] fileList){

        CreateFtpDownloadRequestParam param = new CreateFtpDownloadRequestParam();
        param.setCategory(category);
        param.setArchive(archive);
        param.setFileList(fileList);

        return createFtpDownloadRequest(machine, param);
    }

    String createUrlForGetFtpDownloadRequestList(String machine, String requestNo){
        if (machine == null){
            return this.prefix + "/fsm/ftp/download";
        }
        if (requestNo == null){
            return this.prefix + "/fsm/ftp/download/{machine}";
        }
        return this.prefix + "/fsm/ftp/download/{machine}/{requestNo}";
    }

    @Override
    public FtpDownloadRequestListResponse getFtpDownloadRequestList(String machine, String requestNo){
        String url = createUrlForGetFtpDownloadRequestList(machine, requestNo);
        try {
            ResponseEntity<FtpDownloadRequestListResponse> res =
                    restTemplate.getForEntity(url, FtpDownloadRequestListResponse.class, machine, requestNo);

            return res.getBody();
        } catch (RestClientException e){
            log.error("getFtpDownloadRequestList RestClientException occurred({})", e.getMessage());
            FtpDownloadRequestListResponse response = new FtpDownloadRequestListResponse();
            response.setErrorCode("500 RestClientException");
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    @Override
    public void cancelAndDeleteRequest(String machine, String requestNo){
        String url = "/fsm/ftp/download/{machine}/{requestNo}";
        try {
            restTemplate.delete(url, machine, requestNo);
        } catch (RestClientException e){
            log.error("cancelAndDeleteRequest RestClientException occurred({})", e.getMessage());
            e.printStackTrace();
        }
    }

    /*
        VFTP INTERFACE
     */

    /* SSS LIST */
    @Override
    public VFtpSssListRequestResponse createVFtpSssListRequest(String machine, String directory) {
        String url = this.prefix + "/fsm/vftp/sss/list/{machine}";
        CreateVFtpListRequestParam param = new CreateVFtpListRequestParam();
        param.setDirectory(directory);

        try {
            ResponseEntity<VFtpSssListRequestResponse> res =
                    restTemplate.postForEntity(url, param, VFtpSssListRequestResponse.class, machine);

            return res.getBody();
        } catch (RestClientException e){
            log.error("createVFtpSssListRequest RestClientException occurred({})", e.getMessage());
            VFtpSssListRequestResponse response = new VFtpSssListRequestResponse();
            response.setErrorCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    @Override
    public VFtpSssListRequestResponse getVFtpSssListRequest(String machine, String requestNo){
        String url = this.prefix + "/fsm/vftp/sss/list/{machine}/{requestNo}";
        try {
            ResponseEntity<VFtpSssListRequestResponse> res =
                    restTemplate.getForEntity(url, VFtpSssListRequestResponse.class, machine, requestNo);

            return res.getBody();
        } catch (RestClientException e){
            log.error("getVFtpSssListRequest RestClientException occurred({})", e.getMessage());
            VFtpSssListRequestResponse response = new VFtpSssListRequestResponse();
            response.setErrorCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    @Override
    public void cancelAndDeleteVFtpSssListRequest(String machine, String requestNo){
        String url = this.prefix + "/fsm/vftp/sss/list/{machine}/{requestNo}";
        try {
            restTemplate.delete(url, machine, requestNo);
        } catch (RestClientException e){
            log.error("cancelAndDeleteVFtpSssListRequest RestClientException occurred({})", e.getMessage());
            e.printStackTrace();
        }
    }

    /* SSS DOWNLOAD */
    @Override
    public VFtpSssDownloadRequestResponse createVFtpSssDownloadRequest(String machine, String directory, String[] fileList, boolean archive){
        String url = this.prefix + "/fsm/vftp/sss/download/{machine}";

        CreateVFtpSssDownloadRequestParam param = new CreateVFtpSssDownloadRequestParam();
        param.setDirectory(directory);
        param.setFileList(fileList);
        param.setArchive(archive);
        try {
            ResponseEntity<VFtpSssDownloadRequestResponse> res =
                    restTemplate.postForEntity(url, param, VFtpSssDownloadRequestResponse.class, machine);

            return res.getBody();
        } catch (RestClientException e) {
            log.error("createVFtpSssDownloadRequest RestClientException occurred({})", e.getMessage());
            VFtpSssDownloadRequestResponse response = new VFtpSssDownloadRequestResponse();
            response.setErrorCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    @Override
    public VFtpSssDownloadRequestResponse getVFtpSssDownloadRequest(String machine, String requestNo){
        String url = this.prefix + "/fsm/vftp/sss/download/{machine}/{requestNo}";

        try {
            ResponseEntity<VFtpSssDownloadRequestResponse> res =
                    restTemplate.getForEntity(url, VFtpSssDownloadRequestResponse.class, machine, requestNo);

            return res.getBody();
        } catch (RestClientException e){
            log.error("getVFtpSssDownloadRequest RestClientException occurred({})", e.getMessage());
            VFtpSssDownloadRequestResponse response = new VFtpSssDownloadRequestResponse();
            response.setErrorCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    @Override
    public void cancelAndDeleteVFtpSssDownloadRequest(String machine, String requestNo){
        String url = this.prefix + "/fsm/vftp/sss/download/{machine}/{requestNo}";
        try {
            restTemplate.delete(url, machine, requestNo);
        } catch (RestClientException e){
            log.error("cancelAndDeleteVFtpSssDownloadRequest RestClientException occurred({})", e.getMessage());
            e.printStackTrace();
        }
    }

    /* COMPAT DOWNLOAD */
    @Override
    public VFtpCompatDownloadRequestResponse createVFtpCompatDownloadRequest(String machine, String filename, boolean archive){
        String url = this.prefix + "/fsm/vftp/compat/download/{machine}";

        CreateVFtpCompatDownloadRequestParam param = new CreateVFtpCompatDownloadRequestParam();
        param.setFilename(filename);
        param.setArchive(archive);

        try {
            ResponseEntity<VFtpCompatDownloadRequestResponse> res =
                    restTemplate.postForEntity(url, param, VFtpCompatDownloadRequestResponse.class, machine);

            return res.getBody();
        } catch (RestClientException e){
            log.error("createVFtpCompatDownloadRequest RestClientException occurred({})", e.getMessage());
            VFtpCompatDownloadRequestResponse response = new VFtpCompatDownloadRequestResponse();
            response.setErrorCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    @Override
    public VFtpCompatDownloadRequestResponse getVFtpCompatDownloadRequest(String machine, String requestNo){
        String url = this.prefix + "/fsm/vftp/compat/download/{machine}/{requestNo}";

        try {
            ResponseEntity<VFtpCompatDownloadRequestResponse> res =
                    restTemplate.getForEntity(url, VFtpCompatDownloadRequestResponse.class, machine, requestNo);

            return res.getBody();
        } catch (RestClientException e){
            log.error("getVFtpCompatDownloadRequest RestClientException occurred({})", e.getMessage());
            VFtpCompatDownloadRequestResponse response = new VFtpCompatDownloadRequestResponse();
            response.setErrorCode(500);
            response.setErrorMessage(e.getMessage());
            return response;
        }
    }

    @Override
    public void cancelAndDeleteVFtpCompatDownloadRequest(String machine, String requestNo){
        String url = this.prefix + "/fsm/vftp/compat/download/{machine}/{requestNo}";
        try {
            restTemplate.delete(url, machine, requestNo);
        } catch (RestClientException e){
            log.error("cancelAndDeleteVFtpCompatDownloadRequest RestClientException occurred({})", e.getMessage());
            e.printStackTrace();
        }
    }

}
