package jp.co.canon.cks.eec.fs.rssportal.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.canon.cks.eec.fs.rssportal.model.*;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileDownloaderControllerTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final FileDownloaderController downloadController;
    private final FileServiceController fileServiceController;

    @Autowired
    public FileDownloaderControllerTest(FileDownloaderController downloadController,
                                        FileServiceController fileServiceController) {
        this.downloadController = downloadController;
        this.fileServiceController = fileServiceController;
    }

    @Test
    @Timeout(300)
    void request() throws Exception {
        String downloadId = requestDownload();
        assertNotNull(downloadId);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        ResponseEntity<InputStreamResource> responseEntity;
        request.setServletPath("download");
        responseEntity = downloadController.downloadFile(downloadId, request, response);
        assertNull(responseEntity);

        responseEntity = downloadController.downloadFile("", request, response);
        assertNull(responseEntity);

        responseEntity = downloadController.downloadFile("hello", request, response);
        assertNull(responseEntity);

        request.setServletPath("/rss/rest/dl/status");
        Map<String, Object> param = new HashMap<>();
        param.put("dlId", downloadId);
        while(true) {
            DownloadStatusResponseBody responseBody = downloadController.getStatus(request, param);
            assertNotNull(responseBody);
            if(responseBody.getStatus().equalsIgnoreCase("done")) {
                break;
            }
            Thread.sleep(500);
        }
        MockHttpSession session = new MockHttpSession();
        SessionContext sessionContext = new SessionContext();
        sessionContext.setAuthorized(true);
        UserVo user = new UserVo();
        user.setUsername("user");
        sessionContext.setUser(user);
        session.setAttribute("context", sessionContext);

        // set dummy session to create file name.
        Field sessionField = FileDownloaderController.class.getDeclaredField("session");
        sessionField.setAccessible(true);
        sessionField.set(downloadController, session);

        request.setServletPath("/rss/rest/dl/download");

        responseEntity = downloadController.downloadFile(downloadId, request, response);
        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
    }



    @Test
    void getStatus() {
        // normal operation will be tested in request() testing.
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/dl/status");
        Map<String, Object> param = new HashMap<>();
        DownloadStatusResponseBody response = downloadController.getStatus(request, param);
        param.put("dlId", "hello");
        response = downloadController.getStatus(request, param);
        assertNull(response);
    }

    @Test
    void downloadFile() {
        // this method will be tested in request() testing.
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("downloadFile");
        MockHttpServletResponse response = new MockHttpServletResponse();
        ResponseEntity<InputStreamResource> responseEntity =
                downloadController.downloadFile("hello", request, response);
        assertEquals(responseEntity, null);
    }

    @Test
    void cancelDownload() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("cancel");
        ResponseEntity<String> response = downloadController.cancelDownload(request, "hello");
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    private String requestDownload() throws Exception {
        RSSToolInfo toolInfo = Objects.requireNonNull(fileServiceController.createToolList().getBody())[0];
        assertNotNull(toolInfo);
        RSSLogInfoBean[] logInfos = fileServiceController.createFileTypeList(toolInfo.getTargetname()).getBody();
        assertNotNull(logInfos);
        assertNotEquals(logInfos.length, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        long endMillis = System.currentTimeMillis();
        long startMillis = endMillis-(24*3600000);

        RSSRequestSearch[] requestList = new RSSRequestSearch[logInfos.length];
        for(int i=0; i<logInfos.length; ++i) {
            requestList[i] = new RSSRequestSearch();
            requestList[i].setStructId(toolInfo.getStructId());
            requestList[i].setTargetName(toolInfo.getTargetname());
            requestList[i].setTargetType(toolInfo.getTargettype());
            requestList[i].setLogType(logInfos[i].getLogType());
            requestList[i].setLogCode(logInfos[i].getCode());
            requestList[i].setLogName(logInfos[i].getLogName());
            requestList[i].setStartDate(dateFormat.format(startMillis));
            requestList[i].setEndDate(dateFormat.format(endMillis));
            requestList[i].setKeyword("");
            requestList[i].setDir("");

        }
        RSSFileInfoBeanResponse[] fileList = fileServiceController.createFileList(requestList).getBody();
        assertNotNull(fileList);
        assertNotEquals(fileList.length, 0);

        // request download
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/dl/request");
        Map<String, Object> param = new HashMap<>();
        assertNull(downloadController.request(request, param));
        List<Map<String, Object>> mapList = new ArrayList<>();
        for(RSSFileInfoBeanResponse file: fileList) {
            if(mapList.size()>1000)
                break;

            Map<String, Object> fileMap = new HashMap<>();
            fileMap.put("structId", file.getStructId());
            fileMap.put("machine", file.getTargetName());
            fileMap.put("category", file.getLogId());
            fileMap.put("categoryName", file.getLogName());
            fileMap.put("file", file.getFileName());
            fileMap.put("filesize", String.valueOf(file.getFileSize()));
            fileMap.put("date", file.getFileDate());
            mapList.add(fileMap);
        }
        param.put("list", mapList);
        String downloadId = downloadController.request(request, param);
        assertNotNull(downloadId);
        return downloadId;
    }
}