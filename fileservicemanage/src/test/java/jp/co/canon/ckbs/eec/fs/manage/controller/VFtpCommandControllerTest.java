package jp.co.canon.ckbs.eec.fs.manage.controller;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.*;
import jp.co.canon.ckbs.eec.fs.collect.model.FtpRequest;
import jp.co.canon.ckbs.eec.fs.collect.service.VFtpFileInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VFtpCommandControllerTest {
    @Autowired
    VFtpCommandController controller;

    @Test
    void test_001_001(){
        CreateVFtpListRequestParam param = new CreateVFtpListRequestParam();
        param.setDirectory("IP_AS_RAW");

        ResponseEntity<VFtpSssListRequestResponse> responseEntity = controller.createSssListRequest("MPA_1", param);
        VFtpSssListRequestResponse res = responseEntity.getBody();

        responseEntity = controller.getSssListRequest("MPA_1", res.getRequest().getRequestNo());

        controller.cancelAndDeleteSssListRequest("MPA_1", res.getRequest().getRequestNo());
    }

    @Test
    void test_001_002(){
        controller.getSssListRequest("MPA_AAA", "AAA");
        controller.cancelAndDeleteSssListRequest("MPA_AAA", "AAA");

        CreateVFtpListRequestParam param = new CreateVFtpListRequestParam();
        param.setDirectory("IP_AS_RAW");
        controller.createSssListRequest("MPA_AAA", param);
    }

    String[] getSssFileList(String machine, String directory){
        CreateVFtpListRequestParam param = new CreateVFtpListRequestParam();
        param.setDirectory(directory);

        ResponseEntity<VFtpSssListRequestResponse> responseEntity = controller.createSssListRequest("MPA_1", param);
        VFtpSssListRequestResponse res = responseEntity.getBody();

        String requestNo = res.getRequest().getRequestNo();

        while (res.getRequest().getCompletedTime() == 0){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            responseEntity = controller.getSssListRequest("MPA_1", requestNo);
            res = responseEntity.getBody();
        }
        VFtpFileInfo[] fileInfos = res.getRequest().getFileList();
        ArrayList<String> fileList = new ArrayList<>();
        for(VFtpFileInfo fi : fileInfos){
            fileList.add(fi.getFileName());
        }
        return fileList.toArray(new String[0]);
    }

    @Test
    void test_002_001(){
        String[] fileList = getSssFileList("MPA_1", "IP_AS_RAW");
        CreateVFtpSssDownloadRequestParam param = new CreateVFtpSssDownloadRequestParam();
        param.setDirectory("IP_AS_RAW");
        param.setFileList(fileList);
        param.setArchive(true);

        ResponseEntity<VFtpSssDownloadRequestResponse> responseEntity =
                controller.createSssDownloadRequest("MPA_1", param);

        VFtpSssDownloadRequestResponse res = responseEntity.getBody();

        String requestNo = res.getRequest().getRequestNo();

        controller.getSssDownloadRequest("MPA_1", requestNo);
        controller.cancelAndDeleteSssDownloadRequest("MPA_1", requestNo);
    }

    @Test
    void test_002_002(){
        controller.getSssDownloadRequest("MPA_AAA", "AAA");
        controller.cancelAndDeleteSssDownloadRequest("MPA_AAA", "AAA");

        CreateVFtpSssDownloadRequestParam param = new CreateVFtpSssDownloadRequestParam();
        param.setDirectory("IP_AS_RAW");
        param.setFileList(new String[0]);
        param.setArchive(true);
        controller.createSssDownloadRequest("MPA_AAA", param);
    }

    @Test
    void test_003_001(){
        CreateVFtpCompatDownloadRequestParam param = new CreateVFtpCompatDownloadRequestParam();
        param.setFilename("abcdefg.log");
        param.setArchive(true);

        ResponseEntity<VFtpCompatDownloadRequestResponse> responseEntity =
                controller.createCompatDownloadRequest("MPA_1", param);
        VFtpCompatDownloadRequestResponse res = responseEntity.getBody();

        String requestNo = res.getRequest().getRequestNo();

        controller.getCompatDownloadRequest("MPA_1", requestNo);
        controller.cancelAndDeleteCompatDownloadRequest("MPA_1", requestNo);
    }

    @Test
    void test_003_002(){
        controller.getCompatDownloadRequest("MPA_AAA", "AAA");
        controller.cancelAndDeleteCompatDownloadRequest("MPA_AAA", "AAA");

        CreateVFtpCompatDownloadRequestParam param = new CreateVFtpCompatDownloadRequestParam();
        param.setFilename("abcdefg.log");
        param.setArchive(true);
        controller.createCompatDownloadRequest("MPA_AAA", param);
    }
}
