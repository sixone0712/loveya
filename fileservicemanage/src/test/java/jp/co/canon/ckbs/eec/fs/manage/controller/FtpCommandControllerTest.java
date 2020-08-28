package jp.co.canon.ckbs.eec.fs.manage.controller;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.CreateFtpDownloadRequestParam;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestListResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.service.FileInfo;
import jp.co.canon.ckbs.eec.fs.collect.service.LogFileList;
import jp.co.canon.ckbs.eec.fs.manage.service.CategoryList;
import jp.co.canon.ckbs.eec.fs.manage.service.MachineList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FtpCommandControllerTest {
    @Autowired
    FtpCommandController controller;

    @Test
    void test_001_001(){
        ResponseEntity<MachineList> responseEntity =
                                                controller.getMachineList();
    }

    @Test
    void test_002_001(){
        ResponseEntity<CategoryList> responseEntity =
                                                controller.getMachineCategories(null);
    }

    @Test
    void test_003_001(){
        controller.getFileList("MPA_1", "003", "20200827000000", "20200827120000", null, null);
    }

    @Test
    void test_003_002(){
        controller.getFileList("MPA_NONO", "003", "20200827000000", "20200827120000", null, null);
    }

    String[] getFileList(String machine, String category, String from, String to){
        ResponseEntity<LogFileList> responseEntity =
                controller.getFileList("MPA_1", "003", "20200827000000", "20200827120000", null, null);

        LogFileList logFileList = responseEntity.getBody();

        if (logFileList != null){
            FileInfo[] fileInfoList = logFileList.getList();
            if (fileInfoList != null){
                if (fileInfoList.length > 0){
                    ArrayList<String> fileList = new ArrayList<>();
                    for(FileInfo x : fileInfoList){
                        if (fileList.size() >= 5){
                            break;
                        }
                        if (x.getType().equals("D")){
                            continue;
                        }
                        fileList.add(x.getFilename());
                    }
                    if (fileList.size() == 0){
                        return null;
                    }
                    return fileList.toArray(new String[0]);
                }
            }
        }
        return null;
    }

    @Test
    void test_004_001(){
        String[] fileList = getFileList("MPA_1", "003", "20200827000000", "20200827120000");
        if (fileList != null){
            CreateFtpDownloadRequestParam param = new CreateFtpDownloadRequestParam();
            param.setFileList(fileList);
            param.setCategory("003");
            param.setArchive(true);

            ResponseEntity<FtpDownloadRequestResponse> responseEntity = controller.createFtpDownloadRequest("MPA_1", param);
            FtpDownloadRequestResponse res = responseEntity.getBody();

            controller.getFtpDownloadRequestList();
            controller.getFtpDownloadRequestList("MPA_1");
            controller.getFtpDownloadRequestList("MPA_1", res.getRequestNo());
            controller.cancelAndDeleteFtpDownloadRequest("MPA_1", res.getRequestNo());
        }
    }

    @Test
    void test_004_002(){
        String[] fileList = getFileList("MPA_1", "003", "20200827000000", "20200827120000");
        if (fileList != null){
            CreateFtpDownloadRequestParam param = new CreateFtpDownloadRequestParam();
            param.setFileList(fileList);
            param.setCategory("003");
            param.setArchive(true);

            ResponseEntity<FtpDownloadRequestResponse> responseEntity = controller.createFtpDownloadRequest("MPA_1", param);
            FtpDownloadRequestResponse res = responseEntity.getBody();

            String requestNo = res.getRequestNo();

            {
                ResponseEntity<FtpDownloadRequestListResponse> responseEntity2 = controller.getFtpDownloadRequestList("MPA_1", requestNo);
                FtpDownloadRequestListResponse res2 = responseEntity2.getBody();
                FtpDownloadRequest req = res2.getRequestList()[0];
                while(req.getCompletedTime() == 0){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    responseEntity2 = controller.getFtpDownloadRequestList("MPA_1", requestNo);
                    res2 = responseEntity2.getBody();
                    req = res2.getRequestList()[0];
                }

            }
        }

    }
}
