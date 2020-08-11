package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.rssportal.model.FileInfo;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class VFtpSssDownloadRequestForm extends DownloadRequestForm{

    @Getter
    private String directory;

    @Getter
    private List<FileInfo> files;

    public VFtpSssDownloadRequestForm(String fab, String machine, String directory) {
        super("vftp_sss", fab, machine);
        this.directory = directory;
        files = new ArrayList<>();
    }

    public void addFile(final String file, final long size) {
        files.add(new FileInfo(file, size, null));
    }
}
