package jp.co.canon.ckbs.eec.fs.manage.service;

import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.model.RequestFileInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FtpFileServiceTest {
    @Autowired
    FtpFileService fileService;

    @Test
    void test_001(){
        try {
            fileService.getHostsForMachine("MPA_1");
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
        try {
            fileService.getHostsForMachine("MPA_AAA");
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test_002(){
        fileService.getCategories("MPA_1");
    }

    @Test
    void test_003(){
        try {
            fileService.createFtpDownloadRequest("MPA_AAA", "003", true, null);
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test_004(){
        try {
            fileService.getFtpDownloadRequestList("MPA_AAA", "AAA");
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test_005(){
        try {
            fileService.cancelAndDeleteRequest("MPA_AAA", "AAA");
        } catch (FileServiceManageException e) {
            e.printStackTrace();
        }
    }

    @Test
    void test_006(){
        FtpDownloadRequest req = new FtpDownloadRequest();
        req.setArchive(false);

        ArrayList<RequestFileInfo> fileInfoArrayList = new ArrayList<>();

        RequestFileInfo info = new RequestFileInfo("abcdefg.txt");
        fileInfoArrayList.add(info);

        req.setFileInfos(fileInfoArrayList.toArray(new RequestFileInfo[0]));

        fileService.convertFtpDownloadRequest(req);

        info.setDownloadPath("ABCDEFG/abcdefg.zip");
        fileService.convertFtpDownloadRequest(req);
    }
}
