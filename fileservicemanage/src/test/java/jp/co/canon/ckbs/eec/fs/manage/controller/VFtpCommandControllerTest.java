package jp.co.canon.ckbs.eec.fs.manage.controller;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.CreateVFtpListRequestParam;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpSssListRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class VFtpCommandControllerTest {
    @Autowired
    VFtpCommandController commandController;

    @Test
    public void test_001(){
        CreateVFtpListRequestParam param = new CreateVFtpListRequestParam();
        param.setDirectory("IP_AS_RAW_AAA");

        ResponseEntity<VFtpSssListRequestResponse> res = commandController.createSssListRequest("MPA_1", param);

        VFtpSssListRequest request = res.getBody();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        res = commandController.getSssListRequest("MPA_1", request.getRequestNo());

        request = res.getBody();

        System.out.println(request);
    }
}
