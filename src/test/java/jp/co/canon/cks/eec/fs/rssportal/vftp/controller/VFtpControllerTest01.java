package jp.co.canon.cks.eec.fs.rssportal.vftp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import jp.co.canon.cks.eec.fs.rssportal.RssportalApplication;
import jp.co.canon.cks.eec.fs.rssportal.vftp.controller.VFtpController.SssListRequestParam;

@SpringBootTest(classes = RssportalApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("vftptest3")
public class VFtpControllerTest01 {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void test_001() {
        ResponseEntity<String> res = restTemplate.getForEntity("/vftp/sss/listrequest/1234", String.class);

        System.out.println(res.getStatusCode());
        System.out.println(res.getBody());
    }

    @Test
    void test_002() {
        SssListRequestParam param = new SssListRequestParam();
        param.setDirectory("AAAA");
        ResponseEntity<String> res = restTemplate.postForEntity("/vftp/sss/listrequest", param, String.class);

        System.out.println(res.getStatusCode());
        System.out.println(res.getBody());
    }

    @Test
    void test_003() {
        SssListRequestParam param = new SssListRequestParam();
        param.setDirectory("IP_AS_RAW-20200514_081300-20200515_120000");
        ResponseEntity<String> res = restTemplate.postForEntity("/vftp/sss/listrequest", param, String.class);

        System.out.println(res.getStatusCode());
        System.out.println(res.getBody());
    }

    @Test
    void test_004() {
        /*
        SssListRequestParam param = new SssListRequestParam();
        param.setDirectory("IP_AS_RAW-20200514_081300-20200515_120000-DE_MPA1");
        ResponseEntity<FileListStatus> res = restTemplate.postForEntity("/vftp/sss/listrequest", param, FileListStatus.class);

        Assertions.assertEquals(HttpStatus.CREATED, res.getStatusCode());
        URI createdLocation = res.getHeaders().getLocation();

        while (res.getBody().getStatus() != Status.FAILED && res.getBody().getStatus() != Status.COMPLETED) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            res = restTemplate.getForEntity(createdLocation, FileListStatus.class);
        }
        */
    }

    @Test
    public void test_aaa(){

    }
}