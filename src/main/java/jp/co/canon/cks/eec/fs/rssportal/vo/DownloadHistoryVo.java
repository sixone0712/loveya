package jp.co.canon.cks.eec.fs.rssportal.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class DownloadHistoryVo {
    private int id;
    private String dl_user;
    private Date dl_date;
    private String dl_type;
    private String dl_filename;
    private String dl_status;
}
