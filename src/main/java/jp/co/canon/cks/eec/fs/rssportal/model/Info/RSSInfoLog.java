package jp.co.canon.cks.eec.fs.rssportal.model.Info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RSSInfoLog {
    private int logType = 0;
    private String logCode = "";
    private String logName = "";
    //private String fileListForwarding = null;     //Not currently in use
}
