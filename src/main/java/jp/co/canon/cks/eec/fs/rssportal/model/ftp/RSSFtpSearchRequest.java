package jp.co.canon.cks.eec.fs.rssportal.model.ftp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RSSFtpSearchRequest {
    private String fabName = "";
    private String machineName = "";
    private String categoryCode = "";
    private String categoryName = "";
    private String startDate = "";
    private String endDate = "";
    private String keyword = "";      //Not currently in use
    private String dir = "";          //Not currently in use

    public RSSFtpSearchRequest getClone() {
        RSSFtpSearchRequest clone = new RSSFtpSearchRequest();
        clone.fabName = this.fabName;
        clone.machineName = this.machineName;
        clone.categoryCode = this.categoryCode;
        clone.categoryName = this.categoryName;
        clone.startDate = this.startDate;
        clone.endDate = this.endDate;
        clone.keyword = this.keyword;
        clone.dir = this.dir;
        return clone;
    }
}