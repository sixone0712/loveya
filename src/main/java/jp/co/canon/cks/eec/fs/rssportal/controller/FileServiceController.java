package jp.co.canon.cks.eec.fs.rssportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jp.co.canon.cks.eec.fs.manage.FileInfoModel;
import jp.co.canon.cks.eec.fs.manage.FileServiceManageServiceLocator;
import jp.co.canon.cks.eec.fs.manage.FileTypeModel;
import jp.co.canon.cks.eec.fs.manage.ToolInfoModel;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/rss/rest/soap")
public class FileServiceController {

    @Value("${rssportal.property.constructdisplay}")
    private String contructDisplay;
    @Value("${rssportal.file-collect-service.retry}")
    private int fileServiceRetryCount;
    @Value("${rssportal.file-collect-service.retry-interval}")
    private int fileServiceRetryInterval;

    private final Log log = LogFactory.getLog(getClass());
    FileServiceManageServiceLocator serviceLocator = new FileServiceManageServiceLocator();

    @GetMapping("/getFabName")
    public ResponseEntity<?> getGenre() throws FileNotFoundException {
        log.info("/rss/rest/soap/getFabName");
        log.info("[getFabName]contructDisplay: " + contructDisplay);
        try (InputStream inputStream = new FileInputStream(new File(contructDisplay))) {
            String xml = IOUtils.toString(inputStream);
            JSONObject jObject = XML.toJSONObject(xml);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            Object json = mapper.readValue(jObject.toString(), Object.class);
            String output = mapper.writeValueAsString(json);
            return ResponseEntity.status(HttpStatus.OK).body(json);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("[createFileList]HttpStatus: INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/createToolList")
    public ResponseEntity<RSSToolInfo[]> createToolList() throws Exception {
        log.info("/rss/rest/soap/createToolList");
        ToolInfoModel[] result = null;
        int retry = 0;
        while(retry < fileServiceRetryCount){
            try {
                result = serviceLocator.getFileServiceManage().createToolList();
                break;
            } catch(Exception e){
                retry++;
                log.error("[createToolList]request failed(retry: " + retry);
                Thread.sleep(fileServiceRetryInterval);
            }
        }

        if(result == null) {
            log.error("[createToolList]request totally failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        ToolInfoModel[] toolModels = result;
        ArrayList<RSSToolInfo> toolList = new ArrayList<>();
        for (int i = 0; i < toolModels.length; i++) {
            RSSToolInfo tool = new RSSToolInfo();
            tool.setStructId(toolModels[i].getStructId());
            tool.setTargetname(toolModels[i].getName());
            tool.setTargettype(toolModels[i].getType());
            toolList.add(tool);
        }

        RSSToolInfo[] arrToolList = toolList.toArray(new RSSToolInfo[toolList.size()]);
        return ResponseEntity.status(HttpStatus.OK).body(arrToolList);
    }

    @GetMapping("/createFileTypeList")
    public ResponseEntity<RSSLogInfoBean[]> createFileTypeList(@RequestParam(name="tool") String tool) throws Exception {
        log.info("/rss/rest/soap/createFileTypeList");
        String FILE_SELECT_IN_DIR_PAGE = "FileListSelectInDirectory";
        String FILE_SELECT_PAGE = "FileListSelect";
        FileTypeModel[] ftList = null;
        int retry = 0;

        if (tool == null) {
            log.error("[createFileTypeList]param(tool) is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        while(retry < fileServiceRetryCount){
            try {
                ftList = serviceLocator.getFileServiceManage().createFileTypeList(tool);
                break;
            } catch(Exception e){
                retry++;
                log.error("[createFileTypeList] request failed(retry: " + retry);
                Thread.sleep(fileServiceRetryInterval);
            }
        }

        if(ftList == null) {
            log.error("[createFileTypeList]request totally failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        RSSLogInfoBean[] r = new RSSLogInfoBean[ftList.length];
        for (int i = 0; i < ftList.length; i++) {
            r[i] = new RSSLogInfoBean();
            r[i].setCode(ftList[i].getLogType());
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
            if ((v & 0x10) == 0x10) {
                r[i].setFileListForwarding(FILE_SELECT_IN_DIR_PAGE);
            } else {
                r[i].setFileListForwarding(FILE_SELECT_PAGE);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(r);
    }

    @PostMapping("/createFileList")
    public ResponseEntity<RSSFileInfoBeanResponse[] > createFileList(@RequestBody RSSRequestSearch[] requestList) throws Exception {
        log.info("/rss/rest/soap/createFileList");

        // Create ThreadPool with 10 threads
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        // Futrure object to hold the result when threads are executed asynchronously
        ArrayList<Future<ArrayList<RSSFileInfoBeanResponse>>> futures = new ArrayList<Future<ArrayList<RSSFileInfoBeanResponse>>>();

        for (final RSSRequestSearch list : requestList) {
            // Futrure object to hold the result when threads are executed asynchronously
            Callable<ArrayList<RSSFileInfoBeanResponse>> callable = new Callable<ArrayList<RSSFileInfoBeanResponse>>() {
                @Override
                public ArrayList<RSSFileInfoBeanResponse> call() throws Exception {
                    ArrayList<RSSFileInfoBeanResponse> result = new ArrayList<>();
                    Calendar st = null;
                    Calendar ed = null;
                    SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
                    String structId = list.getStructId();
                    String targetName = list.getTargetName();
                    String logName = list.getLogName();
                    String startDate = list.getStartDate();
                    String endDate = list.getEndDate();
                    String toolId = list.getTargetName();
                    String logId = list.getLogCode();
                    String keyword = list.getKeyword();
                    String dir = list.getDir();
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
                            src = serviceLocator.getFileServiceManage().createFileList(toolId, logId, st, ed, keyword, dir);
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
                        RSSFileInfoBeanResponse dest = new RSSFileInfoBeanResponse();
                        if (dest.isFile()) {
                            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(src[i].getTimestamp().getTimeInMillis());
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

                            result.add(dest);
                        }
                    }

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

        ArrayList<RSSFileInfoBeanResponse> resultList = new ArrayList<>();
        int totalCnt = 0;
        int errCnt = 0;
        for (Future<ArrayList<RSSFileInfoBeanResponse>> future : futures) {
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

        RSSFileInfoBeanResponse[] array = resultList.toArray(new RSSFileInfoBeanResponse[resultList.size()]);

        return ResponseEntity.status(HttpStatus.OK).body(array);
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
