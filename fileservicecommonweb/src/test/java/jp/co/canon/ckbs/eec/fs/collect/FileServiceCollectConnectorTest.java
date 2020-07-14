package jp.co.canon.ckbs.eec.fs.collect;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.CreateFtpDownloadRequestParam;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestListResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class FileServiceCollectConnectorTest {
    @Test
    void test_001(){
        FileServiceCollectConnector connector = new FileServiceCollectConnector("10.1.36.118:8080");

        connector.getFtpFileList("MPA_2", "002", "20200601000000", "20200706235959", "", "");

    }

    @Test
    void test_002(){
        FileServiceCollectConnector connector = new FileServiceCollectConnector("10.1.36.118:8080");

        ArrayList<String> fileList = new ArrayList<>();

        fileList.add("20200629225500");
        fileList.add("20200629223000");

        CreateFtpDownloadRequestParam param = new CreateFtpDownloadRequestParam();
        param.setCategory("002");
        param.setFileList(fileList.toArray(new String[0]));
        param.setArchive(true);

        FtpDownloadRequestResponse res = connector.createFtpDownloadRequest("MPA_2", param);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(res);
    }

    @Test
    void test_003(){
        FileServiceCollectConnector connector = new FileServiceCollectConnector(("10.1.36.118:8080"));
        ArrayList<String> fileList = new ArrayList<>();

        fileList.add("20200629225500");
        fileList.add("20200629223000");

        FtpDownloadRequestResponse res =
                connector.createFtpDownloadRequest("MPA_2",
                        "002",
                        true,
                        fileList.toArray(new String[0]));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(res);
    }

    @Test
    void test_004(){
        FileServiceCollectConnector connector = new FileServiceCollectConnector(("10.1.36.118:8080"));
        FtpDownloadRequestListResponse res = connector.getFtpDownloadRequestList("MPA_1", "AAA");
        System.out.println(res);
    }
}
