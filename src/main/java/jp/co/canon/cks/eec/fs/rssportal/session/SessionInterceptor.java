package jp.co.canon.cks.eec.fs.rssportal.session;

import jp.co.canon.cks.eec.fs.rssportal.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

        if(session.isNew()) {
            initSession(session);
        }

        SessionContext context = (SessionContext) session.getAttribute("context");
        if(context==null) {
            log.error("session-interceptor sequence error");
            return false;
        }

        if(!context.isAuthorized()) {
            String url = request.getServletPath();
            for(String permitted: GUEST_PERMITTED_PAGES) {
                if(url.startsWith(permitted)) {
                    return true;
                }
            }
        } else {
            long current = System.currentTimeMillis();
            if((current-session.getLastAccessedTime())>SESSION_TIMEOUT) {
                log.info("session timeout");
                session.invalidate();
                response.sendRedirect("/test/login");
                return false;
            }
            return true;
        }

        response.sendRedirect("/test/login");
        return false;
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

    private Log log = LogFactory.getLog(getClass());
}
