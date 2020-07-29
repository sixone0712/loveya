package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.Defines.RSSErrorReason;
import jp.co.canon.cks.eec.fs.rssportal.model.error.RSSError;
import jp.co.canon.cks.eec.fs.rssportal.model.auth.AccessToken;
import jp.co.canon.cks.eec.fs.rssportal.model.histories.RSSHistoryList;
import jp.co.canon.cks.eec.fs.rssportal.service.DownloadHistoryService;
import jp.co.canon.cks.eec.fs.rssportal.service.JwtService;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.DownloadHistoryVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
@RequestMapping("/rss/api/histories")
public class DlHistoryController {
    private final String HISTORY_RESULT = "result";
    private final String HISTORY_DATA = "data";
    private final HttpSession httpSession;
    private final DownloadHistoryService serviceDlHistory;
    private final JwtService jwtService;
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    public DlHistoryController(HttpSession httpSession, DownloadHistoryService serviceDlHistory, JwtService jwtService) {
        this.httpSession = httpSession;
        this.serviceDlHistory = serviceDlHistory;
        this.jwtService = jwtService;
    }

    @GetMapping("/downloads")
    @ResponseBody
    public ResponseEntity<?> getHistoryList(HttpServletRequest request) throws Exception {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        List<DownloadHistoryVo> lists = serviceDlHistory.getHistoryList();
        List<RSSHistoryList> newList = new ArrayList<RSSHistoryList>();

        for(DownloadHistoryVo list : lists) {
            RSSHistoryList history = new RSSHistoryList();
            SimpleDateFormat conTimeFormat  = new SimpleDateFormat("yyyyMMddHHmmss");
            history.setHistoryId(list.getId());
            history.setType(list.getDl_type());
            history.setDate(list.getDl_date() != null ? conTimeFormat.format(list.getDl_date()) : null);
            history.setFileName(list.getDl_filename());
            history.setUserName(list.getDl_user());
            history.setStatus(list.getDl_status());
            history.setHistoryId(list.getId());
            history.setHistoryId(list.getId());
            newList.add(history);
        }

        resBody.put("lists", newList);
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @PostMapping("/downloads")
    @ResponseBody
    public ResponseEntity<?> addDlHistory(HttpServletRequest request, @RequestBody Map<String, Object> param)  throws Exception {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        String accessToken = request.getHeader("Authorization");

        if(param == null || param.size() == 0 || accessToken == null || accessToken.isEmpty()) {
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }

        String type = param.containsKey("type") ? param.get("type").toString() : null;
        String filename = param.containsKey("filename") ? (String)param.get("filename") : null;
        String status = param.containsKey("status") ? (String)param.get("status") : null;

        if(type == null || type.isEmpty() || filename == null || filename.isEmpty() || status == null || status.isEmpty()) {
            error.setReason(RSSErrorReason.INVALID_PARAMETER);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
        }

        try {
            DownloadHistoryVo dlVo = new DownloadHistoryVo();
            AccessToken decodedAccess = jwtService.decodeAccessToken(accessToken);
            dlVo.setDl_user(decodedAccess.getUserName());
            dlVo.setDl_type(type);
            dlVo.setDl_filename(filename);
            dlVo.setDl_status(status);
            if(!serviceDlHistory.addDlHistory(dlVo)) {
                throw new Exception();
            }
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            error.setReason(RSSErrorReason.INTERNAL_ERROR);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resBody);
        }
    }
}
