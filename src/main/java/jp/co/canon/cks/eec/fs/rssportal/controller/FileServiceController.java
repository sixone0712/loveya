package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnectorFactory;
import jp.co.canon.ckbs.eec.fs.manage.service.CategoryList;
import jp.co.canon.ckbs.eec.fs.manage.service.MachineList;
import jp.co.canon.cks.eec.fs.rssportal.Defines.RSSErrorReason;
import jp.co.canon.cks.eec.fs.rssportal.model.error.RSSError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rss/api/infos")
public class FileServiceController {
    @Value("${rssportal.file-collect-service.retry}")
    private int fileServiceRetryCount;

    @Value("${rssportal.file-collect-service.retry-interval}")
    private int fileServiceRetryInterval;

    @Value("${rssportal.file-service-manager.addr}")
    private String fileServiceAddress;

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    FileServiceManageConnectorFactory connectorFactory;

    @GetMapping("/machines")
    @ResponseBody
    public ResponseEntity<?> getMachines() throws Exception {
        log.info("[Get] /rss/api/infos/machines");
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        MachineList machineList = null;
        int retry = 0;

        while (retry < fileServiceRetryCount) {
            try {
                machineList = connectorFactory.getConnector(fileServiceAddress).getMachineList();
                break;
            } catch (Exception e) {
                retry++;
                log.error("[machines]request failed(retry: " + retry + ")");
                log.error("[machines]request failed: " + e);
                Thread.sleep(fileServiceRetryInterval);
            }
        }

        if (machineList == null) {
            log.error("[machines]request totally failed");
            error.setReason(RSSErrorReason.INTERNAL_ERROR);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resBody);
        }

        resBody.put("lists", machineList.getMachines());
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @GetMapping(value = {"/categories", "/categories/{machineName}"})
    @ResponseBody
    public ResponseEntity<?> getCategories(@PathVariable(value = "machineName", required = false) String machineName) throws Exception {
        log.info("[Get] /rss/api/infos/machines/" + machineName);
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        CategoryList categoryList = null;
        int retry = 0;

        while (retry < fileServiceRetryCount) {
            try {
                if (machineName == null || machineName.isEmpty()) {
                    categoryList = connectorFactory.getConnector(fileServiceAddress).getCategoryList();
                } else {
                    categoryList = connectorFactory.getConnector(fileServiceAddress).getCategoryList(machineName);
                }
                break;
            } catch (Exception e) {
                retry++;
                log.error("[categories]request failed(retry: " + retry);
                Thread.sleep(fileServiceRetryInterval);
            }
        }

        if (categoryList == null) {
            log.error("[categories]request totally failed");
            error.setReason(RSSErrorReason.INTERNAL_ERROR);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resBody);
        }

        resBody.put("lists", categoryList.getCategories());
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

}