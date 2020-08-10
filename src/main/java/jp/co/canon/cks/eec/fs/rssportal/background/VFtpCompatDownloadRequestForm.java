package jp.co.canon.cks.eec.fs.rssportal.background;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class VFtpCompatDownloadRequestForm extends DownloadRequestForm {

    private String command;
    private boolean decompress;

    public VFtpCompatDownloadRequestForm(String fab, String machine, String command, boolean decompress) {
        super("vftp_compat", fab, machine);
        this.command = command;
        this.decompress = decompress;
    }
}
