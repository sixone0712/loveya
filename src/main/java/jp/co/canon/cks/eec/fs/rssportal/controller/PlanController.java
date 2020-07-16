package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.background.CollectPlanner;
import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListService;
import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListVo;
import jp.co.canon.cks.eec.fs.rssportal.model.plans.RSSPlansCollectionPlan;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/rss/api/plans")
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

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> addPlan(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        log.info(String.format("[Post] %s", request.getServletPath()));

        if(param==null) {
            log.error("no param");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            int id = addPlanProc(param);
            if(id<0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("planID", id);
            return ResponseEntity.status(HttpStatus.OK).body(resBody);
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<?> listPlan(HttpServletRequest request, @RequestParam Map<String, Object> param) {
        log.info(String.format("[Get] %s", request.getServletPath()));
        if(param==null) {
            log.error("no param");
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }

        List<CollectPlanVo> plans;
        if(param.containsKey("withPriority")) plans = service.getAllPlansBySchedulePriority();
        else plans = service.getAllPlans();

        List<RSSPlansCollectionPlan> convList = new ArrayList<RSSPlansCollectionPlan>();
        for(CollectPlanVo plan : plans) {
            RSSPlansCollectionPlan newPlan = new RSSPlansCollectionPlan();
            SimpleDateFormat conTimeFormat  = new SimpleDateFormat("yyyyMMddHHmmss");
            newPlan.setPlanId(plan.getId());
            newPlan.setPlanType("");      // need to add
            newPlan.setOwnerId(plan.getOwner());
            newPlan.setPlanName(plan.getPlanName());
            newPlan.setFabNames(plan.getFab());
            newPlan.setMachineNames(plan.getTool());
            newPlan.setCategoryCodes(plan.getLogType());
            newPlan.setCategoryNames(plan.getLogTypeStr());
            newPlan.setCommands(null);      // need to add
            newPlan.setType(plan.getCollectTypeStr());
            newPlan.setInterval(Long.toString(plan.getInterval()));
            newPlan.setDescription(plan.getDescription());
            newPlan.setStart(plan.getCollectStart() != null ? conTimeFormat.format(plan.getCollectStart()) : null);
            newPlan.setFrom(plan.getStart() != null ? conTimeFormat.format(plan.getStart()): null);
            newPlan.setTo(plan.getEnd() != null ? conTimeFormat.format(plan.getEnd()): null);
            newPlan.setLastCollection(plan.getLastCollect() != null ? conTimeFormat.format(plan.getLastCollect()) : null);
            newPlan.setStatus(plan.getStatus());
            newPlan.setDetailedStatus(plan.getDetail());
            convList.add(newPlan);
        }

        Map<String, Object> resBody = new HashMap<>();
        resBody.put("lists", convList);

        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @DeleteMapping("/{planId}")
    @ResponseBody
    public ResponseEntity<?> deletePlan(HttpServletRequest request, @PathVariable("planId") String planId) {
        log.info(String.format("[Delete] %s", request.getServletPath()));
        if(planId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        boolean ret = service.deletePlan(Integer.parseInt(planId));
        if(!ret) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/storage/{fileId}")
    public ResponseEntity<InputStreamResource> download(HttpServletRequest request,
                                                        HttpServletResponse response,
                                                        @PathVariable("fileId") String fileId) {
        log.info(String.format("[Get] %s", request.getServletPath()));
        if(fileId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        int id = Integer.parseInt(fileId);

        DownloadListVo item = downloadService.get(id);
        if(item==null) {
            log.error("invalid downloadId "+id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        CollectPlanVo plan = service.getPlan(item.getPlanId());
        if(plan==null || plan.getLastCollect()==null) {
            log.error("invalid download request for invisible plan "+item.getPlanId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        File zip = new File(item.getPath());
        if(zip==null || !zip.isFile() || !zip.exists()) {
            log.error("no download file");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(isr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PutMapping("/{planId}")
    @ResponseBody
    public ResponseEntity<?> modify(HttpServletRequest request,
                                          @PathVariable("planId") String planId,
                                          @RequestBody Map<String, Object> param) {
        log.info(String.format("[Put] %s", request.getServletPath()));
        if(planId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        int id = Integer.parseInt(planId);
        CollectPlanVo plan = service.getPlan(id);
        if(plan==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if(!service.deletePlan(id)) {
            log.error("invalid planId="+id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            int newId = addPlanProc(param);
            if(newId<0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("planId", newId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{planId}/{action}")
    public ResponseEntity<?> changePlanStatus(HttpServletRequest request,
                                           @PathVariable("planId") String planId,
                                           @PathVariable("action") String action) {
        log.info(String.format("[Put] %s", request.getServletPath()));
        if(planId == null || action == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        int id = Integer.parseInt(planId);

        HttpStatus status;
        if(action.equals("stop")) {
            status = service.stopPlan(id) ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        } else if(action.equals("restart")) {
            status = service.restartPlan(id)? HttpStatus.OK : HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.NOT_FOUND;
        }

        return ResponseEntity.status(status).body(null);
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

        String planName = param.containsKey("planName")?(String)param.get("planName"):null;
        String planType = param.containsKey("planType")?(String)param.get("planType"):null;
        List<String> fabNames = param.containsKey("fabNames")?(List<String>) param.get("fabNames"):null;
        List<String> machineNames = param.containsKey("machineNames")?(List<String>) param.get("machineNames"):null;
        List<String> categoryNames = param.containsKey("categoryNames")?(List<String>) param.get("categoryNames"):null;
        List<String> categoryCodes = param.containsKey("categoryCodes")?(List<String>) param.get("categoryCodes"):null;
        List<String> commands = param.containsKey("commands")?(List<String>) param.get("commands"):null;
        String start = param.containsKey("start")?(String)param.get("start"):null;
        String from = param.containsKey("from")?(String)param.get("from"):null;
        String to = param.containsKey("to")?(String)param.get("to"):null;
        String type = param.containsKey("type")?(String)param.get("type"):null;
        String intervalStr = param.containsKey("interval")?(String)param.get("interval"):null;
        String description = param.containsKey("description")?(String)param.get("description"):null;

        if(planType != null) {
            if (planType.equalsIgnoreCase("ftp")) {
                if (categoryNames == null || categoryCodes == null) {
                    return -1;
                }
            } else if (planType.equalsIgnoreCase("vftp_compat") || planType.equals("vftp_sss")) {
                if (commands == null) {
                    return -1;
                }
            } else {
                return -1;
            }
        } else {
            return -1;
        }

        if(planName==null || fabNames==null || machineNames==null || start==null || from==null
            ||to==null || type==null || intervalStr==null || description==null) {
            return -1;
        }

        if(type.equalsIgnoreCase("cycle")==false
            && type.equalsIgnoreCase("continuous")==false) return -1;

        Date collectStartDate = toDate(start);
        Date fromDate = toDate(from);
        Date toDate = toDate(to);
        long interval = Long.valueOf(intervalStr);

        int id = service.addPlan(planName, fabNames, machineNames, categoryCodes, categoryNames,
                collectStartDate, fromDate, toDate, type, interval, description);
        if(id<0)
            return -2;
        return id;
    }

    private Date toDate(@NonNull String str) throws ParseException {
        return new SimpleDateFormat("yyyyMMddHHmmss").parse(str);
    }

    private final Log log = LogFactory.getLog(getClass());
}
