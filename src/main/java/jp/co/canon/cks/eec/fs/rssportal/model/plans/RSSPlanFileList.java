package jp.co.canon.cks.eec.fs.rssportal.model.plans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class RSSPlanFileList {
    private int planId;
    private String planName;
    private int fileId;
    private String created;
    private String status;
    private String downloadUrl;
}
