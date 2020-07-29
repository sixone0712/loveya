package jp.co.canon.cks.eec.fs.rssportal.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class DownloadForm {
    private String ftpType;
    private String system;
    private String fab;
    private String tool;
    private String logType;
    private String logTypeStr;
    private List<FileInfo> files;
    private String command;

    public DownloadForm(String system, String fab, String tool, String logType, String logTypeStr) {
        this.ftpType = "ftp";
        this.system = system;
        this.fab = fab;
        this.tool = tool;
        this.logType = logType;
        this.logTypeStr = logTypeStr;
        files = new ArrayList<>();
    }

    public DownloadForm(String ftpType, String machine, String command) {
        this.ftpType = ftpType;
        this.tool = machine;
        this.command = command;
    }

    public void addFile(final String file, final long size, final String date) {
        files.add(new FileInfo(file, size, date));
    }

    public void addFile(final String file, final long size, final String date, final long millis) {
        files.add(new FileInfo(file, size, date, millis));
    }
}