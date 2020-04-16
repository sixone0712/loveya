package jp.co.canon.cks.eec.fs.rssportal.test.controller;

import jp.co.canon.cks.eec.fs.rssportal.service.UserPermissionService;
import jp.co.canon.cks.eec.fs.rssportal.service.UserService;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserPermissionVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dbtest")
public class DatabaseTestController {

    private final HttpSession httpSession;
    private final UserService serviceUser;
    private final UserPermissionService serviceUserPerm;

    @Autowired
    public DatabaseTestController(HttpSession httpSession, UserService serviceUser, UserPermissionService serviceUserPerm) {
        this.httpSession = httpSession;
        this.serviceUser = serviceUser;
        this.serviceUserPerm = serviceUserPerm;
    }

    @RequestMapping("/main")
    public String dbsample() {
        log.info("dbtest page");
        if(serviceUser==null || serviceUserPerm==null) {
            log.error("service beans injection failed");
            return null;
        }

        return "test/dbtest";
    }

    @RequestMapping("/userperm/add")
    @ResponseBody
    public String addUserPerm(@RequestParam Map<String, Object> param) {
        log.info("/userperm/add");
        if(param.containsKey("permname")==false) {
            return "/userperm/add ... failed";
        }

        String permname = (String)param.get("permname");
        serviceUserPerm.add(permname);
        return "/userperm/add ... success";
    }

    @RequestMapping("/userperm/get")
    @ResponseBody
    public String getUserPerms() {
        log.info("/userperm/get");
        List<UserPermissionVo> list = serviceUserPerm.findAll();

        StringBuilder sb = new StringBuilder("");
        for(UserPermissionVo vo: list) {
            sb.append(vo.toString()+"\r\n");
        }
        return sb.toString();
    }

    @RequestMapping("/user/add")
    @ResponseBody
    public String addUser(@RequestParam Map<String, Object> param) {
        log.info("/user/add");
        String username = param.containsKey("username")?(String)param.get("username"):null;
        String password = param.containsKey("password")?(String)param.get("password"):null;
        if(username==null || password==null || username.isEmpty() || password.isEmpty()) {
            return "failed to add user";
        }
        UserVo user = new UserVo();
        user.setUsername(username);
        user.setPassword(password);
        serviceUser.addUser(user);

        UserVo newbie = serviceUser.getUser(username);
        return newbie.toString();
    }

    @RequestMapping("/user/list")
    @ResponseBody
    public String getUser() {
        log.info("/user/list");
        List<UserVo> list = serviceUser.getUserList();
        return list.toString();
    }

    private final Log log = LogFactory.getLog(getClass());
}
