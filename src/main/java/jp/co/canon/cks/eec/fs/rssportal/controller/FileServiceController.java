package jp.co.canon.cks.eec.fs.rssportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jp.co.canon.cks.eec.fs.manage.FileServiceManageServiceLocator;
import jp.co.canon.cks.eec.fs.manage.FileTypeModel;
import jp.co.canon.cks.eec.fs.manage.ToolInfoModel;
import jp.co.canon.cks.eec.fs.rssportal.model.Infos.RSSInfosCategory;
import jp.co.canon.cks.eec.fs.rssportal.model.Infos.RSSInfosMachine;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rss/api/infos")
public class FileServiceController {

    @Value("${rssportal.property.constructdisplay}")
    private String contructDisplay;
    @Value("${rssportal.file-collect-service.retry}")
    private int fileServiceRetryCount;
    @Value("${rssportal.file-collect-service.retry-interval}")
    private int fileServiceRetryInterval;
    @Value("${rssportal.constructDisplayTree}")
    public String[] constructDisplayTree;
    private final Log log = LogFactory.getLog(getClass());

    FileServiceManageServiceLocator serviceLocator = new FileServiceManageServiceLocator();

    @GetMapping("/fabs")
    @ResponseBody
    public ResponseEntity<?> getFabs() throws FileNotFoundException {
        log.info("[Get] /rss/api/infos/fabs");
        JSONObject lists = new JSONObject();
        JSONArray res = new JSONArray();
        String output = null;

        try (InputStream inputStream = new FileInputStream(new File(contructDisplay))) {
            String xml = IOUtils.toString(inputStream);
            JSONObject jObject = XML.toJSONObject(xml);
            JSONObject ConstructDisplay = jObject.getJSONObject("ConstructDisplay");
            log.debug("ConstructDisplay: " + ConstructDisplay.toString());
            JSONArray Tree = ConstructDisplay.getJSONArray("Tree");

            for (int i = 0; i < Tree.length(); i++) {
                JSONObject obj = Tree.getJSONObject(i);
                String title = obj.getString("name");
                for (String treeName : constructDisplayTree) {
                    if (title.equals(treeName)) {
                        JSONArray Child = null;
                        try {
                            Child = obj.getJSONArray("Child");
                            for (int j = 0; j < Child.length(); j++) {
                                JSONObject fabs = Child.getJSONObject(j);
                                String name = fabs.getString("name");
                                String id = Integer.toString(fabs.getInt("id"));
                                JSONObject newFab = new JSONObject();
                                newFab.put("fabName", name);
                                newFab.put("fabId", id);
                                res.put(newFab);
                            }
                        } catch (Exception e) {
                            log.info("[fabs] Chlid does not exist.[ " + e + "]");
                        }
                    }
                }
            }
            lists.put("lists", res);

            // Pring Log
            /*
            for (int i = 0; i < res.length(); i++) {
                JSONObject obj = res.getJSONObject(i);
                String fabName = obj.getString("fabName");
                int fabId = obj.getInt("fabId");
                log.info("fabName: " + fabName);
                log.info("fabId: " + fabId);
            }
             */

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            Object json = mapper.readValue(lists.toString(), Object.class);
            output = mapper.writeValueAsString(json);
            return ResponseEntity.status(HttpStatus.OK).body(output);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("[createFileList]HttpStatus: INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public String findFabName(JSONArray fabs, String findId) {
        if (fabs.length() != 0 && fabs != null) {
            for (int i = 0; i < fabs.length(); i++) {
                JSONObject fab = fabs.getJSONObject(i);
                String fabName = fab.getString("fabName");
                String fabId = fab.getString("fabId");
                //log.info("fabName: " + fabName);
                //log.info("fabId: " + fabId);
                if (fabId.equals(findId)) {
                    return fabName;
                }
            }
        }
        return null;
    }

    @GetMapping("/machines")
    @ResponseBody
    //public ResponseEntity<ArrayList<RSSInfoMachine>> getMachines() throws Exception {
    public ResponseEntity<?> getMachines() throws Exception {
        log.info("[Get] /rss/api/infos/machines");
        String fabs = null;
        JSONObject jsonParse = null;
        JSONArray fabList = null;
        try {
            fabs = (String) getFabs().getBody();
            jsonParse = new JSONObject(fabs);
            fabList = jsonParse.getJSONArray("lists");
        } catch (JSONException e) {
            log.error("[machines]request getFabs failed");
            log.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        ToolInfoModel[] result = null;
        int retry = 0;
        while (retry < fileServiceRetryCount) {
            try {
                result = serviceLocator.getFileServiceManage().createToolList();
                break;
            } catch (Exception e) {
                retry++;
                log.error("[machines]request failed(retry: " + retry);
                Thread.sleep(fileServiceRetryInterval);
            }
        }

        if (result == null) {
            log.error("[machines]request totally failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        ToolInfoModel[] toolModels = result;
        ArrayList<RSSInfosMachine> mpaList = new ArrayList<>();
        for (int i = 0; i < toolModels.length; i++) {
            RSSInfosMachine machine = new RSSInfosMachine();
            String fabName = findFabName(fabList, toolModels[i].getStructId());
            if (fabName != null && !fabName.equals("")) {
                machine.setFabName(fabName);
                machine.setMachineName(toolModels[i].getName());
                mpaList.add(machine);
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("lists", mpaList);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/categories/{machineName}")
    @ResponseBody
    public ResponseEntity<?> getCategories(@PathVariable("machineName") String machineName) throws Exception {
        log.info("[Get] /rss/api/infos/machines/" + machineName);
        // Not currently used
        //String FILE_SELECT_IN_DIR_PAGE = "FileListSelectInDirectory";
        //String FILE_SELECT_PAGE = "FileListSelect";
        FileTypeModel[] ftList = null;
        int retry = 0;

        if (machineName == null) {
            log.error("[categories]param(tool) is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        while (retry < fileServiceRetryCount) {
            try {
                ftList = serviceLocator.getFileServiceManage().createFileTypeList(machineName);
                break;
            } catch (Exception e) {
                retry++;
                log.error("[categories]request failed(retry: " + retry);
                Thread.sleep(fileServiceRetryInterval);
            }
        }

        if (ftList == null) {
            log.error("[categories]request totally failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        RSSInfosCategory[] r = new RSSInfosCategory[ftList.length];
        for (int i = 0; i < ftList.length; i++) {
            r[i] = new RSSInfosCategory();
            r[i].setCategoryCode(ftList[i].getLogType());
            r[i].setCategoryName(ftList[i].getDataName());
            // Not currently used
            /*
            int v = Integer.parseInt(ftList[i].getSearchType());
            switch((v & 0x03)) {
                case 3:
                    r[i].setCategoryType(0);
                    break;
                case 1:
                    r[i].setCategoryType(1);
                    break;
                case 2:
                    r[i].setCategoryType(2);
                    break;
                default:
                    r[i].setCategoryType(3);
            }
            */

            // Not currently used
            /*
            if ((v & 0x10) == 0x10) {
                r[i].setFileListForwarding(FILE_SELECT_IN_DIR_PAGE);
            } else {
                r[i].setFileListForwarding(FILE_SELECT_PAGE);
            }
            */
        }

        Map<String, Object> res = new HashMap<>();
        res.put("lists", r);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}