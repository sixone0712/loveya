package jp.co.canon.cks.eec.fs.rssportal.vftp.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import jp.co.canon.cks.eec.fs.rssportal.RssportalApplication;
import jp.co.canon.cks.eec.fs.rssportal.vftp.FileDownloadStatus;
import jp.co.canon.cks.eec.fs.rssportal.vftp.FileListStatus;
import jp.co.canon.cks.eec.fs.rssportal.vftp.controller.VFtpController.CompatGetRequestParam;
import jp.co.canon.cks.eec.fs.rssportal.vftp.controller.VFtpController.SssGetItem;
import jp.co.canon.cks.eec.fs.rssportal.vftp.controller.VFtpController.SssGetRequestParam;
import jp.co.canon.cks.eec.fs.rssportal.vftp.controller.VFtpController.SssListRequestParam;

@SpringBootTest(classes = RssportalApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("vftptest3")
public class VFtpControllerTest02 {
    @Autowired
    VFtpController controller;

    @Test
    public void test_001() {
        Assertions.assertNotNull(controller);
    }

    @Test
    public void test_002() {
        controller.deleteCompatGetRequest("ABCDEFG");
    }

    @Test
    public void test_003() {
        controller.deleteSssGetRequest("ABCDEFG");
    }

    @Test
    public void test_004() {
        controller.deleteSssListRequest("ABCDEFG");
    }

    @Test
    public void test_postSssListRequest_001() {
        SssListRequestParam param = new SssListRequestParam();
        param.setDirectory("1234");
        ResponseEntity<?> responseEntity = controller.postSssListRequest(param);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void test_postSssListRequest_002() {
        SssListRequestParam param = new SssListRequestParam();
        param.setDirectory("IP_AS_RAW-20200510_010101-20200520_010101");
        ResponseEntity<?> responseEntity = controller.postSssListRequest(param);
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void test_postSssListRequest_003() {
        SssListRequestParam param = new SssListRequestParam();
        param.setDirectory("IP_AS_RAW-20200510_010101-20200520_010101-DE_TTT");
        ResponseEntity<?> responseEntity = controller.postSssListRequest(param);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void test_postSssListRequest_004() {
        SssListRequestParam param = new SssListRequestParam();
        param.setDirectory("IP_AS_RAW-20200510_010101-20200520_010101-DE_MPA1");
        ResponseEntity<?> responseEntity = controller.postSssListRequest(param);
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        EntityModel<FileListStatus> entityModel;
        entityModel = (EntityModel<FileListStatus>) responseEntity.getBody();
        FileListStatus sts = entityModel.getContent();
        String requestNo = sts.getRequestNo();

        responseEntity = controller.getSssListRequest(requestNo);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        entityModel = (EntityModel<FileListStatus>) responseEntity.getBody();
        sts = entityModel.getContent();

        controller.deleteSssListRequest(requestNo);
    }

    @Test
    public void test_getSssListRequest_001() {
        ResponseEntity<?> responseEntity = controller.getSssListRequest("ABCDEFG");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void test_postSssGetRequest_001() {
        ResponseEntity<?> responseEntity = controller.postSssGetRequest(null);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void test_postSssGetRequest_002() {
        SssGetRequestParam param = new SssGetRequestParam();
        ArrayList<SssGetItem> list = new ArrayList<>();
        param.setList(list);
        ResponseEntity<?> responseEntity = controller.postSssGetRequest(param);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void test_postSssGetRequest_003() {
        SssGetRequestParam param = new SssGetRequestParam();
        // SssGetItem
        ArrayList<SssGetItem> list = new ArrayList<>();
        SssGetItem item = new SssGetItem();
        item.setServer(null);
        item.setPath("/VROOT/SSS/Optional/IP_AS_RAW-20200510_010101-20200520_010101-DE_MPA1");
        item.setFilename("abcdefg.txt");
        list.add(item);
        param.setList(list);
        ResponseEntity<?> responseEntity = controller.postSssGetRequest(param);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void test_postSssGetRequest_004() {
        SssGetRequestParam param = new SssGetRequestParam();
        // SssGetItem
        ArrayList<SssGetItem> list = new ArrayList<>();
        SssGetItem item = new SssGetItem();
        item.setServer("OTS01_FS");
        item.setPath(null);
        item.setFilename("abcdefg.txt");
        list.add(item);
        param.setList(list);
        ResponseEntity<?> responseEntity = controller.postSssGetRequest(param);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void test_postSssGetRequest_005() {
        SssGetRequestParam param = new SssGetRequestParam();
        // SssGetItem
        ArrayList<SssGetItem> list = new ArrayList<>();
        SssGetItem item = new SssGetItem();
        item.setServer("OTS01_FS");
        item.setPath("/VROOT/SSS/Optional/IP_AS_RAW-20200510_010101-20200520_010101-DE_MPA1");
        item.setFilename(null);
        list.add(item);
        param.setList(list);
        ResponseEntity<?> responseEntity = controller.postSssGetRequest(param);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void test_postSssGetRequest_006() {
        SssGetRequestParam param = new SssGetRequestParam();
        // SssGetItem
        ArrayList<SssGetItem> list = new ArrayList<>();
        SssGetItem item = new SssGetItem();
        item.setServer("TTT");
        item.setPath("/VROOT/SSS/Optional/IP_AS_RAW-20200510_010101-20200520_010101-DE_MPA1");
        item.setFilename("abcdefg.txt");
        list.add(item);
        param.setList(list);
        ResponseEntity<?> responseEntity = controller.postSssGetRequest(param);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void test_postSssGetRequest_007() {
        SssGetRequestParam param = new SssGetRequestParam();
        // SssGetItem
        ArrayList<SssGetItem> list = new ArrayList<>();
        SssGetItem item = new SssGetItem();
        item.setServer("OTS01_FS");
        item.setPath("/VROOT/SSS/IP_AS_RAW-20200510_010101-20200520_010101-DE_MPA1");
        item.setFilename("abcdefg.txt");
        list.add(item);
        param.setList(list);
        ResponseEntity<?> responseEntity = controller.postSssGetRequest(param);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void test_postSssGetRequest_010() {
        SssGetRequestParam param = new SssGetRequestParam();
        // SssGetItem
        ArrayList<SssGetItem> list = new ArrayList<>();
        SssGetItem item = new SssGetItem();
        item.setServer("OTS01_FS");
        item.setPath("/VROOT/SSS/Optional/IP_AS_RAW-20200510_010101-20200520_010101-DE_MPA1");
        item.setFilename("abcdefg.txt");
        list.add(item);
        param.setList(list);
        ResponseEntity<?> responseEntity = controller.postSssGetRequest(param);
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        EntityModel<?> entityModel;
        Object body = responseEntity.getBody();
        if (!(body instanceof EntityModel)) {
            Assertions.fail();
            return;
        }
        entityModel = (EntityModel<?>) body;
        Object content = entityModel.getContent();

        if (!(content instanceof FileDownloadStatus)) {
            Assertions.fail();
            return;
        }
        FileDownloadStatus sts = (FileDownloadStatus) content;
        String requestNo = sts.getRequestNo();

        responseEntity = controller.getSssGetRequest(requestNo);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void test_getSssGetRequest_011() {
        ResponseEntity<?> responseEntity = controller.getSssGetRequest("ABCDEFG");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void test_getSssGetRequestDownload_001() {
        ResponseEntity<?> responseEntity = controller.getSssGetRequestDownload("ABCDEFG");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void test_getSssGetRequestDownload_002() {
        SssGetRequestParam param = new SssGetRequestParam();
        // SssGetItem
        ArrayList<SssGetItem> list = new ArrayList<>();
        SssGetItem item = new SssGetItem();
        item.setServer("OTS01_FS");
        item.setPath("/VROOT/SSS/Optional/IP_AS_RAW-20200510_010101-20200520_010101-DE_MPA1");
        item.setFilename("mfp.axf");
        list.add(item);
        param.setList(list);
        ResponseEntity<?> responseEntity = controller.postSssGetRequest(param);
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        EntityModel<?> entityModel;
        Object body = responseEntity.getBody();
        if (!(body instanceof EntityModel)) {
            Assertions.fail();
            return;
        }
        entityModel = (EntityModel<?>) body;
        Object content = entityModel.getContent();

        if (!(content instanceof FileDownloadStatus)) {
            Assertions.fail();
            return;
        }
        FileDownloadStatus sts = (FileDownloadStatus) content;
        String requestNo = sts.getRequestNo();

        responseEntity = controller.getSssGetRequestDownload(requestNo);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        HttpStatus httpStatus = responseEntity.getStatusCode();
        while (httpStatus == HttpStatus.NOT_FOUND) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            responseEntity = controller.getSssGetRequestDownload(requestNo);
            httpStatus = responseEntity.getStatusCode();
            System.out.println(httpStatus);
        }
        Assertions.assertEquals(HttpStatus.OK, httpStatus);
        InputStreamResource isr = (InputStreamResource) responseEntity.getBody();
        try {
            isr.getInputStream().close();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_postCompatGetRequest_001() {
        CompatGetRequestParam param = new CompatGetRequestParam();
        param.setFilename("1212.log");

        ResponseEntity<?> responseEntity = null;

        responseEntity = controller.postCompatGetRequest(param);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void test_postCompatGetRequest_003() {
        CompatGetRequestParam param = new CompatGetRequestParam();
        param.setFilename("20200528_112222-20200528_112233-DE_MPA1.log");

        ResponseEntity<?> responseEntity = null;

        responseEntity = controller.postCompatGetRequest(param);
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        EntityModel<?> entityModel;
        Object body = responseEntity.getBody();
        if (!(body instanceof EntityModel)) {
            Assertions.fail();
            return;
        }
        entityModel = (EntityModel<?>) body;
        Object content = entityModel.getContent();

        if (!(content instanceof FileDownloadStatus)) {
            Assertions.fail();
            return;
        }
        FileDownloadStatus sts = (FileDownloadStatus) content;
        String requestNo = sts.getRequestNo();

        responseEntity = controller.getCompatGetRequest(requestNo);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        responseEntity = controller.getCompatGetRequestDownload(requestNo);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        HttpStatus httpStatus = responseEntity.getStatusCode();
        while (httpStatus == HttpStatus.NOT_FOUND) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            responseEntity = controller.getCompatGetRequestDownload(requestNo);
            httpStatus = responseEntity.getStatusCode();
        }
    }
    
    @Test
    public void test_getCompatGetRequest_001(){
        ResponseEntity<?> responseEntity = null;
        responseEntity = controller.getCompatGetRequest("ABCDEFG");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void test_getCompatGetRequestDownload_001(){
        ResponseEntity<?> responseEntity = null;
        responseEntity = controller.getCompatGetRequestDownload("ABCDEFG");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}