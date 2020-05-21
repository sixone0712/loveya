package jp.co.canon.cks.eec.fs.rssportal.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class DownloadHistoryVo {

    private int dw_id;
    private String dw_user;
    private Date dw_date;
    private String dw_type;
    private String dw_filelist;


}
