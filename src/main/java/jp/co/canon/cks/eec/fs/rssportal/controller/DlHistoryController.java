package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.service.DownloadHistoryService;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.DownloadHistoryVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rss/rest/dlHistory")
public class DlHistoryController {
    private final String HISTORY_RESULT = "result";
    private final String HISTORY_DATA = "data";
    private final HttpSession httpSession;
    private final DownloadHistoryService serviceDlHistory;

    @Autowired
    public DlHistoryController(HttpSession httpSession, DownloadHistoryService serviceDlHistory) {
        log.info("/dlHistory/ Controller");
        this.httpSession = httpSession;
        this.serviceDlHistory = serviceDlHistory;
    }

    @GetMapping("/getHistoryList")
    @ResponseBody
    public Map<String, Object> getHistoryList() throws Exception {

        log.info("/dwHistory/getHistoryList");
        Map<String, Object> returnData = new HashMap<>();
        List<DownloadHistoryVo> list = serviceDlHistory.getHistoryList();
        if (list == null || list.size() == 0) {
            log.info("List data is null");
            returnData.put(HISTORY_RESULT, -1);
        } else {
            returnData.put(HISTORY_RESULT, 0);
            returnData.put(HISTORY_DATA, list);
        }
        return returnData;
    }

    @PostMapping("/addDlHistory")
    @ResponseBody
    public boolean addDlHistory(@RequestBody Map<String, Object> param)  throws Exception {

        log.info("/dlHistory/addDlHistory");
        DownloadHistoryVo dlVo = new DownloadHistoryVo();

        if(param==null || param.size()==0 ) return false;
        if(httpSession.getAttribute("context") != null) {
            SessionContext context = (SessionContext)httpSession.getAttribute("context");
            String type = param.containsKey("type")?param.get("type").toString():null;
            String filename = param.containsKey("filename")?(String)param.get("filename"):null;
            String status = param.containsKey("status")?(String)param.get("status"):null;
            dlVo.setDl_user(context.getUser().getUsername());
            dlVo.setDl_type(type);
            dlVo.setDl_filename(filename);
            dlVo.setDl_status(status);
            log.info("getDl_user : " + dlVo.getDl_user());
            log.info("getDl_type : " + dlVo.getDl_type());
            log.info("getDl_filename : " + dlVo.getDl_filename());
            log.info("getDl_status : " + dlVo.getDl_status());
        } else {
            // List<DownloadHistoryVo> list = serviceDwHistory.getHistoryList();
            log.info("context is empty ");
            return false;
        }
        return serviceDlHistory.addDlHistory(dlVo);
    }

    private final Log log = LogFactory.getLog(getClass());
}
