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
}
