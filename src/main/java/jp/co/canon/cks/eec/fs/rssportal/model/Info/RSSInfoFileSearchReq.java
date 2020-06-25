package jp.co.canon.cks.eec.fs.rssportal.model.Info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RSSInfoFileSearchReq {
    String fabName = "";
    String mpaName = "";
    String logCode = "";
    String logName = "";
    String startDate = "";
    String endDate = "";
    //String keyword = "";      //Not currently in use
    //String dir = "";          //Not currently in use
}
