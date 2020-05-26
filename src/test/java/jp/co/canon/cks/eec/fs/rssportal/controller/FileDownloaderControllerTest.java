package jp.co.canon.cks.eec.fs.rssportal.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class FileDownloaderControllerTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final FileDownloaderController downloadController;
    private final String host;

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;
    private MockHttpSession session;
    private List<Map<String, Object>> toolList;
    private Map<String, String> logTypeTable;

    @Autowired
    public FileDownloaderControllerTest(FileDownloaderController downloadController) {
        this.downloadController = downloadController;
        host = "http://localhost";
    }

    private StringBuilder getUrl(String servletName) {
        StringBuilder sb = new StringBuilder(host);
        sb.append(":").append(port);
        if(servletName!=null)
            sb.append(servletName);
        return sb;
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        log.info("setup===============================");
        if(session==null) {
            log.info("create session");
            String user = "user";
            String pass = "c4ca4238a0b923820dcc509a6f75849b";

            StringBuilder loginUrl = getUrl("/user/login")
                    .append("?user=").append(user)
                    .append("&password=").append(pass);

            ResultActions result = mockMvc.perform(get(loginUrl.toString()).servletPath("/user/login"));
            result.andExpect(status().isOk()).andExpect(content().string("0"));
            //result.andDo(print());
            session = (MockHttpSession) result.andReturn().getRequest().getSession();
        }

        if(toolList==null) {
            log.info("create tool list");
            StringBuilder createToolListUrl = getUrl("/soap/createToolList");
            String toolListStr = mockMvc.perform(get(createToolListUrl.toString()).servletPath("/soap/createToolList").session(session))
                    //.andDo(print())
                    .andReturn().getResponse().getContentAsString();
            toolList = new ObjectMapper().readValue(toolListStr, new TypeReference<List<Map<String, Object>>>() {});
            for (Map<String, Object> tool : toolList) {
                log.info("structId=" + tool.get("structId") + " target=" + tool.get("targetname"));
            }

            StringBuilder createFileTypeUrl = getUrl("/soap/createFileTypeList");
            createFileTypeUrl.append("?tool=");
            for(Map<String, Object> tool : toolList) {
                String reqUrl = createFileTypeUrl.toString()+tool.get("targetname");
                String fileTypeStr = mockMvc.perform(get(reqUrl).servletPath("/soap/createFileTypeList").session(session))
                        //.andDo(print())
                        .andReturn().getResponse().getContentAsString();
                List<Map<String, Object>> fileTypes = new ObjectMapper().readValue(fileTypeStr, new TypeReference<List<Map<String, Object>>>() {});
                tool.put("fileTypes", fileTypes);
                if(logTypeTable==null)
                    logTypeTable = new HashMap<>();
                for(Map<String, Object> type: fileTypes) {
                    if(!logTypeTable.containsKey(type.get("code")))
                        logTypeTable.put((String)type.get("code"), (String)type.get("logName"));
                }
            }
        }

    }

    private List<Map<String, Object>> createFileListRequestBody(Map<String, Object> tool) {
        assertThat(tool.containsKey("fileTypes")).isTrue();
        List<Map<String, Object>> list = new ArrayList<>();
        for(Map<String, Object> file : (List<Map<String, Object>>)tool.get("fileTypes")) {
            Map<String, Object> item = new HashMap<>();
            item.put("structId", tool.get("structId"));
            item.put("targetName", tool.get("targetname"));
            item.put("targetType", tool.get("targettype"));
            item.put("logType", file.get("logType"));
            item.put("logCode", file.get("code"));
            item.put("startDate", "20200507000015");
            item.put("endDate", "20200507002810");
            item.put("keyword", "");
            item.put("dir", "");
            list.add(item);
        }
        return list;
    }



    @AfterEach
    void tearDown() {
        log.info("tearDown");
    }

//    @Test
    void contextLoads() {
        assertThat(downloadController).isNotNull();
    }


    private Map<String, Object> createDownloadFileRequestBody(Map<String, Object> file) {
        assertThat(file).isNotNull();
        Map<String, Object> map = new HashMap<>();
        map.put("structId", file.get("structId"));
        map.put("machine", file.get("targetName"));
        map.put("category", file.get("logId"));
        map.put("categoryName", logTypeTable.get(file.get("logId")));
        map.put("file", file.get("fileName"));
        map.put("filesize", String.valueOf(((int)file.get("sizeKB"))*1024));
        map.put("date", file.get("fileDate"));
        return map;
    }

    private String requestDownload(Map<String, Object> ... tools) throws Exception {
        log.info("downloadRequest");
        if(tools.length==0)
            return null;

        String createFileListUrl = getUrl("/soap/createFileList").toString();
        for(Map<String, Object> tool: tools) {
            String fileListRequestBodyJson = new ObjectMapper().writeValueAsString(createFileListRequestBody(tool));
            String fileListStr = mockMvc.perform(post(createFileListUrl)
                    .servletPath("/soap/createFileList")
                    .session(session)
                    .content(fileListRequestBodyJson)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    //.andDo(print())
                    .andReturn().getResponse().getContentAsString();
            assertThat(fileListStr).isNotNull();
            List<Map<String, Object>> fileList = new ObjectMapper().readValue(fileListStr,
                    new TypeReference<List<Map<String, Object>>>() {});
            tool.put("fileList", fileList);
        }

        List<Map<String,Object>> downloadListRequestBody = new ArrayList<>();
        for(Map<String, Object> tool: tools) {
            List<Map<String, Object>> files = (List<Map<String, Object>>) tool.get("fileList");
            for(Map<String, Object> file: files) {
                downloadListRequestBody.add(createDownloadFileRequestBody(file));
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", downloadListRequestBody);
        String json = new ObjectMapper().writeValueAsString(map);

        StringBuilder url = getUrl("/dl/request");
        ResultActions result = mockMvc.perform(
                post(url.toString()).servletPath("/dl/request")
                        .session(session)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON));
        //.andDo(print());
        result.andExpect(status().isOk());
        String content = result.andReturn().getResponse().getContentAsString();
        assertThat(content).isNotNull();
        log.info("downloadId="+content);
        return content;
    }

    @Test
    void request() throws Exception {
        final int testToolCount = 2;
        log.info("test dl/request");

        String createFileListUrl = getUrl("/soap/createFileList").toString();
        for(int i=0; i<testToolCount; ++i) {
            if(!toolList.get(i).containsKey("fileList")) {
                String fileListRequestBodyJson = new ObjectMapper().writeValueAsString(createFileListRequestBody(toolList.get(i)));
                String fileListStr = mockMvc.perform(post(createFileListUrl)
                        .servletPath("/soap/createFileList")
                        .session(session)
                        .content(fileListRequestBodyJson)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        //.andDo(print())
                        .andReturn().getResponse().getContentAsString();
                assertThat(fileListStr).isNotNull();
                log.info(toolList.get(i).get("targetname") + " fileList= " + fileListStr);
                List<Map<String, Object>> fileList = new ObjectMapper().readValue(fileListStr, new TypeReference<List<Map<String, Object>>>() {});
                toolList.get(i).put("fileList", fileList);
            }
        }

        List<Map<String,Object>> downloadListRequestBody = new ArrayList<>();
        for(int i=0; i<testToolCount; ++i) {
            Map<String, Object> tool = toolList.get(i);
            List<Map<String, Object>> files = (List<Map<String, Object>>) tool.get("fileList");
            for(Map<String, Object> file: files) {
                downloadListRequestBody.add(createDownloadFileRequestBody(file));
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", downloadListRequestBody);
        String json = new ObjectMapper().writeValueAsString(map);

        StringBuilder url = getUrl("/dl/request");
        ResultActions result = mockMvc.perform(
                post(url.toString()).servletPath("/dl/request")
                        .session(session)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON));
                //.andDo(print());
        result.andExpect(status().isOk());
        String content = result.andReturn().getResponse().getContentAsString();
        assertThat(content).isNotNull();
        log.info("downloadId="+content);
    }


    @Test
    @Timeout(300)
    void getStatus() throws Exception {
        log.info("test /dl/status");
        assertThat(toolList).isNotNull();
        String downloadId = requestDownload(toolList.get(0), toolList.get(1));
        assertThat(downloadId).isNotNull();

        String statusUrl = getUrl("/dl/status").append("?dlId=").append(downloadId).toString();

        while(true) {
            String content = mockMvc.perform(get(statusUrl).servletPath("/dl/status").session(session))
                    //.andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            assertThat(content).isNotNull();
            Map<String, Object> response = new ObjectMapper().readValue(content, Map.class);
            String status = (String) response.get("status");
            log.info(String.format("%s %d/%d", status, response.get("downloadFiles"), response.get("totalFiles")));
            if(status.equalsIgnoreCase("done"))
                break;
            Thread.sleep(3000);
        }
    }

    @Test
    @Timeout(360)
    void downloadFile() throws Exception {
        log.info("test /dl/download");
        assertThat(toolList).isNotNull();

        String downloadId = requestDownload(toolList.get(0), toolList.get(1));
        assertThat(downloadId).isNotNull();

        String statusUrl = getUrl("/dl/status").append("?dlId=").append(downloadId).toString();
        while(true) {
            String content = mockMvc.perform(get(statusUrl).servletPath("/dl/status").session(session))
                    //.andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            assertThat(content).isNotNull();
            Map<String, Object> response = new ObjectMapper().readValue(content, Map.class);
            String status = (String) response.get("status");
            log.info(String.format("%s %d/%d", status, response.get("downloadFiles"), response.get("totalFiles")));
            if(status.equalsIgnoreCase("done"))
                break;
            Thread.sleep(3000);
        }

        String downloadUrl = getUrl("/dl/download").append("?dlId=").append(downloadId).toString();
        mockMvc.perform(get(downloadUrl).session(session))
                .andExpect(status().isOk());

    }

    @Test
    void cancelDownload() throws Exception {
        log.info("test /dl/cancel");
        String downloadId = requestDownload(toolList.get(0));
        assertThat(downloadId).isNotNull();

        String statusUrl = getUrl("/dl/status").append("?dlId=").append(downloadId).toString();
        while(true) {
            String content = mockMvc.perform(get(statusUrl).servletPath("/dl/status").session(session))
                    //.andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            assertThat(content).isNotNull();
            Map<String, Object> response = new ObjectMapper().readValue(content, Map.class);
            String status = (String) response.get("status");
            log.info(String.format("%s %d/%d", status, response.get("downloadFiles"), response.get("totalFiles")));
            if(status.equalsIgnoreCase("in-progress"))
                break;
            Thread.sleep(1000);
        }

        String cancelUrl = getUrl("/dl/cancel").append("?dlId=").append(downloadId).toString();
        mockMvc.perform(get(cancelUrl).session(session))
                .andExpect(status().isOk());
    }

}