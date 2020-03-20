package jp.co.canon.cks.eec.fs.rssportal.download.controller;

import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadStatusResponseBody;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Controller
public class FileDownloaderController {

    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping(value="dl/request")
    @ResponseBody
    public String reqTest(@RequestBody Map<String, Object> param) {

        log.warn("reqTest()");
        log.warn("param size="+param.size());

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

            if(checkItem) {
                addDownloadItem(map, (String)item.get("machine"), (String)item.get("category"), (String)item.get("file"), (String)item.get("filesize"));
            } else {
                log.error("parameter failed");
                return null;
            }
        }

        List<DownloadForm> targetList = new ArrayList<>();
        map.forEach((m, submap)->submap.forEach((c, dlForm)->targetList.add(dlForm)));

        /*
        map.forEach(new BiConsumer<String, Map<String, DownloadForm>>() {
            @Override
            public void accept(String s, Map<String, DownloadForm> submap) {
                submap.forEach(new BiConsumer<String, DownloadForm>() {
                    @Override
                    public void accept(String s, DownloadForm downloadForm) {
                        targetList.add(downloadForm);
                    }
                });
            }
        });
         */

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
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam(value="dlId", defaultValue="") String dlId) {
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

        // FIXME
        return null;
    }

    private void addDownloadItem(final Map map, final String machine, final String category, final String file, final String size) {

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
        form.addFile(file, Long.parseLong(size));
    }



}
