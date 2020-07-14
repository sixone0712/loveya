package jp.co.canon.ckbs.eec.fs.manage.controller;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.CreateFtpDownloadRequestParam;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.manage.service.MachineList;
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
    TestRestTemplate restTemplate;

    @Test
    public void test_001(){
        ResponseEntity<MachineList> res = restTemplate.getForEntity("/machines", MachineList.class);
        MachineList machineList = res.getBody();
    }

    @Test
    public void test_002(){
        CreateFtpDownloadRequestParam param = new CreateFtpDownloadRequestParam();
        ArrayList<String> fileList = new ArrayList<>();

        fileList.add("20200629225500");
        fileList.add("20200629223000");

        param.setCategory("002");
        param.setArchive(true);
        param.setFileList(fileList.toArray(new String[0]));

        ResponseEntity<FtpDownloadRequestResponse> res =
                restTemplate.postForEntity("/ftp/download/MPA_2", param, FtpDownloadRequestResponse.class);

        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());



    }
}
