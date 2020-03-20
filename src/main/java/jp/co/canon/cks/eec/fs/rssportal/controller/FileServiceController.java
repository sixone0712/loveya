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

@RestController
@RequestMapping("/api")
public class FileServiceController {

    FileServiceManageServiceLocator serviceLocator = new FileServiceManageServiceLocator();

    @GetMapping("/createToolList")
    public RSSToolInfo[] createToolList() throws Exception {

        ToolInfoModel[] result = serviceLocator.getFileServiceManage().createToolList();
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

    @Async
    @PostMapping("/createFileList")
    public RSSFileInfoBeanResponse[] createFileList(@RequestBody RSSRequestSearch[] requestList) throws Exception {
        ArrayList<RSSFileInfoBeanResponse> resultList = new ArrayList<>();

        for(RSSRequestSearch request :  requestList) {
            Calendar st = null;
            Calendar ed = null;
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
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

            if (src == null) src = new FileInfoModel[0];

            for (int i = 0; i < src.length; i++) {
                RSSFileInfoBeanResponse dest = new RSSFileInfoBeanResponse();
                if(dest.isFile()) {
                    dest.setFile(src[i].getType().equals("F"));
                    dest.setFileId(0);
                    dest.setLogId(logId);
                    dest.setFileName(src[i].getName());
                    dest.setFilePath(src[i].getName());
                    dest.setFileSize(src[i].getSize());
                    dest.setFileDate(Long.toString(src[i].getTimestamp().getTimeInMillis()));
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
}

