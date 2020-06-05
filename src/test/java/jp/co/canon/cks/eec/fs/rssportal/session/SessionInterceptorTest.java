package jp.co.canon.cks.eec.fs.rssportal.session;

import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SessionInterceptorTest {


    @Autowired
    private MockMvc mockMvc;
    private MockHttpSession session;
    private SessionInterceptor sessionInterceptor;

    @Test
    void test() throws Exception {
        sessionInterceptor = new SessionInterceptor();
        MockHttpServletRequest request = null;
        MockHttpServletResponse response = null;
        Object obj = new Object();

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.setServletPath("/");
        sessionInterceptor.preHandle(request,response,obj);
        assertEquals("true",response.getHeader("userauth"));

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.setServletPath("/rss/rest");
        sessionInterceptor.preHandle(request,response,obj);
        assertEquals("false",response.getHeader("userauth"));


        if(session==null) {
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

            request = new MockHttpServletRequest();
            response = new MockHttpServletResponse();
            request.setServletPath("/rss/rest");
            request.setSession(session);
            sessionInterceptor.preHandle(request,response,obj);

        }
        ModelAndView modelAndView = new ModelAndView();
        sessionInterceptor.postHandle(request,response,obj, modelAndView);

    }

}