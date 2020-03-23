package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.manage.FileInfoModel;
import jp.co.canon.cks.eec.fs.manage.FileServiceManageServiceLocator;
import jp.co.canon.cks.eec.fs.manage.FileTypeModel;
import jp.co.canon.cks.eec.fs.manage.ToolInfoModel;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSFileInfoBeanResponse;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSLogInfoBean;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSRequestSearch;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSToolInfo;
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
@RequestMapping("/api")
public class FileServiceController {

    FileServiceManageServiceLocator serviceLocator = new FileServiceManageServiceLocator();

    @GetMapping("/createToolList")
    public RSSToolInfo[] createToolList() throws Exception {

        ToolInfoModel[] result = serviceLocator.getFileServiceManage().createToolList();
        //ToolInfoModel[] result = serviceLocator.getFileServiceManage(null).createToolList();
        ToolInfoModel[] toolModels = result;
        if (toolModels == null) toolModels = new ToolInfoModel[0];    // 2011.11.29 add by J,Tsuruta

        ArrayList<RSSToolInfo> toolList = new ArrayList<>();
        for (int i = 0; i < toolModels.length; i++) {

            // 임시로.....
            if(!toolModels[i].getStructId().equals("SYS")) {
                System.out.print("structId : "  + toolModels[i].getStructId() + "\n");
                RSSToolInfo tool = new RSSToolInfo();
                tool.setStructId(toolModels[i].getStructId());    // 2011.11.29 modify by J.Tsuruta
                tool.setTargetname(toolModels[i].getName());
                tool.setTargettype(toolModels[i].getType());
                toolList.add(tool);
            }
        }

        RSSToolInfo[] arrToolList = toolList.toArray(new RSSToolInfo[toolList.size()]);
        return arrToolList;
    }

    @GetMapping("/createFileTypeList")
    public RSSLogInfoBean[] createFileTypeList() throws Exception {
        String FILE_SELECT_IN_DIR_PAGE = "FileListSelectInDirectory";
        String FILE_SELECT_PAGE = "FileListSelect";
        FileTypeModel[] ftList = serviceLocator.getFileServiceManage().createFileTypeList("EQVM88");
        //FileTypeModel[] ftList = serviceLocator.getFileServiceManage(null).createFileTypeList("EQVM88");

        if (ftList == null) ftList = new FileTypeModel[0];	// 2011.11.29 add by J,Tsuruta

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

        // 10개의 Thread를 가진 ThreadPool생성
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        // Thread들이 비동기로 수행되면 그 결과를 담을 Futrure 객체
        ArrayList<Future<ArrayList<RSSFileInfoBeanResponse>>> futures = new ArrayList<Future<ArrayList<RSSFileInfoBeanResponse>>>();

        for (final RSSRequestSearch list : requestList) {
            // callable 객체를 통해 어떤 일을 수행할지 결정한다.
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
                    //FileInfoModel[] src = serviceLocator.getFileServiceManage(null).createFileList(toolId, logId, st, ed, keyword, dir);

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
            // 생성된 callable들을 threadpool에서 수행시키고 결과는 Future 목록에 담는다.
            futures.add(threadPool.submit(callable));
        }
        // 수행중인 callable들이 다 끝나면 threadpool을 종료시킨다.(반드시 해야함) // 자동으로 제거되지 않는다.
        // showdownNow()는 수행중인 callable이 있더라도 인터럽트시켜 강제 종료한다.
        threadPool.shutdown();

        ArrayList<RSSFileInfoBeanResponse> resultList = new ArrayList<>();
        for (Future<ArrayList<RSSFileInfoBeanResponse>> future : futures) {
            resultList.addAll(future.get());
        }

        RSSFileInfoBeanResponse[] array = resultList.toArray(new RSSFileInfoBeanResponse[resultList.size()]);

        return array;
    }
}

