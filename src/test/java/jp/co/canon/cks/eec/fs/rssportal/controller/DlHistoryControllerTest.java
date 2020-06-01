package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.service.UserService;
import jp.co.canon.cks.eec.fs.rssportal.service.UserServiceImpl;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DlHistoryControllerTest {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DlHistoryController dlHistoryController;
    private final String HISTORY_RESULT = "result";
    private final String HISTORY_DATA = "data";

    @Autowired
    private MockMvc mockMvc;
    private MockHttpSession session;
    private SessionContext context;

    @Autowired
    public  DlHistoryControllerTest(DlHistoryController dlHistoryController) {
        this.dlHistoryController = dlHistoryController;
    }
    @BeforeEach
    void setUp() throws Exception {
        log.info("setup===============================");
        if(session==null) {
            log.info("create session");
            String user = "ymkwon";
            String pass = "c4ca4238a0b923820dcc509a6f75849b";
            String loginUrl = "http://localhost:8080/rss/rest/user/login" +
                                "?user=" + user + "&password=" + pass;
            log.info("loginUrl  : "+ loginUrl);
            Map<String, Object> res = new HashMap<>();
            res.put("error", 0);
            res.put("name", "ymkwon");
            res.put("auth", "100");

            ResultActions result = mockMvc.perform(get(loginUrl).servletPath("/rss/rest/user/login"));
            session = (MockHttpSession) result.andReturn().getRequest().getSession();
            context = (SessionContext) result.andReturn().getRequest().getAttribute("context");
            if(context != null)
            {
                SessionContext context = new SessionContext();
                UserVo LoginUser = new UserVo();
                context.setUser(LoginUser);
                context.setAuthorized(true);
                session.setAttribute("context", context);
            }
        }
        log.info("setup===============================End");
    }

    @Test
    void getHistoryList() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/dlHistory/getHistoryList");
        Map<String, Object> resp = null;

        /*test 1 - in case : History is nothing*/
        resp = dlHistoryController.getHistoryList();
        assertEquals(-1, resp.get(HISTORY_RESULT));
        assertNull(resp.get(HISTORY_DATA));
    }

    @Test
    void addDlHistory() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/dlHistory/addDlHistory");
        boolean resp = false;
        Map<String, Object> param = new HashMap<>();

        /*test 1 - in case : param is null*/
        resp = dlHistoryController.addDlHistory(null);
        assertFalse(resp);

        /*test 2 - in case : context is not empty */
/*        dlHistoryController.setHttpSession(session);*/
        param.put("type",1);
        param.put("filename","ymkwon_CR7_20200526_182447.zip");
        param.put("status","User Cancel");

        resp = dlHistoryController.addDlHistory(param);
        assertTrue(resp);

        /*test 3 - in case : context is null*/
        session.setAttribute("context", null);
        /*dlHistoryController.setHttpSession(session);*/
        param.put("type",1);
        param.put("filename","ymkwon_CR7_20200526_182447.zip");
        param.put("status","not login");

        resp = dlHistoryController.addDlHistory(param);
        assertFalse(resp);
    }

    @Test
    void getHistoryListNotEmpty() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/dlHistory/getHistoryList");
        Map<String, Object> resp = null;

        /*test 1 - in case : is somethins*/
        resp = dlHistoryController.getHistoryList();
        assertEquals(0, resp.get(HISTORY_RESULT));
        assertNotNull(resp.get(HISTORY_DATA));
    }

}