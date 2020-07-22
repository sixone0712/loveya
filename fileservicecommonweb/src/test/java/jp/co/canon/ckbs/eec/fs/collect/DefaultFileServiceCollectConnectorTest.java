package jp.co.canon.ckbs.eec.fs.collect;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.CreateFtpDownloadRequestParam;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestListResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpSssDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.RequestFileInfo;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.service.LogFileList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class DefaultFileServiceCollectConnectorTest {
    @Test
    void test_001(){
        DefaultFileServiceCollectConnector connector = new DefaultFileServiceCollectConnector("10.1.36.118:8080");

        LogFileList logFileList =
                connector.getFtpFileList("MPA_2", "002", "20200601000000", "20200706235959", "", "");
        System.out.println("AAA");
    }

    @Test
    void test_002(){
        DefaultFileServiceCollectConnector connector = new DefaultFileServiceCollectConnector("10.1.36.118:8080");

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
        DefaultFileServiceCollectConnector connector = new DefaultFileServiceCollectConnector(("10.1.36.118:8080"));
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
        DefaultFileServiceCollectConnector connector = new DefaultFileServiceCollectConnector(("10.1.36.118:8080"));
        FtpDownloadRequestListResponse res = connector.getFtpDownloadRequestList("MPA_1", "AAA");
        System.out.println(res);
    }

    @Test
    void test_005(){
        DefaultFileServiceCollectConnector connector = new DefaultFileServiceCollectConnector(("10.1.36.118:8080"));
        List<String> fileList = new ArrayList<>();
        fileList.add("abcdefg.log");

        VFtpSssDownloadRequestResponse res = connector.createVFtpSssDownloadRequest("MPA_1", "IP_AS_RAW_AAA", fileList.toArray(new String[0]), true);

        VFtpSssDownloadRequest request;
        res = connector.getVFtpSssDownloadRequest("MPA_1", res.getRequest().getRequestNo());
        request = res.getRequest();
        while(request.getStatus() != VFtpSssDownloadRequest.Status.EXECUTED){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            res = connector.getVFtpSssDownloadRequest("MPA_1", res.getRequest().getRequestNo());
            request = res.getRequest();
        }

        RequestFileInfo[] fileArr = request.getFileList();
        System.out.println(fileArr);
    }
}
