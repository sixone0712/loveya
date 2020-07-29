package jp.co.canon.cks.eec.fs.rssportal.background;

public class VFtpSssDownloadRequestForm extends DownloadRequestForm{

    private String directory;

    public VFtpSssDownloadRequestForm(String fab, String machine, String directory) {
        super("vftp-sss", fab, machine);
        this.directory = directory;
    }
}
