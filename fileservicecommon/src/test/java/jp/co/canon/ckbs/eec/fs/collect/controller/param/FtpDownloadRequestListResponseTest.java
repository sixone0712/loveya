package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class FtpDownloadRequestListResponseTest {
    @Test
    void test_001(){
        FtpDownloadRequestListResponse response = new FtpDownloadRequestListResponse();
        response.setErrorCode("400");
        Assertions.assertEquals("400", response.getErrorCode());
        response.setErrorMessage("error");
        Assertions.assertEquals("error", response.getErrorMessage());

        ArrayList<FtpDownloadRequest> reqList = new ArrayList<>();
        FtpDownloadRequest req = new FtpDownloadRequest();
        req.setRequestNo("AAA");
        reqList.add(req);

        response.setRequestList(reqList.toArray(new FtpDownloadRequest[0]));
        Assertions.assertArrayEquals(reqList.toArray(new FtpDownloadRequest[0]), response.getRequestList());
    }
}
