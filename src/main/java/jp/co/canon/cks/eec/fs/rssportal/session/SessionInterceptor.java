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
            "/favicon\\.ico",
            "/dbtest/[\\w./]*",
            "/dbtest",
            "/rss",
            "/user/isLogin",
            "/build/react/[\\w.]*"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        String url = request.getServletPath();
        StringBuilder sb = new StringBuilder("request ");
        sb.append(url).append(" ");

        // If the client requests a page which permits guest access, this method does nothing here.
        if(isUserspace(url)==false) {
            response.addHeader("userauth", "true");
            sb.append("[guest][true]");
            log.info(sb.toString());
            return true;
        }

        // Check session has been authorized.
        SessionContext context = (SessionContext)session.getAttribute("context");
        if(context==null) {
            response.addHeader("userauth", "false");
            if(isPageMove(url)) {
                response.sendRedirect("/rss");
            }
            sb.append("[invalid-session][false]");
            log.info(sb.toString());
            return false;
        }

        long current = System.currentTimeMillis();
        if((current-session.getLastAccessedTime())>SESSION_TIMEOUT) {
            if(isPageMove(url)) {
                response.sendRedirect("/rss");
            }
            session.invalidate();
            sb.append("[timeout][false]");
            log.info(sb.toString());
            return false;
        }

        response.addHeader("userauth", "true");
        log.info(sb.toString());
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

    private boolean isPageMove(@NonNull String url) {
        return url.startsWith("/rss");
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
