package jp.co.canon.cks.eec.fs.rssportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jp.co.canon.cks.eec.fs.manage.FileInfoModel;
import jp.co.canon.cks.eec.fs.manage.FileServiceManageServiceLocator;
import jp.co.canon.cks.eec.fs.manage.FileTypeModel;
import jp.co.canon.cks.eec.fs.manage.ToolInfoModel;
import jp.co.canon.cks.eec.fs.rssportal.model.Info.RSSInfoFileSearchReq;
import jp.co.canon.cks.eec.fs.rssportal.model.Info.RSSInfoFileSearchRes;
import jp.co.canon.cks.eec.fs.rssportal.model.Info.RSSInfoLog;
import jp.co.canon.cks.eec.fs.rssportal.model.Info.RSSInfoMpa;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/rss/api/info")
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

    @PostMapping("/files")
    public ResponseEntity<ArrayList<RSSInfoFileSearchRes>> createFileList(@RequestBody RSSInfoFileSearchReq[] requestList) throws Exception {
        log.info("/rss/rest/info/files");

        // Create ThreadPool with 10 threads
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        // Futrure object to hold the result when threads are executed asynchronously
        ArrayList<Future<ArrayList<RSSInfoFileSearchRes>>> futures = new ArrayList<Future<ArrayList<RSSInfoFileSearchRes>>>();

        for (final RSSInfoFileSearchReq list : requestList) {
            // Futrure object to hold the result when threads are executed asynchronously
            Callable<ArrayList<RSSInfoFileSearchRes>> callable = new Callable<ArrayList<RSSInfoFileSearchRes>>() {
                @Override
                public ArrayList<RSSInfoFileSearchRes> call() throws Exception {
                    ArrayList<RSSInfoFileSearchRes> result = new ArrayList<>();
                    Calendar st = null;
                    Calendar ed = null;
                    SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");

                    String fabName = list.getFabName();
                    String mpaName = list.getMpaName();
                    String logName = list.getLogName();
                    String logCode = list.getLogCode();
                    String startDate = list.getStartDate();
                    String endDate = list.getEndDate();
                    String keyword = "";    //list.getKeyword();    //Not currently in use
                    String dir = "";    //list.getDir();            //Not currently in use
                    FileInfoModel[] src = null;
                    int retry = 0;

                    if (startDate != null) {
                        st = Calendar.getInstance();
                        st.setTime(f.parse(startDate));
                    }
                    if (endDate != null) {
                        ed = Calendar.getInstance();
                        ed.setTime(f.parse(endDate));
                    }

                    while(retry < fileServiceRetryCount){
                        try {
                            src = serviceLocator.getFileServiceManage().createFileList(mpaName, logCode, st, ed, keyword, dir);
                            break;
                        } catch(Exception e){
                            e.printStackTrace();
                            retry++;
                            log.error("[createFileList]request failed(retry: " + retry + ")");
                            Thread.sleep(fileServiceRetryInterval);
                        }
                    }

                    if(src == null) {
                        log.error("[createFileList]request Completely failed");
                        return null;
                    }

                    for (int i = 0; i < src.length; i++) {
                        RSSInfoFileSearchRes dest = new RSSInfoFileSearchRes();
                        //if (dest.isFile()) {  //Not currently in use
                            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(src[i].getTimestamp().getTimeInMillis());
                            //dest.setFile(src[i].getType().equals("F"));   //Not currently in use
                            //dest.setFileId(0);    //Not currently in use
                            dest.setLogCode(logCode);
                            dest.setFileName(src[i].getName());
                            dest.setFilePath(src[i].getName());
                            dest.setFileSize(src[i].getSize());
                            dest.setFileDate(timeStamp);
                            //dest.setFileStatus("");   //Not currently in use

                            // additional info
                            dest.setFabName(fabName);
                            dest.setMpaName(mpaName);
                            dest.setLogName(logName);

                            result.add(dest);
                        }
                    //}

                    return result;
                }
            };
            // Execute the generated callables in the threadpool and put the result in the Future list.
            futures.add(threadPool.submit(callable));
        }
        // When the callables that are being executed are finished, the threadpool is terminated (must be done).
        // It is not automatically removed.
        // showdownNow () interrupts even if there is a callable being executed, and forcibly terminates it.
        threadPool.shutdown();

        ArrayList<RSSInfoFileSearchRes> resultList = new ArrayList<>();
        int totalCnt = 0;
        int errCnt = 0;
        for (Future<ArrayList<RSSInfoFileSearchRes>> future : futures) {
            totalCnt++;
            if(future.get() == null) {
                errCnt++;
            } else {
                resultList.addAll(future.get());
            }
        }

        if(totalCnt == errCnt) {
            log.error("[createFileList]There is no response to all requests.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(resultList);
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
