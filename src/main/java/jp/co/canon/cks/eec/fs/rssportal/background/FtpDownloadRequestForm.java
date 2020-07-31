package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.rssportal.model.FileInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class FtpDownloadRequestForm extends DownloadRequestForm {

    private String categoryType;
    private String categoryName;
    private String directory;
    private List<FileInfo> files;

    public FtpDownloadRequestForm(String fab, String machine, String categoryType, String categoryName) {
        super("ftp", fab, machine);
        this.categoryType = categoryType;
        this.categoryName = categoryName;
        files = new ArrayList<>();
    }

    public void addFile(final String file, final long size, final String date) {
        files.add(new FileInfo(file, size, date));
    }

    public void addFile(final String file, final long size, final String date, final long millis) {
        files.add(new FileInfo(file, size, date, millis));
    }
}