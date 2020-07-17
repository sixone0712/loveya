package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.Defines.RSSErrorReason;
import jp.co.canon.cks.eec.fs.rssportal.model.error.RSSError;
import jp.co.canon.cks.eec.fs.rssportal.service.UserService;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rss/api/auths")
public class LoginController {
    private final HttpSession httpSession;
    private final UserService serviceUser;
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    public LoginController(HttpSession httpSession, UserService serviceUser) {
        this.httpSession = httpSession;
        this.serviceUser = serviceUser;
    }

    @GetMapping("/me")
    @ResponseBody
    public ResponseEntity<?> isLogin(HttpServletRequest request)  throws Exception {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        try {
            SessionContext context = (SessionContext) httpSession.getAttribute("context");
            UserVo user = context.getUser();
            resBody.put("userName", user.getUsername());
            resBody.put("userId", user.getId());
            resBody.put("permission", user.getPermissions());
            return ResponseEntity.status(HttpStatus.OK).body(resBody);
        } catch (Exception e) {
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }
    }

    @GetMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(HttpServletRequest request,
                                   @RequestParam(name="username", required = false, defaultValue = "") String username,
                                   @RequestParam(name="password", required = false, defaultValue = "") String password)  throws Exception {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();;

        if(username == null|| username.equals("") || password == null || password.equals("")) {
            error.setReason(RSSErrorReason.INVALID_PARAMETER);
            resBody.put("error", error.getRSSError());
            log.error(String.format("[Get] %s : %s", request.getServletPath(), "Invalid string value: username or password is empty."));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
        }

        int userId = serviceUser.verify(username, password);
        if(userId >= 10000) {
            SessionContext context = new SessionContext();
            UserVo LoginUser = serviceUser.getUser(userId);
            serviceUser.UpdateLastAccessTime(userId);
            context.setUser(LoginUser);
            context.setAuthorized(true);
            httpSession.setAttribute("context", context);
            resBody.put("userName", LoginUser.getUsername());
            resBody.put("userId", LoginUser.getId());
            resBody.put("permission", LoginUser.getPermissions());
            resBody.put("accessToken", "");     // need to add`
            resBody.put("refreshToken", "");    // need to add
        }
        else {
            if(userId == 34) {
                error.setReason(RSSErrorReason.INVALID_PASSWORD);
                resBody.put("error", error.getRSSError());
                log.error(String.format("[Get] %s : %s", request.getServletPath(), "Password is incorrect."));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
            } else {
                error.setReason(RSSErrorReason.NOT_FOUND);
                resBody.put("error", error.getRSSError());
                log.error(String.format("[Get] %s : %s", request.getServletPath(), "User does not exist."));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @GetMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletRequest request)  throws Exception {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();;
        try {
            SessionContext context = (SessionContext) httpSession.getAttribute("context");
            UserVo user = context.getUser();
            resBody.put("userName", user.getUsername());
            resBody.put("userId", user.getId());
            this.httpSession.invalidate();
            return ResponseEntity.status(HttpStatus.OK).body(resBody);
        } catch (Exception e) {
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }
    }
}
