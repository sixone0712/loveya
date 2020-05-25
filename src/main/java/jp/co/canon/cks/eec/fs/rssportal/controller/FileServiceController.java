package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.manage.FileInfoModel;
import jp.co.canon.cks.eec.fs.manage.FileServiceManageServiceLocator;
import jp.co.canon.cks.eec.fs.manage.FileTypeModel;
import jp.co.canon.cks.eec.fs.manage.ToolInfoModel;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSFileInfoBeanResponse;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSLogInfoBean;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSRequestSearch;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSToolInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/rss/rest/soap")
public class FileServiceController {

    private final Log log = LogFactory.getLog(getClass());
    FileServiceManageServiceLocator serviceLocator = new FileServiceManageServiceLocator();

    @GetMapping("/createToolList")
    public RSSToolInfo[] createToolList() throws Exception {
        ToolInfoModel[] result = serviceLocator.getFileServiceManage().createToolList();
        ToolInfoModel[] toolModels = result;
        if (toolModels == null) toolModels = new ToolInfoModel[0];

        ArrayList<RSSToolInfo> toolList = new ArrayList<>();
        for (int i = 0; i < toolModels.length; i++) {
            RSSToolInfo tool = new RSSToolInfo();
            tool.setStructId(toolModels[i].getStructId());
            tool.setTargetname(toolModels[i].getName());
            tool.setTargettype(toolModels[i].getType());
            toolList.add(tool);
        }

        RSSToolInfo[] arrToolList = toolList.toArray(new RSSToolInfo[toolList.size()]);
        return arrToolList;
    }

    @GetMapping("/createFileTypeList")
    public RSSLogInfoBean[] createFileTypeList(@RequestParam(name="tool") String tool) throws Exception {
        if (tool == null) {
            return null;
        }

        String FILE_SELECT_IN_DIR_PAGE = "FileListSelectInDirectory";
        String FILE_SELECT_PAGE = "FileListSelect";
        FileTypeModel[] ftList = serviceLocator.getFileServiceManage().createFileTypeList(tool);

        if (ftList == null) ftList = new FileTypeModel[0];

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

        return r;
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


    @PostMapping("/createFileList")
    public RSSFileInfoBeanResponse[] createFileList(@RequestBody RSSRequestSearch[] requestList) throws Exception {
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

                    if (startDate != null) {
                        st = Calendar.getInstance();
                        st.setTime(f.parse(startDate));
                    }
                    if (endDate != null) {
                        ed = Calendar.getInstance();
                        ed.setTime(f.parse(endDate));
                    }

                    FileInfoModel[] src = serviceLocator.getFileServiceManage().createFileList(toolId, logId, st, ed, keyword, dir);

                    if (src == null) src = new FileInfoModel[0];

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
        for (Future<ArrayList<RSSFileInfoBeanResponse>> future : futures) {
            resultList.addAll(future.get());
        }

        RSSFileInfoBeanResponse[] array = resultList.toArray(new RSSFileInfoBeanResponse[resultList.size()]);

        return array;
    }
}

