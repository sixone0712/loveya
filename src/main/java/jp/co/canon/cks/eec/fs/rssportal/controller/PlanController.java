package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.background.CollectPlanner;
import jp.co.canon.cks.eec.fs.rssportal.service.CollectPlanService;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public ResponseEntity<Integer> addPlan(@RequestBody Map<String, Object> param) {
        log.info("request \"/plan/add\"");
        if(checkSession()==false) {
            log.error("unauthorized session");
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        if(param==null) {
            log.error("no param");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        try {
            int id = addPlanProc(param);
            if(id<0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity(id, HttpStatus.OK);
        } catch (ParseException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping("/list")
    @ResponseBody
    public ResponseEntity<List<CollectPlanVo>> listPlan(@RequestParam Map<String, Object> param) {
        log.info("request \"/plan/list\"");
        if(checkSession()==false) {
            log.error("unauthorized session");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if(param==null) {
            log.error("no param");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String withExpired = (String) (param.containsKey("withExpired")?param.get("withExpired"):"");
        List<CollectPlanVo> list;
        if(withExpired.equals(""))
            list = service.getAllPlansBySchedulePriority();
        else
            list = service.getAllPlans();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ResponseEntity deletePlan(@RequestParam(name="id") int id) {
        log.info("request \"plan/delete\" [id="+id+"]");
        if(checkSession()==false)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        boolean ret = service.deletePlan(id);
        if(ret)
            return new ResponseEntity(HttpStatus.OK);
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping("/download")
    public ResponseEntity<InputStreamResource> download(@RequestParam(name="id") int id, HttpServletResponse response) {
        log.info("request \"plan/download\" [id="+id+"]");
        if(checkSession()==false)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        File zip = collector.getZipPath(id);
        if(zip==null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        String downloadFileName = zip.getName();
        try {
            InputStream is = new FileInputStream(zip);
            InputStreamResource isr = new InputStreamResource(is);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(Files.size(zip.toPath()));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            response.setHeader("Content-Disposition", "attachment; filename="+downloadFileName);
            return new ResponseEntity(isr, headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping("/modify")
    @ResponseBody
    public ResponseEntity<Integer> modify(@RequestParam(name="id") int id, @RequestBody Map<String, Object> param) {
        log.info("request \"plan/modify\" [id="+id+"]");
        CollectPlanVo plan = service.getPlan(id);
        if(plan==null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        boolean ret = service.deletePlan(id);
        if(!ret) {
            log.error("invalid planId="+id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            int newId = addPlanProc(param);
            if(id<0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity(newId, HttpStatus.OK);
        } catch (ParseException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    private int addPlanProc(@NonNull Map<String, Object> param) throws ParseException {

        String planId = param.containsKey("planId")?(String)param.get("planId"):null;
        List<String> tools = param.containsKey("tools")?(List<String>) param.get("tools"):null;
        List<String> logTypes = param.containsKey("logTypes")?(List<String>) param.get("logTypes"):null;
        String collectStartStr = param.containsKey("collectStart")?(String)param.get("collectStart"):null;
        String fromStr = param.containsKey("from")?(String)param.get("from"):null;
        String toStr = param.containsKey("to")?(String)param.get("to"):null;
        String collectType = param.containsKey("collectType")?(String)param.get("collectType"):null;
        String intervalStr = param.containsKey("interval")?(String)param.get("interval"):null;
        String description = param.containsKey("description")?(String)param.get("description"):null;

        if(planId==null || tools==null || logTypes==null || fromStr==null || toStr==null || collectStartStr==null ||
                collectType==null || intervalStr==null)
            return -1;
        if(collectType.equalsIgnoreCase("cycle")==false &&
                collectType.equalsIgnoreCase("continuous")==false)
            return -1;

        Date collectStartDate = toDate(collectStartStr);
        Date fromDate = toDate(fromStr);
        Date toDate = toDate(toStr);
        long interval = Long.valueOf(intervalStr);

        int id = service.addPlan(planId, tools, logTypes, collectStartDate, fromDate, toDate, collectType, interval, description);
        if(id<0)
            return -2;
        return id;
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
