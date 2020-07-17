package jp.co.canon.ckbs.eec.fs.manage;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.*;
import jp.co.canon.ckbs.eec.fs.collect.service.LogFileList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class FileServiceManageConnector {
    RestTemplate restTemplate;
    String host;
    String prefix;

    public FileServiceManageConnector(String host){
        this.restTemplate = new RestTemplate();
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

        String url = this.prefix + "/fsm/ftp/files?machine={machine}&category={category}&from={from}&to={to}&keyword={keyword}&path={path}";
        ResponseEntity<LogFileList> res =
                restTemplate.getForEntity(url, LogFileList.class, machine, category, from, to, keyword, path);
        return res.getBody();
    }

    public FtpDownloadRequestResponse createFtpDownloadRequest(String machine, CreateFtpDownloadRequestParam param){
        String url = this.prefix + "/fsm/ftp/download/{machine}";

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
            return this.prefix + "/fsm/ftp/download";
        }
        if (requestNo == null){
            return this.prefix + "/fsm/ftp/download/{machine}";
        }
        return this.prefix + "/fsm/ftp/download/{machine}/{requestNo}";
    }

    public FtpDownloadRequestListResponse getFtpDownloadRequestList(String machine, String requestNo){
        String url = createUrlForGetFtpDownloadRequestList(machine, requestNo);

        ResponseEntity<FtpDownloadRequestListResponse> res =
                restTemplate.getForEntity(url, FtpDownloadRequestListResponse.class, machine, requestNo);

        return res.getBody();
    }

    public void cancelAndDeleteRequest(String machine, String requestNo){
        String url = "/fsm/ftp/download/{machine}/{requestNo}";
        restTemplate.delete(url, machine, requestNo);
    }

    /*
        VFTP INTERFACE
     */

    /* SSS LIST */
    public VFtpListRequestResponse createVFtpSssListRequest(String machine, String directory) {
        String url = this.prefix + "/fsm/vftp/sss/list/{machine}";
        CreateVFtpListRequestParam param = new CreateVFtpListRequestParam();
        param.setDirectory(directory);

        ResponseEntity<VFtpListRequestResponse> res =
                restTemplate.postForEntity(url, param, VFtpListRequestResponse.class, machine);

        return res.getBody();
    }

    public VFtpListRequestResponse getVFtpSssListRequest(String machine, String requestNo){
        String url = this.prefix + "/fsm/vftp/sss/list/{machine}/{requestNo}";

        ResponseEntity<VFtpListRequestResponse> res =
                restTemplate.getForEntity(url, VFtpListRequestResponse.class, machine, requestNo);

        return res.getBody();
    }

    public void cancelAndDeleteVFtpSssListRequest(String machine, String requestNo){
        String url = this.prefix + "/fsm/vftp/sss/list/{machine}/{requestNo}";
        restTemplate.delete(url, machine, requestNo);
    }

    /* SSS DOWNLOAD */
    public VFtpSssDownloadRequestResponse createVFtpCompatDownloadRequest(String machine, String directory, String[] fileList, boolean archive){
        String url = this.prefix + "/fsm/vftp/sss/download/{machine}";

        CreateVFtpSssDownloadRequestParam param = new CreateVFtpSssDownloadRequestParam();
        param.setDirectory(directory);
        param.setFileList(fileList);
        param.setArchive(archive);

        ResponseEntity<VFtpSssDownloadRequestResponse> res =
                restTemplate.postForEntity(url, param, VFtpSssDownloadRequestResponse.class, machine);

        return res.getBody();
    }

    public VFtpSssDownloadRequestResponse getVFtpCompatDownloadRequest(String machine, String requestNo){
        String url = this.prefix + "/fsm/vftp/sss/download/{machine}/{requestNo}";

        ResponseEntity<VFtpSssDownloadRequestResponse> res =
                restTemplate.getForEntity(url, VFtpSssDownloadRequestResponse.class, machine, requestNo);

        return res.getBody();
    }

    public void cancelAndDeleteVFtpSssDownloadRequest(String machine, String requestNo){
        String url = this.prefix + "/fsm/vftp/sss/download/{machine}/{requestNo}";
        restTemplate.delete(url, machine, requestNo);
    }

    /* COMPAT DOWNLOAD */
    public VFtpCompatDownloadRequestResponse createVFtpCompatDownloadRequest(String machine, String filename, boolean archive){
        String url = this.prefix + "/fsm/vftp/compat/download/{machine}";

        CreateVFtpCompatDownloadRequestParam param = new CreateVFtpCompatDownloadRequestParam();
        param.setFilename(filename);
        param.setArchive(archive);

        ResponseEntity<VFtpCompatDownloadRequestResponse> res =
                restTemplate.postForEntity(url, param, VFtpCompatDownloadRequestResponse.class, machine);

        return res.getBody();
    }

    public VFtpCompatDownloadRequestResponse getVFtpDownloadRequest(String machine, String requestNo){
        String url = this.prefix + "/fsm/vftp/compat/download/{machine}/{requestNo}";

        ResponseEntity<VFtpCompatDownloadRequestResponse> res =
                restTemplate.getForEntity(url, VFtpCompatDownloadRequestResponse.class, machine, requestNo);

        return res.getBody();
    }

    public void cancelAndDeleteVFtpCompatDownloadRequest(String machine, String requestNo){
        String url = this.prefix + "/fsm/vftp/compat/download/{machine}/{requestNo}";
        restTemplate.delete(url, machine, requestNo);
    }

}
