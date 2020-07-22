package jp.co.canon.ckbs.eec.fs.manage;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpCompatDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpSssDownloadRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpSssListRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpCompatDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssDownloadRequest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class DefaultFileServiceManageConnectorTest {
    @Test
    void test_001(){
        DefaultFileServiceManageConnector connector = new DefaultFileServiceManageConnector("10.1.36.118:8081");

        VFtpSssListRequestResponse res = connector.createVFtpSssListRequest("MPA_1", "IP_AS_RAW_AAA");
        connector.cancelAndDeleteVFtpSssListRequest("MPA_1", res.getRequest().getRequestNo());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        res = connector.getVFtpSssListRequest("MPA_1", res.getRequestNo());

        System.out.println(res);

    }

    @Test
    void test_002(){
        DefaultFileServiceManageConnector connector = new DefaultFileServiceManageConnector("10.1.36.118:8081");

        List<String> fileList = new ArrayList<>();
        fileList.add("abcdefg.log");
        VFtpSssDownloadRequestResponse res =
                connector.createVFtpSssDownloadRequest("MPA_1",
                        "IP_AS_RAW_AAA",
                        fileList.toArray(new String[0]),
                        false);

        while (res.getRequest().getStatus() != VFtpSssDownloadRequest.Status.EXECUTED){
            res = connector.getVFtpSssDownloadRequest("MPA_1", res.getRequest().getRequestNo());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(res);
    }

    @Test
    void test_003(){
        DefaultFileServiceManageConnector connector = new DefaultFileServiceManageConnector("10.1.36.118:8081");

        VFtpCompatDownloadRequestResponse res =
                connector.createVFtpCompatDownloadRequest("MPA_1", "aaa.log", false);

        while(res.getRequest().getStatus() != VFtpCompatDownloadRequest.Status.EXECUTED){
            res = connector.getVFtpCompatDownloadRequest("MPA_1", res.getRequest().getRequestNo());
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(res);
    }
}
