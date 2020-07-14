package jp.co.canon.ckbs.eec.fs.collect.controller;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestListResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.CreateFtpDownloadRequestParam;
import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FtpCommandControllerTest {
    @Autowired
    FtpCommandController controller;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void test_001(){
        CreateFtpDownloadRequestParam param = new CreateFtpDownloadRequestParam();

        param.setCategory("002");

        ArrayList<String> list = new ArrayList<>();
        list.add("20200629225500");
        list.add("20200629223000");

        param.setFileList(list.toArray(new String[0]));
        param.setArchive(true);

        ResponseEntity<FtpDownloadRequestResponse> res = controller.createFtpDownloadRequest("MPA_2", param);
        FtpDownloadRequestResponse r = res.getBody();

        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertNull(r.getErrorCode());

        String requestNo = r.getRequestNo();
        FtpDownloadRequest request;
        while(true){
            ResponseEntity<FtpDownloadRequestListResponse> res2 = controller.getFtpDownloadRequestList("MPA_2", requestNo, "");
            FtpDownloadRequest[] requestList = res2.getBody().getRequestList();
            request = requestList[0];
            if (FtpDownloadRequest.checkCompletedStatus(request)){
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(request);
    }

    @Test
    void test_002(){
        CreateFtpDownloadRequestParam param = new CreateFtpDownloadRequestParam();

        param.setCategory("002");

        ArrayList<String> list = new ArrayList<>();
        list.add("20200629225500");
        list.add("20200629223000");

        param.setFileList(list.toArray(new String[0]));
        param.setArchive(true);

        ResponseEntity<FtpDownloadRequestResponse> res;
        res = restTemplate.postForEntity("/ftp/download/MPA_2", param, FtpDownloadRequestResponse.class);
        FtpDownloadRequestResponse r = res.getBody();
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertNull(r.getErrorCode());

        String requestNo = r.getRequestNo();
        FtpDownloadRequest request;

        while(true){
            ResponseEntity<FtpDownloadRequestListResponse> res2;
            res2 = restTemplate.getForEntity("/ftp/download/MPA_2/"+requestNo, FtpDownloadRequestListResponse.class);
            FtpDownloadRequest[] requestList = res2.getBody().getRequestList();
            request = requestList[0];
            if (FtpDownloadRequest.checkCompletedStatus(request)){
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(request);
    }
}
