package jp.co.canon.cks.eec.fs.rssportal.background;

import lombok.Getter;
import lombok.Setter;

@Getter
public class DownloadRequestForm {

    protected String ftpType;
    protected String fab;
    protected String machine;

    public DownloadRequestForm(String ftpType, String fab, String machine) {
        this.ftpType = ftpType;
        this.fab = fab;
        this.machine = machine;
    }
}
