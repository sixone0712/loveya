package jp.co.canon.ckbs.eec.fs.manage.service.configuration.legacy.toolinfo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class ToolInfo {
    @Getter @Setter
    String name;

    @Getter @Setter
    String version;

    ArrayList<FtpInfo> ftpInfos = new ArrayList<>();

    ArrayList<LogData> logDataList= new ArrayList<>();

    public void addFtpInfo(FtpInfo ftpInfo){
        if (ftpInfo != null) {
            ftpInfos.add(ftpInfo);
        }
    }

    public void addLogData(LogData logData){
        if (logData != null) {
            logDataList.add(logData);
        }
    }

}
