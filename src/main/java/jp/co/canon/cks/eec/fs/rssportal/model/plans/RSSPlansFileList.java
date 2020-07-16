package jp.co.canon.cks.eec.fs.rssportal.model.plans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class RSSPlansFileList {
    private int planId;
    private int fileId;
    private String created;
    private String status;
    private String downloadUrl;
}
