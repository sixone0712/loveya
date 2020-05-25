package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.service.DownloadHistoryService;
import jp.co.canon.cks.eec.fs.rssportal.service.UserPermissionService;
import jp.co.canon.cks.eec.fs.rssportal.service.UserService;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.DownloadHistoryVo;
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
@RequestMapping("/rss/rest/dwHistory")
public class DwHistoryController {
    private final String HISTORY_RESULT = "result";
    private final String HISTORY_DATA = "data";
    private final HttpSession httpSession;
    private final DownloadHistoryService serviceDwHistory;

    @Autowired
    public DwHistoryController(HttpSession httpSession, DownloadHistoryService serviceDwHistory) {
        log.info("/dwHistory/ Controller");
        this.httpSession = httpSession;
        this.serviceDwHistory = serviceDwHistory;
    }

    @GetMapping("/getHistoryList")
    @ResponseBody
    public Map<String, Object> getHistoryList() throws Exception {

        log.info("/dwHistory/getHistoryList");
        Map<String, Object> returnData = new HashMap<>();
        List<DownloadHistoryVo> list = serviceDwHistory.getHistoryList();
        if (list == null) {
            log.info("List data is null");
            returnData.put(HISTORY_RESULT, -1);
        } else {
            returnData.put(HISTORY_RESULT, 0);
            returnData.put(HISTORY_DATA, list);
        }
        return returnData;
    }

    private final Log log = LogFactory.getLog(getClass());
}
