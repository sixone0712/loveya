package jp.co.canon.cks.eec.fs.rssportal.downloadlist;

import jp.co.canon.cks.eec.fs.rssportal.model.plans.RSSPlanFileList;
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

    @Autowired
    public DownloadListController(HttpSession session, DownloadListService service) {
        if(session==null || service==null)
            throw new BeanInitializationException("initialized exception occurs");
        this.session = session;
        this.service = service;
    }

    @GetMapping("/{planId}/filelists")
    @ResponseBody
    public ResponseEntity<?> getList(HttpServletRequest request,
                                                        @PathVariable("planId") String planId) {
        log.info(String.format("[Get] %s", request.getServletPath()));

        if(planId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        int id = Integer.parseInt(planId);
        List<DownloadListVo> files;
        files = service.getList(id);

        List<RSSPlanFileList> convList = new ArrayList<RSSPlanFileList>();
        for(DownloadListVo file : files) {
            RSSPlanFileList newFile = new RSSPlanFileList();
            SimpleDateFormat conTimeFormat  = new SimpleDateFormat("yyyyMMddHHmmss");
            newFile.setPlanId(file.getPlanId());
            newFile.setFileId(file.getId());
            newFile.setCreated(file.getCreated() != null ? conTimeFormat.format(file.getCreated()) : null);
            newFile.setStatus(file.getStatus());
            newFile.setDownloadUrl("/rss/api/plans/storage/" + String.valueOf(file.getId()));
            convList.add(newFile);
        }

        Map<String, Object> resBody = new HashMap<>();
        resBody.put("lists", convList);

        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @DeleteMapping("/{planId}/filelists/{fileId}")
    @ResponseBody
    public ResponseEntity<?> delete(HttpServletRequest request,
                                 @PathVariable("planId") String planId,
                                 @PathVariable("fileId") String fileId) {
        log.info(String.format("[Delete] %s", request.getServletPath()));

        if(planId == null || fileId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        int id = Integer.parseInt(fileId);
        DownloadListVo item = service.get(id);
        if(item==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        service.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}