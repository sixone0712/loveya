package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserController userController;
    private final String USER_NAME = "name";
    private final String USER_AUTH = "auth";

    @Autowired
    private MockMvc mockMvc;
    private MockHttpSession session;

    @Autowired
    public UserControllerTest(UserController userController) {
        this.userController = userController;
    }

    @BeforeEach
    void setUp() throws Exception {
        log.info("setup===============================");
        if(session==null) {
            log.info("create session");
            String user = "ymkwon";
            String pass = "c4ca4238a0b923820dcc509a6f75849b";
            Map<String, Object> res = new HashMap<>();
            UserVo LoginUser = new UserVo();
            LoginUser.setUsername(user);
            LoginUser.setPassword(pass);
            LoginUser.setPermissions("100");
            session = new MockHttpSession();
            SessionContext sessionContext = new SessionContext();
            sessionContext.setUser(LoginUser);
            sessionContext.setAuthorized(true);
            session.setAttribute("context",sessionContext);
        }
        log.info("setup===============================End");
    }

    @Test
    void isLogin() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Field sessionField = UserController.class.getDeclaredField("httpSession");
        sessionField.setAccessible(true);
        sessionField.set(userController, session);
        Map<String, Object> param = null;
        param = userController.isLogin(null);
        assertTrue((Boolean) param.get("isLogin"));

        session.setAttribute("context", null);
        sessionField.set(userController, session);
        param = null;
        param = userController.isLogin(null);
        assertFalse((Boolean) param.get("isLogin"));


    }

    @Test
    void login() {
    }

    @Test
    void logout() {
    }

    @Test
    void changePw() {
    }

    @Test
    void changeAuth() {
    }

    @Test
    void create() {
    }

    @Test
    void loadUserList() {
    }

    @Test
    void deleteUser() {
    }
}