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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rss/rest/user")
public class UserController {
    private final String USER_RESULT = "result";
    private final String USER_DATA = "data";
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

    @GetMapping("/isLogin")
    @ResponseBody
    public Map<String, Object> isLogin(@RequestParam Map<String, Object> param)  throws Exception {
        log.info("/user/isLogin");
        Map<String, Object> res = new HashMap<>();

        if(httpSession.getAttribute("context") != null) {
            SessionContext context = (SessionContext)httpSession.getAttribute("context");
            res.put("isLogin", true);
            res.put("username", context.getUser().getUsername());
            res.put("permissions", context.getUser().getPermissions());
            log.info("true");
        } else {
            res.put("isLogin", false);
            res.put("username", "");
            res.put("permissions", "");
            log.info("false");
        }

        return res;
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
                serviceUser.UpdateLastAccessTime(userId);
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
        String id = param.containsKey("id")?(String)param.get("id"):null;
        String permission = param.containsKey("permission")?(String)param.get("permission"):null;

        log.info("id: "+id);

        if(id==null || permission==null || id.isEmpty() || permission.isEmpty())
        {
            res = 32;   // LOGIN_FAIL_NO_USERNAME_PASSWORD
        }
        else
        {
            UserVo userObj = serviceUser.getUser(Integer.parseInt(id));
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
    @GetMapping("/create")
    @ResponseBody
    public int create(@RequestParam Map<String, Object> param)  throws Exception {
        int res = 0;
        log.info("/user/create");
        String name = param.containsKey("name")?(String)param.get("name"):null;
        String pwd = param.containsKey("pwd")?(String)param.get("pwd"):null;
        String auth = param.containsKey("auth")?(String)param.get("auth"):null;

        if(name==null || pwd==null || auth==null
                || name.isEmpty() || pwd.isEmpty() || auth.isEmpty())
        {
            res = 32;   // LOGIN_FAIL_NO_USERNAME_PASSWORD
        }
        else
        {
            UserVo userObj = serviceUser.getUser(name);
            if(userObj != null) {
               res= 300;//USER_SET_FAIL_SAME_NAME
            }
            else {
                boolean DbResult = false;
                userObj = new UserVo();
                userObj.setUsername(name);
                userObj.setPassword(pwd);
                userObj.setPermissions(auth);
                DbResult = serviceUser.addUser(userObj);
                if(!DbResult)
                {
                    res = 350;//USER_SET_FAIL_NO_REASON
                    log.info("DB create fail");
                }
                else
                {
                    log.info("DB create Success");
                }
            }
        }
        return res;
    }

    @GetMapping("/loadUserList")
    @ResponseBody
    public Map<String, Object> loadUserList()  throws Exception {

        log.info("/user/loadUserList");
        Map<String, Object> returnData = new HashMap<>();
        List<UserVo> list = serviceUser.getUserList();
        if(list == null)
        {
            log.info("List data is null");
        }
        else {
            returnData.put(USER_RESULT,  0);
            returnData.put(USER_DATA, list);
        }
        return returnData;
    }
    private final Log log = LogFactory.getLog(getClass());

    @GetMapping("/delete")
    @ResponseBody
    public int deleteUser(@RequestParam Map<String, Object> param)  throws Exception {
        int res = 0;
        log.info("/user/deleteUser");
        String id = param.containsKey("id")?(String)param.get("id"):null;
        log.info("id: "+id);

        if(id==null || id.isEmpty())
        {
            res = 33;  //LOGIN_FAIL_NO_REGISTER_USER
        }
        else
        {
            UserVo userObj = serviceUser.getUser(Integer.parseInt(id));
            if(userObj == null) {
                res= 33;//LOGIN_FAIL_NO_REGISTER_USER
            }
            else {
                boolean DbResult = false;
                DbResult = serviceUser.deleteUser(userObj);
                if(!DbResult)
                {
                    res = 350;//USER_SET_FAIL_NO_REASON
                    log.info("DB delete fail");
                }
                else
                {
                    log.info("DB delelte Success");
                }
            }
        }
        return res;
    }
}
