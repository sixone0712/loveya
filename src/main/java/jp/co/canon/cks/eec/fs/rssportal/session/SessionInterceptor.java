package jp.co.canon.cks.eec.fs.rssportal.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.canon.cks.eec.fs.rssportal.Defines.RSSErrorReason;
import jp.co.canon.cks.eec.fs.rssportal.model.error.RSSError;
import jp.co.canon.cks.eec.fs.rssportal.service.JwtService;
import jp.co.canon.cks.eec.fs.rssportal.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionInterceptor extends HandlerInterceptorAdapter {
    private final JwtService jwtService;
    private final UserService userService;

    public static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    public SessionInterceptor (JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    private static final String[] allowedRegex = {
            "/",
            "/index.html",
            "/rss/index.bundle.*.js",
            "/rss/main.*.css",
            "/error",
            "/rss/page/login",
            "/rss/api/auths/login",
            "/rss/api/auths/logout",
            //"/rss/api/auths/me",
            "/rss/api/auths/token",
            "/favicon\\.ico",
            "/dbtest/[\\w./]*",
            "/dbtest",
            "/rss",
            "/rss/version",
            "/build/react/[\\w.]*"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getServletPath();
        StringBuilder sb = new StringBuilder("request ");
        sb.append(url).append(" ");
        log.info(sb.toString());

        if(isPageMove(url)) {
            return true;
        }

        if(!isUserspace(url)) {
            response.addHeader("userauth", "true");
            sb.append("[guest][true]");
            log.info(sb.toString());
            return true;
        }

        log.info("[preHandle]Authorization: " + request.getHeader("Authorization"));
        String accessToken = request.getHeader("Authorization");
        log.info("[preHandle]accessToken: " + accessToken);
        if (!jwtService.isUsable(accessToken) || userService.getToken(accessToken.replace(TOKEN_PREFIX, ""))) {
            log.info("[preHandle]accessToken invalid");
            Map<String, Object> resBody = new HashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();
            RSSError error = new RSSError();
            error.setReason(RSSErrorReason.INVALID_ACCESS_TOKEN);
            resBody.put("error", error.getRSSError());
            String resBodyJson = objectMapper.writeValueAsString(resBody);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(resBodyJson);
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

    private boolean isPageMove(@NonNull String url) {
        return url.startsWith("/rss/page") || url.equals("/rss");
    }

    @SuppressWarnings("unused")
    /*private void testAllowRegex() {
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
    }*/

    private Log log = LogFactory.getLog(getClass());
}
