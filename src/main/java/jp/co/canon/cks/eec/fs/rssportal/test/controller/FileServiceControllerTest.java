package jp.co.canon.cks.eec.fs.rssportal.test.controller;

import jp.co.canon.cks.eec.fs.rssportal.model.RSSLogInfoBean;
import jp.co.canon.cks.eec.fs.rssportal.model.RSSToolInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/test")
public class FileServiceControllerTest {
    @GetMapping("/createToolList")
    public RSSToolInfo[] createToolList() throws Exception {

        ArrayList<RSSToolInfo> toolList = new ArrayList<>();
        String[] targetname = {
                "EQVM88",
                "EQVM87",
                "VSSVM87A5",
                "VSSVM87B2",
                "VSSVM88B6",
                "VSSVM87C4",
                "VSSVM87C42",
                "VSSVM88VSSG",
                "VSSVM86D5",
                "VSSVM86B3",
                "HPCONS",
        };

        String[] structId ={
                "CR7",
                "CR7",
                "VSS",
                "VSS",
                "VSS",
                "VSS",
                "VSS",
                "VSS",
                "VSS",
                "VSS",
                "VSS",
        };

        for (int i = 0; i < 11; i++) {

            RSSToolInfo tool = new RSSToolInfo();
            tool.setStructId(structId[i]);    // 2011.11.29 modify by J.Tsuruta
            tool.setTargetname(targetname[i]);
            tool.setTargettype("Common");
            toolList.add(tool);
        }

        RSSToolInfo[] arrToolList = toolList.toArray(new RSSToolInfo[toolList.size()]);
        return arrToolList;
    }

    @GetMapping("/createFileTypeList")
    public RSSLogInfoBean[] createFileTypeList() throws Exception {
        String FILE_SELECT_IN_DIR_PAGE = "FileListSelectInDirectory";
        String FILE_SELECT_PAGE = "FileListSelect";

        RSSLogInfoBean[] r = new RSSLogInfoBean[5];

        String[] code = {"001", "002", "003", "004", "005", "006"};
        String[] logName = {"001 RUNNING STATUS", "002 RUNNING STATUS [event]", "003 Error Log", "004 Operation Log", "005 Console Log", "006 Logserver ALL DATA"};
        String[] searchType = {"1", "1", "1", "1", "1", "1"};

        for (int i = 0; i < 5; i++) {
            r[i] = new RSSLogInfoBean();
            r[i].setCode(code[i]);
            r[i].setLogName(logName[i]);
            int v = Integer.parseInt(searchType[i]);
            switch ((v & 0x03)) {
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
}

