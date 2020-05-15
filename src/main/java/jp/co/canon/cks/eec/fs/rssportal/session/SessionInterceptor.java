package jp.co.canon.cks.eec.fs.rssportal.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Component
public class SessionInterceptor extends HandlerInterceptorAdapter {

    private static final long SESSION_TIMEOUT = 3600*1000;

    private static final String[] allowedRegex = {
            "/",
            "/error",
            "/rss/login",
            "/user/login",
            "/favicon\\.icon",
            "/dbtest/[\\w./]*",
            "/build/react/[\\w.]*"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        String url = request.getServletPath();
        log.info("preHandler (url="+url+")");

        //response.setHeader("");

        // If the client requests a page which permits guest access, this method does nothing here.
        if(isUserspace(url)==false) {
            return true;
        }

        // Check session has been authorized.
        SessionContext context = (SessionContext)session.getAttribute("context");
        if(context==null) {
            log.warn("invalid access. redirect login page");
            //response.sendRedirect("/test/login"); // TODO
            return false;
        }

        long current = System.currentTimeMillis();
        if((current-session.getLastAccessedTime())>SESSION_TIMEOUT) {
            log.info("session timeout");
            session.invalidate();
            //response.sendRedirect("/test/login"); // TODO
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    private boolean isUserspace(@NonNull String url) {
        for(String pat: allowedRegex) {
            if(url.matches(pat))
                return false;
        }
        return true;
    }


    @SuppressWarnings("unused")
    private void testAllowRegex() {
        final String[] testString = {
                "/",
                "/error",
                "/user/login",
                "/dbtest",
                "/index.html",
                "/index.htm",
                "/index",
                "/build/react/index.bundle.js"
        };

        List<String> report = new ArrayList<>();
        for(String test: testString) {
            boolean matched = false;
            StringBuilder sb = new StringBuilder(test);
            sb.append(" ... ");
            for(String regex: allowedRegex) {
                if(test.matches(regex)) {
                    sb.append("matched with ").append(regex);
                    matched = true;
                    break;
                }
            }
            if(!matched)
                sb.append("unmatced");
            report.add(sb.toString());
        }
        log.info("==== regex test report ====");
        for(String str: report)
            log.info(str);
        log.info("===========================");
    }

    private Log log = LogFactory.getLog(getClass());
}
