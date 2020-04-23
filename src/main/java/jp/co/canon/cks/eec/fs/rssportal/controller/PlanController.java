package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.background.CollectPlanner;
import jp.co.canon.cks.eec.fs.rssportal.service.CollectPlanService;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/plan")
public class PlanController {

    private final HttpSession session;
    private final CollectPlanner collector;
    private final CollectPlanService service;

    @Autowired
    public PlanController(@NonNull HttpSession session, @NonNull CollectPlanner collector, @NonNull CollectPlanService service) {
        this.session = session;
        this.collector = collector;
        this.service = service;
    }

    @RequestMapping("/add")
    @ResponseBody
    public String addPlan(@RequestBody Map<String, Object> param) throws ModelAndViewDefiningException {
        log.info("request \"/plan/add\"");
        if(checkSession()==false) {
            resendToLogin("unauthorized session");
        }
        if(param==null) {
            resendToError("null param");
        }

        try {
            List<String> tools = param.containsKey("tools")?(List<String>) param.get("tools"):null;
            List<String> logTypes = param.containsKey("logTypes")?(List<String>) param.get("logTypes"):null;
            String fromStr = param.containsKey("from")?(String)param.get("from"):null;
            String toStr = param.containsKey("to")?(String)param.get("to"):null;
            String collectType = param.containsKey("collectType")?(String)param.get("collectType"):null;
            String intervalStr = param.containsKey("interval")?(String)param.get("interval"):null;
            String description = param.containsKey("description")?(String)param.get("description"):null;

            if(tools==null || logTypes==null || fromStr==null || toStr==null || collectType==null || intervalStr==null)
                resendToError("not enough param");
            if(collectType.equalsIgnoreCase("cycle")==false &&
                    collectType.equalsIgnoreCase("continuous")==false)
                resendToError("parse collectType error");

            Date fromDate = toDate(fromStr);
            Date toDate = toDate(toStr);
            long interval = Long.valueOf(intervalStr);

            if(true)
                service.addPlan(tools, logTypes, fromDate, toDate, collectType, interval, description);

            return "okay";

        } catch (ParseException e) {
            resendToError("parse date error");
        }
        return "failed";
    }

    private Date toDate(@NonNull String str) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
    }

    private void resendToLogin(String reason) throws ModelAndViewDefiningException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login");
        mav.addObject("reason", reason);
        throw new ModelAndViewDefiningException(mav);
    }

    private void resendToError(String error) throws ModelAndViewDefiningException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error");
        mav.addObject("error", error);
        throw new ModelAndViewDefiningException(mav);
    }

    private boolean checkSession() {
        if(session==null) {
            return false;
        }
        SessionContext context = (SessionContext) session.getAttribute("context");
        if(context==null) {
            log.warn("## TODO ##");
        }
        return true; //context.isAuthorized();
    }

    private final Log log = LogFactory.getLog(getClass());
}
