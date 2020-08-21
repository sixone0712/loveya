package jp.co.canon.ckbs.eec.fs.collect.controller.param;

import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.model.FtpRequest;
import jp.co.canon.ckbs.eec.fs.collect.model.RequestFileInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FtpDownloadRequestResponseTest {
    @Test
    void test_001(){
        FtpDownloadRequestResponse res = new FtpDownloadRequestResponse();

        res.setErrorMessage("message1");
        Assertions.assertEquals("message1", res.getErrorMessage());

        res.setErrorCode("400");
        Assertions.assertEquals("400", res.getErrorCode());

        FtpDownloadRequest request = new FtpDownloadRequest();

        request.setCategory("003");
        Assertions.assertEquals("003", request.getCategory());

        request.setResult("OK");
        Assertions.assertEquals("OK", request.getResult());

        request.setArchive(false);
        Assertions.assertFalse(request.isArchive());

        request.setArchiveFileName("abc.zip");
        Assertions.assertEquals("abc.zip", request.getArchiveFileName());

        request.setArchiveFileSize(1024);
        Assertions.assertEquals(1024, request.getArchiveFileSize());

        request.setArchiveFilePath("/LOG/downloads/AAA/aaa.zip");
        Assertions.assertEquals("/LOG/downloads/AAA/aaa.zip", request.getArchiveFilePath());

        ArrayList<RequestFileInfo> requestFileInfos = new ArrayList<>();
        RequestFileInfo rfileInfo = new RequestFileInfo("abc.txt");
        requestFileInfos.add(rfileInfo);

        request.setFileInfos(requestFileInfos.toArray(new RequestFileInfo[0]));

        RequestFileInfo[] fileInfos = request.getFileInfos();
        Assertions.assertArrayEquals(requestFileInfos.toArray(new RequestFileInfo[0]), fileInfos);

        request.fileDownloaded("abc.zip", 300);
        request.fileDownloaded("abc.txt", 300);
        request.fileDownloaded("abc.txt", 500);

        request.setStatus(FtpRequest.Status.EXECUTING);
        Assertions.assertFalse(FtpDownloadRequest.checkCompletedStatus(request));

        {
            FtpDownloadRequest r = new FtpDownloadRequest();

            r.setStatus(FtpRequest.Status.CANCEL);
            Assertions.assertTrue(FtpDownloadRequest.checkCompletedStatus(r));
        }
        {
            FtpDownloadRequest r = new FtpDownloadRequest();

            r.setStatus(FtpRequest.Status.ERROR);
            Assertions.assertTrue(FtpDownloadRequest.checkCompletedStatus(r));
        }
        {
            FtpDownloadRequest r = new FtpDownloadRequest();

            r.setStatus(FtpRequest.Status.EXECUTED);
            Assertions.assertTrue(FtpDownloadRequest.checkCompletedStatus(r));
        }
    }

    @Test
    void test_002(){
        FtpDownloadRequest request = new FtpDownloadRequest();

        request.setCategory("003");
        Assertions.assertEquals("003", request.getCategory());

        request.setDirectory("abc");
        Assertions.assertEquals("abc", request.getDirectory());

        request.setResult("OK");
        Assertions.assertEquals("OK", request.getResult());

        request.setArchive(true);
        Assertions.assertTrue(request.isArchive());

        request.setArchiveFileName("abc.zip");
        Assertions.assertEquals("abc.zip", request.getArchiveFileName());

        request.setArchiveFileSize(1024);
        Assertions.assertEquals(1024, request.getArchiveFileSize());

        request.setArchiveFilePath("/LOG/downloads/AAA/aaa.zip");
        Assertions.assertEquals("/LOG/downloads/AAA/aaa.zip", request.getArchiveFilePath());

        ArrayList<RequestFileInfo> requestFileInfos = new ArrayList<>();
        RequestFileInfo rfileInfo = new RequestFileInfo("abc.txt");
        requestFileInfos.add(rfileInfo);

        request.setFileInfos(requestFileInfos.toArray(new RequestFileInfo[0]));

        RequestFileInfo[] fileInfos = request.getFileInfos();
        Assertions.assertArrayEquals(requestFileInfos.toArray(new RequestFileInfo[0]), fileInfos);

        request.fileDownloaded("abc.zip", 300);
        request.fileDownloaded("abc.txt", 300);
        request.fileDownloaded("abc.txt", 500);

        long count = request.getDownloadedFileCount();
        Assertions.assertEquals(1, count);

        long total_count = request.getTotalFileCount();
        Assertions.assertEquals(1, total_count);
    }

    @Test
    void test_003(){
        FtpDownloadRequest request = new FtpDownloadRequest();

        request.setMachine("MPA_1");
        Assertions.assertEquals("MPA_1", request.getMachine());

        request.setCategory("003");
        Assertions.assertEquals("003", request.getCategory());

        request.setResult("OK");
        Assertions.assertEquals("OK", request.getResult());

        request.setArchive(false);
        Assertions.assertFalse(request.isArchive());

        request.setArchiveFileName("abc.zip");
        Assertions.assertEquals("abc.zip", request.getArchiveFileName());

        request.setArchiveFileSize(1024);
        Assertions.assertEquals(1024, request.getArchiveFileSize());

        request.setArchiveFilePath("/LOG/downloads/AAA/aaa.zip");
        Assertions.assertEquals("/LOG/downloads/AAA/aaa.zip", request.getArchiveFilePath());

        ArrayList<RequestFileInfo> requestFileInfos = new ArrayList<>();
        RequestFileInfo rfileInfo = new RequestFileInfo("abc.txt");
        requestFileInfos.add(rfileInfo);

        request.setFileInfos(requestFileInfos.toArray(new RequestFileInfo[0]));

        RequestFileInfo[] fileInfos = request.getFileInfos();
        Assertions.assertArrayEquals(requestFileInfos.toArray(new RequestFileInfo[0]), fileInfos);

        request.fileDownloaded("abc.zip", 300);
        request.fileDownloaded("abc.txt", 300);
        request.fileDownloaded("abc.txt", 500);

        request.setStatus(FtpRequest.Status.EXECUTING);
        request.setTimestamp(1);
        Assertions.assertEquals(1, request.getTimestamp());

        request.setCompletedTime(2);
        Assertions.assertEquals(2, request.getCompletedTime());

        request.setErrorMessage("error 111");
        Assertions.assertEquals("error 111", request.getErrorMessage());
        FtpDownloadRequestResponse res = FtpDownloadRequestResponse.fromRequest(request);
        Assertions.assertEquals(request.getRequestNo(), res.getRequestNo());
        Assertions.assertEquals(request.getMachine(), res.getMachine());
        Assertions.assertEquals(request.getCategory(), res.getCategory());
        Assertions.assertEquals(request.getTimestamp(), res.getTimestamp());
        Assertions.assertEquals(request.getStatus(), res.getStatus());
        Assertions.assertEquals(request.getResult(), res.getResult());
        Assertions.assertEquals(request.getCompletedTime(), res.getCompletedTime());
        Assertions.assertEquals(request.isArchive(), res.isArchive());
        Assertions.assertArrayEquals(request.getFileInfos(), res.getFileInfos());
        Assertions.assertEquals(request.getDirectory(), res.getDirectory());
        Assertions.assertEquals(request.getErrorMessage(), res.getErrorMessage());
        Assertions.assertNull(res.getErrorCode());

        res.setErrorCode("404");
        Assertions.assertEquals("404", res.getErrorCode());
    }
}
