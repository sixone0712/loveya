package jp.co.canon.cks.eec.fs.rssportal.session;

import jp.co.canon.cks.eec.fs.rssportal.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class SessionInterceptor extends HandlerInterceptorAdapter {

    private static final long SESSION_TIMEOUT = 100 * 1000;

    private static final String[] GUEST_PERMITTED_PAGES = {
            "/login", "/dbtest"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandler");
        HttpSession session = request.getSession();
        String url = request.getServletPath();

        // If the client requests a page which permits guest access, this method does nothing here.
        if(isUserspace(url)==false) {
            return true;
        }

        /*
        SessionContext context = (SessionContext) session.getAttribute("context");
        if(context==null) {
            initSession(session);
        }
        */

        // Check session timeout.
        long current = System.currentTimeMillis();
        if((current-session.getLastAccessedTime())>SESSION_TIMEOUT) {
            log.info("session timeout");
            session.invalidate();
            response.sendRedirect("/test/login");
            return false;
        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    private void initSession(HttpSession session) {
        log.info("init session");

        SessionContext context = new SessionContext();
        context.setDesc("test-session");
        context.setAuthorized(true);  // TODO 인증 완료될 때까지 임시로 true 입력
        session.setAttribute("context", context);
    }

    private boolean isUserspace(@NonNull String url) {
        for(String permitted: GUEST_PERMITTED_PAGES) {
            if(url.startsWith(url)) {
                return false;
            }
        }
        return true;
    }

    private Log log = LogFactory.getLog(getClass());
}
