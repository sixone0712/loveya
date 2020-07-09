package jp.co.canon.cks.eec.fs.rssportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jp.co.canon.cks.eec.fs.manage.*;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSFileInfoBeanResponse;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSLogInfoBean;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSRequestSearch;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSToolInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/rss/rest/soap")
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
    public ResponseEntity<?> getFabs() throws FileNotFoundException {
        log.info("/rss/rest/info/fabs");

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
                                res.put(newFab);
                            }
                        } catch (Exception e) {
                            log.info("[fabs] Chlid does not exist.[ " + e + "]");
                        }
                    }
                }
            }

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
            Object json = mapper.readValue(res.toString(), Object.class);
            output = mapper.writeValueAsString(json);
            return ResponseEntity.status(HttpStatus.OK).body(output);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("[createFileList]HttpStatus: INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/mpas")
    public ResponseEntity<ArrayList<RSSInfoMpa>> getMpas() throws Exception {
        log.info("/rss/rest/info/maps");
        ToolInfoModel[] result = null;
        int retry = 0;
        while(retry < fileServiceRetryCount){
            try {
                result = serviceLocator.getFileServiceManage().createToolList();
                break;
            } catch(Exception e){
                retry++;
                log.error("[mpas]request failed(retry: " + retry);
                Thread.sleep(fileServiceRetryInterval);
            }
        }

        if(result == null) {
            log.error("[mpas]request totally failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        ToolInfoModel[] toolModels = result;
        ArrayList<RSSInfoMpa> mpaList = new ArrayList<>();
        for (int i = 0; i < toolModels.length; i++) {
            RSSInfoMpa mpa = new RSSInfoMpa();
            mpa.setFabId(toolModels[i].getStructId());
            mpa.setMpaName(toolModels[i].getName());
            mpa.setMpaType(toolModels[i].getType());
            mpaList.add(mpa);
        }

        return ResponseEntity.status(HttpStatus.OK).body(mpaList);
    }

    @GetMapping("/logs/{logName}")
    public ResponseEntity<RSSInfoLog[]> getLogs(@PathVariable("logName") String logName) throws Exception {
        log.info("/rss/rest/info/logs");
        // Not currently used
        //String FILE_SELECT_IN_DIR_PAGE = "FileListSelectInDirectory";
        //String FILE_SELECT_PAGE = "FileListSelect";
        FileTypeModel[] ftList = null;
        int retry = 0;

        if (logName == null) {
            log.error("[logs]param(tool) is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        while(retry < fileServiceRetryCount){
            try {
                ftList = serviceLocator.getFileServiceManage().createFileTypeList(logName);
                break;
            } catch(Exception e){
                retry++;
                log.error("[logs]request failed(retry: " + retry);
                Thread.sleep(fileServiceRetryInterval);
            }
        }

        if(ftList == null) {
            log.error("[logs]request totally failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        RSSInfoLog[] r = new RSSInfoLog[ftList.length];
        for (int i = 0; i < ftList.length; i++) {
            r[i] = new RSSInfoLog();
            r[i].setLogCode(ftList[i].getLogType());
            r[i].setLogName(ftList[i].getDataName());
            int v = Integer.parseInt(ftList[i].getSearchType());
            switch((v & 0x03)) {
                case 3:
                    r[i].setLogType(0);
                    break;
                case 1:
                    r[i].setLogType(1);
                    break;
                case 2:
                    r[i].setLogType(2);
                    break;
                default:
                    r[i].setLogType(3);
            }

            // Not currently used
            /*
            if ((v & 0x10) == 0x10) {
                r[i].setFileListForwarding(FILE_SELECT_IN_DIR_PAGE);
            } else {
                r[i].setFileListForwarding(FILE_SELECT_PAGE);
            }
            */
        }
        return ResponseEntity.status(HttpStatus.OK).body(r);
    }

    private boolean createFileList(FileServiceManage manager,
                                   List<RSSFileInfoBeanResponse> list,
                                   RSSRequestSearch request) {
        if(manager==null || list==null || request==null)
            return false;

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        try {
            from.setTime(format.parse(request.getStartDate()));
            to.setTime(format.parse(request.getEndDate()));
        } catch (ParseException e) {
            log.error("[createFileList] failed to parse datetime ("+request.getStructId()+":"+request.getLogCode());
            return false;
        }

        FileInfoModel[] fileInfo;
        int retry = 0;
        while(retry<fileServiceRetryCount) {
            try {
                fileInfo = manager.createFileList(
                        request.getTargetName(),
                        request.getLogCode(),
                        from,
                        to,
                        request.getKeyword(),
                        request.getDir());

                for(FileInfoModel file: fileInfo) {
                    if(file.getName().endsWith(".") || file.getName().endsWith("..") || file.getSize()==0)
                        continue;
                    if(file.getType().equals("D")) {
                        RSSRequestSearch child = request.getClone();
                        child.setDir(file.getName());
                        if(!createFileList(manager, list, child)) {
                            log.warn(String.format("[createFileList]connection error (%s %s %s)",
                                    request.getStructId(), request.getLogCode(), request.getDir()));
                        }
                    } else {
                        RSSFileInfoBeanResponse info = new RSSFileInfoBeanResponse();
                        info.setFile(true);
                        info.setFileId(0);
                        info.setLogId(request.getLogCode());
                        info.setFileName(file.getName());
                        String[] paths = file.getName().split("/");
                        if(paths.length>1) {
                            int lastIndex = file.getName().lastIndexOf("/");
                            info.setFilePath(file.getName().substring(0, lastIndex));
                        } else {
                            info.setFilePath(".");
                        }
                        info.setFileSize(file.getSize());
                        info.setFileDate(format.format(file.getTimestamp().getTime()));
                        info.setFileStatus("");
                        info.setStructId(request.getStructId());
                        info.setTargetName(request.getTargetName());
                        info.setLogName(request.getLogName());
                        list.add(info);
                    }
                }
                return true;
            } catch (RemoteException e) {
                log.error("[createFileList]request failed(retry: " + (++retry) + ")");
                try {
                    Thread.sleep(fileServiceRetryInterval);
                } catch (InterruptedException interruptedException) {
                    log.error("[createFileList]failed to sleep");
                }
            }
        }
        return false;
    }

    @PostMapping("/createFileList")
    public ResponseEntity<RSSFileInfoBeanResponse[] > createFileList(@RequestBody RSSRequestSearch[] requestList) throws Exception {
        log.info("/rss/rest/soap/createFileList");
        List<RSSFileInfoBeanResponse> list = new ArrayList<>();
        for(RSSRequestSearch request: requestList) {
            if(!createFileList(serviceLocator.getFileServiceManage(), list, request)) {
                log.warn("[createFileList]failed to connect "+request.getStructId());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(list.toArray(new RSSFileInfoBeanResponse[0]));
    }

    /*
    @Async
    @PostMapping("/createFileList")
    public RSSFileInfoBeanResponse[] createFileList(@RequestBody RSSRequestSearch[] requestList) throws Exception {
        ArrayList<RSSFileInfoBeanResponse> resultList = new ArrayList<>();

        System.out.println(requestList);

        for(RSSRequestSearch request :  requestList) {
            Calendar st = null;
            Calendar ed = null;
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
            String structId = request.getStructId();
            String targetName = request.getTargetName();
            String logName = request.getLogName();
            String startDate = request.getStartDate();
            String endDate = request.getEndDate();
            String toolId = request.getTargetName();
            String logId = request.getLogCode();
            String keyword = request.getKeyword();
            String dir = request.getDir();

            if (startDate != null) {
                st = Calendar.getInstance();
                st.setTime(f.parse(startDate));
            }
            if (endDate != null) {
                ed = Calendar.getInstance();
                ed.setTime(f.parse(endDate));
            }

            FileInfoModel[] src = serviceLocator.getFileServiceManage().createFileList(toolId, logId, st, ed, keyword, dir);
            //FileInfoModel[] src = serviceLocator.getFileServiceManage(null).createFileList(toolId, logId, st, ed, keyword, dir);

            if (src == null) src = new FileInfoModel[0];

            for (int i = 0; i < src.length; i++) {
                RSSFileInfoBeanResponse dest = new RSSFileInfoBeanResponse();
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(src[i].getTimestamp().getTimeInMillis());
                if(dest.isFile()) {
                    dest.setFile(src[i].getType().equals("F"));
                    dest.setFileId(0);
                    dest.setLogId(logId);
                    dest.setFileName(src[i].getName());
                    dest.setFilePath(src[i].getName());
                    dest.setFileSize(src[i].getSize());
                    //dest.setFileDate(Long.toString(src[i].getTimestamp().getTimeInMillis()));
                    dest.setFileDate(timeStamp);
                    dest.setFileStatus("");

                    // additional info
                    dest.setStructId(structId);
                    dest.setTargetName(targetName);
                    dest.setLogName(logName);

                    resultList.add(dest);
                }
            }
        }

        RSSFileInfoBeanResponse[] array = resultList.toArray(new RSSFileInfoBeanResponse[resultList.size()]);

        return array;
    }
    */
}
