package jp.co.canon.cks.eec.fs.rssportal.downloadlist;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/rss/rest/downloadlist")
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

    @RequestMapping("/list")
    public ResponseEntity<List<DownloadListVo>> getList(HttpServletRequest request,
                                                        @RequestParam(name="planId") int planId) {
        String path = request.getServletPath();
        log.info("request "+path);
        List<DownloadListVo> list;
        list = service.getList(planId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping("/delete")
    public ResponseEntity delete(HttpServletRequest request,
                                         @RequestParam(name="id") int id) {
        String path = request.getServletPath();
        log.info("request "+path);
        DownloadListVo item = service.get(id);
        if(item==null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        service.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
