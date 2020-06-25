package jp.co.canon.cks.eec.fs.rssportal.model.Info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RSSInfoFileSearchRes {
    private String fabName = "";
    private String mpaName = "";
    private String logName = "";
    private String logCode = "";
    //private long fileId = 0;          //Not currently in use
    //private String fileStatus = "";   //Not currently in use
    private String fileName = "";
    private long fileSize = 0;
    private String fileDate = "";
    private String filePath = "";
    //private boolean file = true;      //Not currently in use
}
