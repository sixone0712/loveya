package jp.co.canon.cks.eec.fs.rssportal.background;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class VFtpCompatDownloadRequestForm extends DownloadRequestForm {

    private String command;

    public VFtpCompatDownloadRequestForm(String fab, String machine, String command) {
        super("vftp_compat", fab, machine);
        this.command = command;
    }
}
