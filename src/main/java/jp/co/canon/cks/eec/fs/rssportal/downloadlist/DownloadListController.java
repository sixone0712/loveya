package jp.co.canon.cks.eec.fs.rssportal.downloadlist;

import jp.co.canon.cks.eec.fs.rssportal.Defines.RSSErrorReason;
import jp.co.canon.cks.eec.fs.rssportal.model.error.RSSError;
import jp.co.canon.cks.eec.fs.rssportal.model.plans.RSSPlanFileList;
import jp.co.canon.cks.eec.fs.rssportal.service.CollectPlanService;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rss/api/plans")
public class DownloadListController {

    private final Log log = LogFactory.getLog(getClass());

    private final HttpSession session;
    private final DownloadListService service;
    private final CollectPlanService planService;
    public final String NEW_FILE = "new";
    public final String FINISHED_FILE = "finished";

    @Autowired
    public DownloadListController(HttpSession session, DownloadListService service, CollectPlanService planService) {
        if(session==null || service==null)
            throw new BeanInitializationException("initialized exception occurs");
        this.session = session;
        this.service = service;
        this.planService = planService;
    }

    @GetMapping("/{planId}/filelists")
    @ResponseBody
    public ResponseEntity<?> getList(HttpServletRequest request,
                                     @PathVariable("planId") String planId,
                                     @RequestParam(name="all", required = false, defaultValue = "false") String all) {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        if(planId == null) {
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }

        int id = Integer.parseInt(planId);
        List<DownloadListVo> files;
        files = service.getList(id);

        boolean isdispAll = all.equals("true");
        List<RSSPlanFileList> convList = new ArrayList<RSSPlanFileList>();
        for(DownloadListVo file : files) {
            if(!isdispAll && file.getStatus().equals(FINISHED_FILE)) continue;
            RSSPlanFileList newFile = new RSSPlanFileList();
            SimpleDateFormat conTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            CollectPlanVo plan = planService.getPlan(file.getPlanId());

            newFile.setPlanId(file.getPlanId());
            newFile.setPlanName(plan.getPlanName());
            newFile.setFileId(file.getId());
            newFile.setCreated(file.getCreated() != null ? conTimeFormat.format(file.getCreated()) : null);
            newFile.setStatus(file.getStatus());
            newFile.setDownloadUrl("/rss/api/plans/storage/" + String.valueOf(file.getId()));
            convList.add(newFile);
        }

        resBody.put("lists", convList);
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @GetMapping("/{planId}/filelists/{fileId}")
    @ResponseBody
    public ResponseEntity<?> getfile(HttpServletRequest request,
                                    @PathVariable("planId") String planId,
                                    @PathVariable("fileId") String fileId) {
        log.info(String.format("[Delete] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        if(planId == null || fileId == null) {
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }

        int id = Integer.parseInt(fileId);
        DownloadListVo file = service.get(id);
        if(file == null) {
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }

        RSSPlanFileList newFile = new RSSPlanFileList();
        SimpleDateFormat conTimeFormat  = new SimpleDateFormat("yyyyMMddHHmmss");
        CollectPlanVo plan = planService.getPlan(file.getPlanId());

        newFile.setPlanId(file.getPlanId());
        newFile.setPlanName(plan.getPlanName());
        newFile.setFileId(file.getId());
        newFile.setCreated(file.getCreated() != null ? conTimeFormat.format(file.getCreated()) : null);
        newFile.setStatus(file.getStatus());
        newFile.setDownloadUrl("/rss/api/plans/storage/" + String.valueOf(file.getId()));
        return ResponseEntity.status(HttpStatus.OK).body(newFile);
    }

    @DeleteMapping("/{planId}/filelists/{fileId}")
    @ResponseBody
    public ResponseEntity<?> delete(HttpServletRequest request,
                                 @PathVariable("planId") String planId,
                                 @PathVariable("fileId") String fileId) {
        log.info(String.format("[Delete] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        if(planId == null || fileId == null) {
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }

        int id = Integer.parseInt(fileId);
        DownloadListVo item = service.get(id);
        if(item == null) {
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }
        service.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}