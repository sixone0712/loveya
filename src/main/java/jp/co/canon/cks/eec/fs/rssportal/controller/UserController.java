package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.service.UserPermissionService;
import jp.co.canon.cks.eec.fs.rssportal.service.UserService;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserPermissionVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private final HttpSession httpSession;
    private final UserService serviceUser;
    private final UserPermissionService serviceUserPerm;

    @Autowired
    public UserController(HttpSession httpSession, UserService serviceUser, UserPermissionService serviceUserPerm) {
        log.info("/user/ Controller");
        this.httpSession = httpSession;
        this.serviceUser = serviceUser;
        this.serviceUserPerm = serviceUserPerm;
    }


    @GetMapping("/login")
    @ResponseBody
    public int login(@RequestParam Map<String, Object> param)  throws Exception {
        int res = 0;
        log.info("/user/login");
        String username = param.containsKey("user")?(String)param.get("user"):null;
        String password = param.containsKey("password")?(String)param.get("password"):null;

        if(username==null || username.isEmpty() || password==null || password.isEmpty())
        {
            res = 32;   // LOGIN_FAIL_NO_USERNAME_PASSWORD
        }
        else
        {
            int userId = serviceUser.verify(username, password);
            if(userId >= 10000) {
                SessionContext context = new SessionContext();
                UserVo LoginUser = serviceUser.getUser(userId);
                context.setUser(LoginUser);
                context.setAuthorized(true);
                httpSession.setAttribute("context", context);
                res= 0;
            }
            else {
              res =  userId;
            }
        }
        return res;
    }

    @GetMapping("/logout")
    @ResponseBody
    public int logout()  throws Exception {
        log.info("/user/logout");
        this.httpSession.invalidate();
        return 0;
    }

    @GetMapping("/changePw")
    @ResponseBody
    public int changePw(@RequestParam Map<String, Object> param)  throws Exception {
        int res = 0;
        log.info("/user/changePw");
        String username = param.containsKey("username")?(String)param.get("username"):null;
        String password = param.containsKey("password")?(String)param.get("password"):null;

        if(username==null || password==null || username.isEmpty() || password.isEmpty())
        {
            res = 32;   // LOGIN_FAIL_NO_USERNAME_PASSWORD
        }
        else
        {
            UserVo userObj = serviceUser.getUser(username);
            if(userObj != null && (userObj.getId() >= 10000)) {
                boolean DbResult = false;
                userObj.setPassword(password);
                DbResult = serviceUser.modifyUser(userObj);
                if(!DbResult)
                {
                    res = 32;
                    log.info("DB update fail");
                }
                else
                {
                    log.info("DB update Success");
                }
            }
            else {
                res =  33; // LOGIN_FAIL_NO_REGISTER_USER
            }
        }
        return res;
    }
    @GetMapping("/changeAuth")
    @ResponseBody
    public int changeAuth(@RequestParam Map<String, Object> param)  throws Exception {
        int res = 0;
        log.info("/user/changeAuth");
        String username = param.containsKey("username")?(String)param.get("username"):null;
        String permission = param.containsKey("permission")?(String)param.get("permission"):null;

        if(username==null || permission==null || username.isEmpty() || permission.isEmpty())
        {
            res = 32;   // LOGIN_FAIL_NO_USERNAME_PASSWORD
        }
        else
        {
            UserVo userObj = serviceUser.getUser(username);
            if(userObj != null && (userObj.getId() >= 10000)) {
                boolean DbResult = false;
                userObj.setPermissions(permission);
                DbResult = serviceUser.modifyUser(userObj);
                if(!DbResult)
                {
                    res = 32;
                    log.info("DB update fail");
                }
                else
                {
                    log.info("DB update Success");
                }
            }
            else {
                res =  100; // DB_UPDATE_ERROR_NO_SUCH_USER
            }
        }
        return res;
    }

    private final Log log = LogFactory.getLog(getClass());

}
