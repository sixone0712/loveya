package jp.co.canon.ckbs.eec.fs.manage;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpSssListRequestResponse;
import org.junit.jupiter.api.Test;

public class FileServiceManageConnectorTest {
    @Test
    void test_001(){
        FileServiceManageConnector connector = new FileServiceManageConnector("10.1.36.118:8081");

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
}
