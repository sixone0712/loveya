package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import jp.co.canon.cks.eec.fs.rssportal.model.error.RSSError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rss/api/vftp")
public class VftpDownloadController {
    private final HttpSession session;
    private final FileDownloader fileDownloader;
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    public VftpDownloadController(HttpSession session, FileDownloader fileDownloader) {
        this.session = session;
        this.fileDownloader = fileDownloader;
    }

    // Request VFTP Comapt Downlaod
    @PostMapping("/compat/download")
    @ResponseBody
    public ResponseEntity<?> vftpCompatDownloadRequest(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        log.info(String.format("[Post] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    // Cancel VFTP Comapt Download
    @DeleteMapping("/compat/download/{downloadId}")
    @ResponseBody
    public ResponseEntity<?> vftpCompatDownloadCancel(HttpServletRequest request, @PathVariable("downloadId") String downloadId) {
        log.info(String.format("[Delete] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    // Request VFTP Compat Download status
    @GetMapping("/compat/download/{downloadId}")
    @ResponseBody
    public ResponseEntity<?> vftpCompatDownloadStatus(HttpServletRequest request, @PathVariable("downloadId") String downloadId) {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    // Download VFP Compat file
    @RequestMapping("/compat/storage/{downloadId}")
    public ResponseEntity<?> vftpCompatDownloadFile(
            @PathVariable("downloadId") String downloadId,
            HttpServletRequest request,
            HttpServletResponse response) {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    // Search VFTP SSS File
    @PostMapping("/sss")
    @ResponseBody
    public ResponseEntity<?> searchVftpSSSFileListWithThreadPool(HttpServletRequest request, @RequestBody Map<String, Object> requestList) throws Exception {
        log.info(String.format("[Post] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    // Request VFTP SSS Downlaod
    @PostMapping("/sss/download")
    @ResponseBody
    public ResponseEntity<?> vftpSSSDownloadRequest(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        log.info(String.format("[Post] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    // Cancel VFTP SSS Download
    @DeleteMapping("/sss/download/{downloadId}")
    @ResponseBody
    public ResponseEntity<?> vftpSSSDownloadCancel(HttpServletRequest request, @PathVariable("downloadId") String downloadId) {
        log.info(String.format("[Delete] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    // Request VFTP SSS Download status
    @GetMapping("/sss/download/{downloadId}")
    @ResponseBody
    public ResponseEntity<?> vftpSSSDownloadStatus(HttpServletRequest request, @PathVariable("downloadId") String downloadId) {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    // Download VFP SSS file
    @RequestMapping("/sss/storage/{downloadId}")
    public ResponseEntity<?> vftpSSSDownloadFile(
            @PathVariable("downloadId") String downloadId,
            HttpServletRequest request,
            HttpServletResponse response) {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }
}
