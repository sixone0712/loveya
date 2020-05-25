package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.background.CollectPlanner;
import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListService;
import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListVo;
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

import javax.servlet.http.HttpServletRequest;
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
    private final DownloadListService downloadService;

    @Autowired
    public PlanController(@NonNull HttpSession session, @NonNull CollectPlanner collector,
                          @NonNull CollectPlanService service, @NonNull DownloadListService downloadListService) {
        this.session = session;
        this.collector = collector;
        this.service = service;
        this.downloadService = downloadListService;
    }

    @RequestMapping("/add")
    @ResponseBody
    public ResponseEntity<Integer> addPlan(HttpServletRequest request,
                                           @RequestBody Map<String, Object> param) {
        log.info(String.format("request \"%s\"", request.getServletPath()));
        param.forEach((key,value)->log.info("key="+key+" value="+value));
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
    public ResponseEntity<List<CollectPlanVo>> listPlan(HttpServletRequest request,
                                                        @RequestParam Map<String, Object> param) {
        log.info(String.format("request \"%s\"", request.getServletPath()));
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
    public ResponseEntity deletePlan(HttpServletRequest request, @RequestParam(name="id") int id) {
        log.info(String.format("request \"%s?id=\"", request.getServletPath(), id));
        if(checkSession()==false)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        boolean ret = service.deletePlan(id);
        if(ret)
            return new ResponseEntity(HttpStatus.OK);
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping("/download")
    public ResponseEntity<InputStreamResource> download(HttpServletRequest request,
                                                        HttpServletResponse response,
                                                        @RequestParam(name="id") int id) {
        log.info(String.format("request \"%s?id=\"", request.getServletPath(), id));
        if(checkSession()==false)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        DownloadListVo item = downloadService.get(id);
        if(item==null) {
            log.error("invalid downloadId "+id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CollectPlanVo plan = service.getPlan(item.getPlanId());
        if(plan==null || plan.getLastCollect()==null) {
            log.error("invalid download request for invisible plan "+item.getPlanId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        File zip = new File(item.getPath());
        if(zip==null || !zip.isFile() || !zip.exists()) {
            log.error("no download file");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        downloadService.updateDownloadStatus(id);

        String downloadFileName = createDownloadFilename(plan, item);
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
    public ResponseEntity<Integer> modify(HttpServletRequest request,
                                          @RequestParam(name="id") int id, @RequestBody Map<String, Object> param) {
        log.info(String.format("request \"%s?id=\"", request.getServletPath(), id));
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

    @RequestMapping("/stop")
    public ResponseEntity stopPlan(HttpServletRequest request,
                                   @RequestParam(name="id") int id) {
        log.info("request \""+request.getServletPath()+"\" [id="+id+"]");
        HttpStatus status = service.stopPlan(id)?HttpStatus.OK:HttpStatus.NOT_FOUND;
        return new ResponseEntity(status);
    }

    @RequestMapping("/restart")
    public ResponseEntity restartPlan(HttpServletRequest request,
                                   @RequestParam(name="id") int id) {
        log.info("request \""+request.getServletPath()+"\" [id="+id+"]");
        HttpStatus status = service.restartPlan(id)?HttpStatus.OK:HttpStatus.NOT_FOUND;
        return new ResponseEntity(status);
    }

    private String createDownloadFilename(CollectPlanVo plan, DownloadListVo item) {
        // format: username_fabname{_fabname2}_YYYYMMDD_hhmmss.zip

        if(session==null) {
            log.error("null session");
            return null;
        }

        SessionContext context = (SessionContext)session.getAttribute("context");
        if(context==null || context.isAuthorized()==false) {
            log.error("unauthorized download request");
            return null;
        }
        String username = context.getUser().getUsername();
        if(username==null) {
            log.error("no username");
            return null;
        }

        if(plan.getFab()==null) {
            log.error("no fab info");
            return null;
        }
        String[] fabs = plan.getFab().split(",");
        if(fabs.length==0) {
            log.error("no fab info");
            return null;
        }
        String fab = fabs[0];
        if(fabs.length>1) {
            nextFab:
            for(int i=1; i<fabs.length; ++i) {
                for(int j=0; j<i; ++j) {
                    if(fabs[i].equals(fabs[j]))
                        continue nextFab;
                }
                fab += "_" + fabs[i];
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String cur = dateFormat.format(new Date(item.getCreated().getTime()));

        String fileName = String.format("%s_%s_%s.zip", username, fab, cur);
        log.info("filename = "+fileName);
        return fileName;
    }

    private int addPlanProc(@NonNull Map<String, Object> param) throws ParseException {

        String planName = param.containsKey("planId")?(String)param.get("planId"):null;
        List<String> tools = param.containsKey("tools")?(List<String>) param.get("tools"):null;
        List<String> fabs = param.containsKey("structId")?(List<String>) param.get("structId"):null;
        List<String> logTypes = param.containsKey("logTypes")?(List<String>) param.get("logTypes"):null;
        List<String> logTypeStr = param.containsKey("logNames")?(List<String>) param.get("logNames"):null;
        String collectStartStr = param.containsKey("collectStart")?(String)param.get("collectStart"):null;
        String fromStr = param.containsKey("from")?(String)param.get("from"):null;
        String toStr = param.containsKey("to")?(String)param.get("to"):null;
        String collectType = param.containsKey("collectType")?(String)param.get("collectType"):null;
        String intervalStr = param.containsKey("interval")?(String)param.get("interval"):null;
        String description = param.containsKey("description")?(String)param.get("description"):null;

        if(planName==null || fabs==null || tools==null || logTypes==null || logTypeStr==null || fromStr==null ||
                toStr==null || collectStartStr==null || collectType==null || intervalStr==null)
            return -1;
        if(collectType.equalsIgnoreCase("cycle")==false &&
                collectType.equalsIgnoreCase("continuous")==false)
            return -1;

        Date collectStartDate = toDate(collectStartStr);
        Date fromDate = toDate(fromStr);
        Date toDate = toDate(toStr);
        long interval = Long.valueOf(intervalStr);

        int id = service.addPlan(planName, fabs, tools, logTypes, logTypeStr, collectStartDate, fromDate, toDate,
                collectType, interval, description);
        if(id<0)
            return -2;
        return id;
    }

    private Date toDate(@NonNull String str) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
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
