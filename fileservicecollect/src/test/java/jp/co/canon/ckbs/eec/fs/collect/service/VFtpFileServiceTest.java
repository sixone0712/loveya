package jp.co.canon.ckbs.eec.fs.collect.service;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssDownloadRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class VFtpFileServiceTest {
    @Autowired
    VFtpFileService fileService;

    @Test
    void test_001(){
        List<String> fileList = new ArrayList<>();
        fileList.add("abcdefg.log");

        try {
            VFtpSssDownloadRequest request =
                fileService.addSssDownloadRequest("MPA_1", "IP_AS_RAW_AAA", fileList.toArray(new String[0]), false);

            Thread.sleep(60000);

            System.out.println(request);
        } catch (FileServiceCollectException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
