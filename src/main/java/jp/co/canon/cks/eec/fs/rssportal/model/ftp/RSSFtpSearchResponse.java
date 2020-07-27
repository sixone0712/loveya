package jp.co.canon.cks.eec.fs.rssportal.model.ftp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RSSFtpSearchResponse {
    private String fabName = "";
    private String machineName = "";
    private String categoryName = "";
    private String categoryCode = "";
    //private long fileId = 0;          //Not currently in use
    //private String fileStatus = "";   //Not currently in use
    private String fileName = "";
    private long fileSize = 0;
    private String fileDate = "";
    private String filePath = "";
    private String fileType = "";
    //private boolean file = false;     //Not currently in use
}
