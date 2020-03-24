package jp.co.canon.cks.eec.fs.rssportal.download.controller;

import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.manage.FileServiceManageServiceLocator;
import jp.co.canon.cks.eec.fs.manage.ToolInfoModel;
import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadStatusResponseBody;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSToolInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FileDownloaderController {

    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping(value="dl/request")
    @ResponseBody
    public String request(@RequestBody Map<String, Object> param) {

        log.warn("request()");

        if(param.size()==0 || param.containsKey("list")==false) {
            log.warn("no target to download");
            return null;
        }

        Map<String, Map<String, DownloadForm>> map = new HashMap<>();

        List<Map<String, Object>> downloadList = (List<Map<String, Object>>) param.get("list");
        for(Map item: downloadList) {

            boolean checkItem = true;
            checkItem &= item.containsKey("machine");
            checkItem &= item.containsKey("category");
            checkItem &= item.containsKey("file");
            checkItem &= item.containsKey("filesize");
            checkItem &= item.containsKey("date");

            if(checkItem) {
                addDownloadItem(map, (String)item.get("machine"), (String)item.get("category"),
                        (String)item.get("file"), (String)item.get("filesize"), (String)item.get("date"));
            } else {
                log.error("parameter failed");
                return null;
            }
        }

        List<DownloadForm> targetList = new ArrayList<>();
        map.forEach((m, submap)->submap.forEach((c, dlForm)->targetList.add(dlForm)));

        log.warn("targetList size="+targetList.size());
        String dlId = FileDownloader.getInstance().addRequest(targetList);
        return dlId;
    }

    @RequestMapping("dl/status")
    @ResponseBody
    public DownloadStatusResponseBody getStatus(@RequestParam Map<String, Object> param) {

        if(param.containsKey("dlId")==false) {
            log.warn("dlId is null");
            return null;
        }
        String dlId = (String)param.get("dlId");

        log.warn("getStatus(dlId="+dlId+")");

        FileDownloader dl = FileDownloader.getInstance();
        if(dl.isValidId(dlId)==false) {
            return null;
        }
        return new DownloadStatusResponseBody(dlId);
    }

    @RequestMapping("dl/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @RequestParam(value="dlId", defaultValue="") String dlId,
            HttpServletResponse response) {
        if(dlId.isEmpty()) {
            log.warn("invalid param");
            return null;
        }
        log.warn("download(dlId="+dlId+")");

        FileDownloader dl = FileDownloader.getInstance();
        if(dl.isValidId(dlId)==false) {
            log.warn("invalid dlId");
            return null;
        }
        if(dl.getStatus(dlId).equals("done")==false) {
            log.warn("in-progress");
            return null;
        }

        String dlPath = dl.getDownloadInfo(dlId);
        log.warn("download path="+dlPath);

        try {
            InputStream is = new FileInputStream(new File(dlPath));
            InputStreamResource isr = new InputStreamResource(is);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(Files.size(Paths.get(dlPath)));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            response.setHeader("Content-Disposition", "attachment; filename="+"test.zip");
            return new ResponseEntity(isr, headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }



    private void addDownloadItem(final Map map, final String machine, final String category, final String file, final String size, final String date) {

        DownloadForm form;

        if(map.containsKey(machine)) {
            Map<String, DownloadForm> submap = (Map<String, DownloadForm>) map.get(machine);
            if(submap.containsKey(category)) {
                form = submap.get(category);
            } else {
                form = new DownloadForm(machine, category);
                submap.put(category, form);
            }
        } else {
            form = new DownloadForm(machine, category);
            Map<String, DownloadForm> submap = new HashMap<>();
            submap.put(category, form);
            map.put(machine, submap);
        }

        if(form==null) {
            log.error("fatal: addDownloadItem could not find form");
            return;
        }
        form.addFile(file, Long.parseLong(size), date);
    }



}
