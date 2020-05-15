package jp.co.canon.cks.eec.fs.rssportal.test.controller;

import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListService;
import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListVo;
import jp.co.canon.cks.eec.fs.rssportal.service.CollectPlanService;
import jp.co.canon.cks.eec.fs.rssportal.service.UserPermissionService;
import jp.co.canon.cks.eec.fs.rssportal.service.UserService;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserPermissionVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.*;

@Controller
@RequestMapping("/dbtest")
public class DatabaseTestController {

    private final HttpSession httpSession;
    private final UserService serviceUser;
    private final UserPermissionService serviceUserPerm;
    private final CollectPlanService serviceCollectionPlan;
    private final DownloadListService serviceDownloadList;

    @Autowired
    public DatabaseTestController(HttpSession httpSession,
                                  UserService serviceUser,
                                  UserPermissionService serviceUserPerm,
                                  CollectPlanService serviceCollectionPlan,
                                  DownloadListService serviceDownloadList) {
        this.httpSession = httpSession;
        this.serviceUser = serviceUser;
        this.serviceUserPerm = serviceUserPerm;
        this.serviceCollectionPlan = serviceCollectionPlan;
        this.serviceDownloadList = serviceDownloadList;
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

    @RequestMapping("/plan/add")
    @ResponseBody
    public String addPlan() {
        log.info("/plan/add");

        final long millis_per_minute = 60000;
        final long millis_per_hour = millis_per_minute * 60;
        final long millis_per_day = millis_per_hour * 24;

        long cur = System.currentTimeMillis();

        List<String> tools1 = Arrays.asList("EQVM88", "EQVM87");
        List<String> logTypes1 = Arrays.asList("001", "002", "003");
        Date start1 = new Date(cur+10000);
        Date end1 = new Date(start1.getTime()+millis_per_hour);
        long interval1 = millis_per_minute;

        serviceCollectionPlan.addPlan("planid", tools1, logTypes1, start1, start1, end1, "cycle",
                interval1, "1min-cycle");

        List<String> tools2 = Arrays.asList("EQVM88");
        List<String> logTypes2 = Arrays.asList("004", "005");
        Date start2 = new Date(cur+10000);
        Date end2 = new Date(start1.getTime()+millis_per_hour);
        long interval2 = millis_per_minute*3;

        serviceCollectionPlan.addPlan("planid", tools2, logTypes2, start2, start2, end2, "cycle",
                interval2, "30sec-cycle");

        return "okay";
    }

    @RequestMapping("/plan/list")
    @ResponseBody
    public String getPlans() {
        log.info("/plan/list");
        List<CollectPlanVo> list = serviceCollectionPlan.getAllPlans();
        return list.toString();
    }

    @RequestMapping("/downloadlist/get")
    @ResponseBody
    public String getDownloadList() {
        log.info("/downloadlist/get");
        List<DownloadListVo> list = serviceDownloadList.getList();
        return "ok";
    }

    @RequestMapping("/downloadlist/add")
    @ResponseBody
    public String addDownloadList(HttpServletRequest request) {
        String path = request.getServletPath();
        log.info(path);
        DownloadListVo item = new DownloadListVo();
        item.setPlanId(123);
        item.setCreated(new Timestamp(System.currentTimeMillis()));
        item.setPath("pathpathpath");
        item.setStatus("registered");
        item.setTitle("title1111");
        serviceDownloadList.insert(item);
        return path+" ok";
    }

    private final Log log = LogFactory.getLog(getClass());
}
