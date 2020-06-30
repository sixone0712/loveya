package jp.co.canon.cks.eec.fs.rssportal.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Calendar;

@Getter
@Setter
@ToString
public class RSSRequestSearch {
    String structId = "";
    String targetName = "";     // toolId
    String targetType = "";
    int logType = 0;
    String logCode = "";        //logId
    String logName = "";
    String startDate = "";
    String endDate = "";
    String keyword = "";
    String dir = "";

    public RSSRequestSearch getClone() {
        RSSRequestSearch clone = new RSSRequestSearch();
        clone.structId = this.structId;
        clone.targetName = this.targetName;
        clone.targetType = this.targetType;
        clone.logType = this.logType;
        clone.logCode = this.logCode;
        clone.logName = this.logName;
        clone.startDate = this.startDate;
        clone.endDate = this.endDate;
        clone.keyword = this.keyword;
        clone.dir = this.dir;
        return clone;
    }
}
