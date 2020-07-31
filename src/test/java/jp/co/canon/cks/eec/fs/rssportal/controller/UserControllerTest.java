package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
//import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;   //compile Error
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
import java.util.List;
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

//    @Test
//    void isLogin() throws Exception {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.setServletPath("/rss/rest/user/isLogin");
//
//        Field sessionField = UserController.class.getDeclaredField("httpSession");
//        sessionField.setAccessible(true);
//        sessionField.set(userController, session);
//        Map<String, Object> param = null;
//        param = userController.isLogin(null);
//        assertTrue((Boolean) param.get("isLogin"));
//
//        session.setAttribute("context", null);
//        sessionField.set(userController, session);
//        param = userController.isLogin(null);
//        assertFalse((Boolean) param.get("isLogin"));
//    }
//
//    @Test
//    void login() throws Exception {
//        Map<String, Object> param = new HashMap<>();
//        Map<String, Object> result = null;
//
//        /*test 1 - in case : user information is null*/
//        param.put("user",null);
//        param.put("password",null);
//        result = userController.login(param);
//        if(result != null)
//        {
//            assertEquals(32,result.get("error"));
//        }
//        /*test 2 - in case : no register user*/
//        param.clear();
//        param.put("user","test1234");
//        param.put("password","c4ca4238a0b923820dcc509a6f75849b");
//        result = userController.login(param);
//        if(result != null)
//        {
//            assertEquals(33,result.get("error"));
//        }
//        /*test 3 - in case : user's password incorrect */
//        param.clear();
//        param.put("user","ymkwon");
//        param.put("password","c4ca");
//        result = userController.login(param);
//        if(result != null)
//        {
//            assertEquals(34,result.get("error"));
//        }
//        /*test 4 - in case : correct user */
//        param.clear();
//        param.put("user","ymkwon");
//        param.put("password","c4ca4238a0b923820dcc509a6f75849b");
//        result = userController.login(param);
//        if(result != null)
//        {
//            assertEquals(0,result.get("error"));
//            assertEquals("ymkwon",result.get("name"));
//            assertEquals("100",result.get("auth"));
//        }
//    }
//
//    @Test
//    void logout() throws Exception{
//        int result = 0;
//        result = userController.logout();
//        assertEquals(0,result);
//    }
//
//
//
//    @Test
//    void changePw() throws Exception {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.setServletPath("/rss/rest/user/changePw");
//
//        Field sessionField = UserController.class.getDeclaredField("httpSession");
//        sessionField.setAccessible(true);
//
//        /*test 1 - in case : session context exist & param is null  */
//        sessionField.set(userController, session);
//        Map<String, Object> param = new HashMap<>();
//        int result = 0;
//        param.put("oldPw",null);
//        param.put("newPw",null);
//
//        result = userController.changePw(param);
//        assertEquals(40,result);
//
//        /*test 2 - in case : current password incorrect  */
//        param.clear();
//        param.put("oldPw","oldPw");
//        param.put("newPw","c4ca4238a0b923820dcc509a6f75849b");
//        result = userController.changePw(param);
//        assertEquals(41,result);
//
//        /*test 3 - in case : current password correct  */
//        param.clear();
//        param.put("oldPw","c4ca4238a0b923820dcc509a6f75849b");
//        param.put("newPw","c4ca4238a0b923820dcc509a6f75849b");
//        result = userController.changePw(param);
//        assertEquals(0,result);
//    }
//
//    @Test
//    void changeAuth() throws Exception {
//
//        Map<String, Object> param = new HashMap<>();
//        Map<String, Object> res = null;
//        int result = 0;
//        String changeAuth = "10";
//
//
//        /*test 1 - in case : id is null */
//        param.put("id",null);
//        res = userController.changeAuth(param);
//        assertEquals(32,res.get("result"));
//
//        /*test 2 - in case : no such user */
//        param.put("id","100000000");
//        param.put("permission",changeAuth);
//        res = userController.changeAuth(param);
//        assertEquals(100,res.get("result"));
//
//        /*test 3 - in case : normal user change permission*/
//        //Before setting
//        param.put("name", "test");
//        param.put("pwd", "test");
//        param.put("auth", "100");
//        Map<String, Object> response =null;
//        response=userController.getUser(param);
//        if(response.get("result").toString().equals("0"))
//        {
//            UserVo userVo = (UserVo) response.get("data");
//            param.clear();
//            param.put("id",String.valueOf(userVo.getId()));
//            param.put("permission",changeAuth);
//            response.clear();
//            response = userController.changeAuth(param);
//            assertEquals(0, response.get("result"));
//        }
//        else
//        {
//            result = userController.create(param);
//            if(result == 0)
//            {
//                response.clear();
//                response=userController.getUser(param);
//                if(response.get("result").toString().equals("0"))
//                {
//                    UserVo userVo = (UserVo) response.get("data");
//                    param.clear();
//                    param.put("id",String.valueOf(userVo.getId()));
//                    param.put("permission",changeAuth);
//                    response.clear();
//                    response = userController.changeAuth(param);
//                    assertEquals(0, response.get("result"));
//                    assertEquals(changeAuth, response.get("Auth"));
//                    param.clear();
//                    param.put("id",String.valueOf(userVo.getId()));
//                    userController.deleteUser(param);
//                }
//            }
//
//        }
//    }
//
//    @Test
//    void create() throws Exception{
//        Map<String, Object> param = new HashMap<>();
//        int result = 0;
//
//        /*test 1 - in case : id is null */
//        param.put("name", null);
//        param.put("pwd", null);
//        param.put("auth", null);
//        result = userController.create(param);
//        assertEquals(32,result);
//
//        /*test 2 - in case : error - USER_SET_FAIL_SAME_NAME */
//        param.clear();
//        param.put("name", "ymkwon");
//        param.put("pwd", "test");
//        param.put("auth", "100");
//        result = userController.create(param);
//        assertEquals(300,result);
//
//        param.clear();
//        param.put("name", "test");
//        param.put("pwd", "test");
//        param.put("auth", "10");
//        result = userController.create(param);
//        assertEquals(0,result);
//        Map<String, Object> response = null;
//        response=userController.getUser(param);
//        if(response.get("result").toString().equals("0"))
//        {
//            assertEquals(0,response.get("result"));
//            Map<String, Object> parameter = new HashMap<>();
//            UserVo userVo = (UserVo) response.get("data");
//            parameter.put("id",String.valueOf(userVo.getId()));
//            userController.deleteUser(parameter);
//        }else{
//            assertEquals(33,response.get("result"));
//        }
//
//    }
//
//    @Test
//    void loadUserList() throws Exception{
//        Map<String, Object> result = null;
//
//        result = userController.loadUserList();
//        if(result.get("result") == "-1") {
//            /*test 1 - in case : user data is null*/
//            assertNull(result.get("data"));
//        }
//        else {
//            /*test 2 - in case : user data is not null*/
//            assertEquals(0,result.get("result"));
//            assertNotNull(result.get("data"));
//            List<UserVo> list = null;
//            Map<String, Object> param = new HashMap<>();
//            list = (List<UserVo>) result.get("data");
//            for (UserVo user : list) {
//                param.clear();
//                param.put("id", String.valueOf(user.getId()));
//                userController.deleteUser(param);
//            }
//            result = userController.loadUserList();
//            assertEquals(-1,result.get("result"));
//
//            for (UserVo user : list) {
//                param.clear();
//                param.put("name", user.getUsername());
//                param.put("pwd", user.getPassword());
//                param.put("auth", user.getPermissions());
//                userController.create(param);
//            }
//        }
//    }
//
//    @Test
//    void deleteUser() throws Exception{
//        Map<String, Object> param = new HashMap<>();
//        int result = 0;
//
//        /*test 1 - in case : id is null */
//        param.put("id","");
//        result = userController.deleteUser(param);
//        assertEquals(33,result);
//
//        /*test 2 - in case : id is incorrect */
//        param.clear();
//        param.put("id","100");
//        result= userController.deleteUser(param);
//        assertEquals(33,result);
//    }
}