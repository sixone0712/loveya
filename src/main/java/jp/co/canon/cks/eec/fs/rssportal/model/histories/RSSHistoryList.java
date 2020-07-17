package jp.co.canon.cks.eec.fs.rssportal.model.histories;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RSSHistoryList {
    private int historyId;
    private String type;
    private String date;
    private String fileName;
    private String userName;
    private String status;
}