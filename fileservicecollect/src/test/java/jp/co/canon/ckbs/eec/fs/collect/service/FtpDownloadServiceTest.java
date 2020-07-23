package jp.co.canon.ckbs.eec.fs.collect.service;

import jp.co.canon.ckbs.eec.fs.collect.model.RequestFileInfo;
import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class FtpDownloadServiceTest {
    @Autowired
    FtpDownloadService downloadService;

    @Test
    void test_001(){
        FtpDownloadRequest request = new FtpDownloadRequest();
        request.setMachine("MPA_2");
        request.setCategory("002");
        request.setArchive(true);

        ArrayList<RequestFileInfo> list = new ArrayList<>();

        list.add(new RequestFileInfo("20200629225500"));
        list.add(new RequestFileInfo("20200629223000"));

        request.setFileInfos(list.toArray(new RequestFileInfo[0]));

        try {
            downloadService.addDownloadRequest(request);
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
