package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadStatusResponseBody;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/rss/rest/dl")
public class FileDownloaderController {

    private final HttpSession session;
    private final FileDownloader fileDownloader;

    @Autowired
    public FileDownloaderController(HttpSession session, FileDownloader fileDownloader) {
        this.session = session;
        this.fileDownloader = fileDownloader;
    }

    @RequestMapping(value="/request")
    @ResponseBody
    public String request(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        log.info(String.format("request \"%s\"", request.getServletPath()));
        if(param.size()==0 || param.containsKey("list")==false) {
            log.warn("no target to download");
            return null;
        }
        param.forEach((key, value)->log.info("key="+key+"\nvalue="+value));

        List<DownloadForm> targetList = new ArrayList<>();
        Map<String, Map<String, DownloadForm>> map = new HashMap<>();
        List<Map<String, Object>> downloadList = (List<Map<String, Object>>) param.get("list");

        for(Map item: downloadList) {
            boolean checkItem = true;
            String fab = (String) item.get("structId");
            String tool = (String) item.get("machine");
            String logType = (String) item.get("category");
            String logTypeStr = (String) item.get("categoryName");
            String file = (String) item.get("file");
            String fileSize = (String) item.get("filesize");
            String date = (String) item.get("date");
            boolean isFile = (boolean)item.get("isFile"); // if an item doesn't contains 'isFile', it occurs NullPointException.

            if(fab!=null && tool!=null && logType!=null && logTypeStr!=null && file!=null && fileSize!=null
                    && date!=null) {
                if(isFile) {
                    addDownloadItem(map, fab, tool, logType, logTypeStr, file, fileSize, date);
                } else {
                    try {
                        fileDownloader.createDownloadFileList(targetList, fab, tool, logType, logTypeStr, null, null, file);
                    } catch (InterruptedException e) {
                        log.error("stopped creating download list");
                        return null;
                    }
                }
            } else {
                log.error("parameter failed");
                return null;
            }
        }

        map.forEach((m, submap)->submap.forEach((c, dlForm)->targetList.add(dlForm)));

        log.warn("targetList size="+targetList.size());
        String dlId = fileDownloader.addRequest(targetList);
        return dlId;
    }

    @RequestMapping("/status")
    @ResponseBody
    public DownloadStatusResponseBody getStatus(HttpServletRequest request, @RequestParam Map<String, Object> param) {
        log.info(String.format("request \"%s\"", request.getServletPath()));
        if(param.containsKey("dlId")==false) {
            log.warn("dlId is null");
            return null;
        }
        String dlId = (String)param.get("dlId");

        log.trace("getStatus(dlId="+dlId+")");

        if(fileDownloader.isValidId(dlId)==false) {
            return null;
        }
        return new DownloadStatusResponseBody(fileDownloader, dlId);
    }

    @RequestMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @RequestParam(value="dlId", defaultValue="") String dlId,
            HttpServletRequest request,
            HttpServletResponse response) {
        log.info(String.format("request \"%s?dlId=%s\"", request.getServletPath(), dlId));

        if(dlId.isEmpty()) {
            log.error("invalid param");
            return null;
        }

        if(fileDownloader.isValidId(dlId)==false) {
            log.error("invalid dlId");
            return null;
        }
        if(fileDownloader.getStatus(dlId).equals("done")==false) {
            log.error("in-progress");
            return null;
        }

        String dlPath = fileDownloader.getDownloadInfo(dlId);
        log.info("download path="+dlPath);

        try {
            InputStream is = new FileInputStream(new File(dlPath));
            InputStreamResource isr = new InputStreamResource(is);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(Files.size(Paths.get(dlPath)));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            response.setHeader("Content-Disposition", "attachment; filename="+createZipFilename(dlId));
            return new ResponseEntity(isr, headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping("/cancel")
    @ResponseBody
    public ResponseEntity<String> cancelDownload(HttpServletRequest request,
                                                 @RequestParam(value="dlId") String downloadId) {
        log.info(String.format("request \"%s?dlId=%s\"", request.getServletPath(), downloadId));
        if(!fileDownloader.cancelRequest(downloadId))
            return new ResponseEntity("invalid download id", HttpStatus.NOT_FOUND);
        return new ResponseEntity("ok", HttpStatus.OK);
    }

    private String createZipFilename(String downloadId) {
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

        List<String> fabs = fileDownloader.getFabs(downloadId);
        if(fabs==null || fabs.size()==0) {
            log.error("no fab info");
            return null;
        }
        String fab = fabs.get(0);
        if(fabs.size()>1) {
            for(int i=1; i<fabs.size(); ++i)
                fab += "_"+fabs.get(i);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String cur = dateFormat.format(new Date(System.currentTimeMillis()));

        String fileName = String.format("%s_%s_%s.zip", username, fab, cur);
        log.info("filename = "+fileName);
        return fileName;
    }

    private void addDownloadItem(final Map map, String fab, String tool, String logType, String logTypeStr,
                                 String file, String size, String date) {

        DownloadForm form;

        // We have to consider sub-directories now.
        String[] paths = file.split("/");
        if(paths.length!=1) {
            // This file places at the sub-directory.
            // In this case, a key composes with 'logType/sub-directory-name' pattern.
            logType = logType+"/"+paths[0];
        }

        if(map.containsKey(tool)) {
            Map<String, DownloadForm> submap = (Map<String, DownloadForm>) map.get(tool);
            if(submap.containsKey(logType)) {
                form = submap.get(logType);
            } else {
                form = new DownloadForm("FS_P#M", fab, tool, logType, logTypeStr);
                submap.put(logType, form);
            }
        } else {
            form = new DownloadForm("FS_P#M", fab, tool, logType, logTypeStr);
            Map<String, DownloadForm> submap = new HashMap<>();
            submap.put(logType, form);
            map.put(tool, submap);
        }

        if(form==null) {
            log.error("fatal: addDownloadItem could not find form");
            return;
        }
        form.addFile(file, Long.parseLong(size), date);
    }

    private final Log log = LogFactory.getLog(getClass());
}
