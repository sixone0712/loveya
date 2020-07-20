package jp.co.canon.ckbs.eec.fs.collect.service;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class VFtpListServiceTest {
    @Autowired
    VFtpListService listService;

    @Test
    public void test_01(){
        VFtpSssListRequest request = new VFtpSssListRequest();
        request.setMachine("MPA_1");
        request.setDirectory("IP_AS_RAW_AAA");
        listService.addListRequest(request);

        while(true){
            if (request.getStatus() == VFtpSssListRequest.Status.CANCEL){
                break;
            }
            if (request.getStatus() == VFtpSssListRequest.Status.ERROR){
                break;
            }
            if (request.getStatus() == VFtpSssListRequest.Status.EXECUTED){
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }

        System.out.println("Hello");
    }
}
