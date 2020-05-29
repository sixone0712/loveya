package jp.co.canon.cks.eec.fs.rssportal.model;

import java.util.ArrayList;
import java.util.List;

public class DownloadForm {
    private final String system;
    private final String fab;
    private final String tool;
    private final String logType;
    private final String logTypeStr;
    private List<FileInfo> files;

    public DownloadForm(String fab, String tool, String logType, String logTypeStr) {
        this("FS_P", fab, tool, logType, logTypeStr);
    }

    public DownloadForm(String system, String fab, String tool, String logType, String logTypeStr) {
        this.system = system;
        this.fab = fab;
        this.tool = tool;
        this.logType = logType;
        this.logTypeStr = logTypeStr;
        files = new ArrayList<>();
    }

    public void addFile(final String file, final long size, final String date) {
        files.add(new FileInfo(file, size, date));
    }

    public void addFile(final String file, final long size, final String date, final long millis) {
        files.add(new FileInfo(file, size, date, millis));
    }

    /* Getters */
    public String getSystem() {return system;}
    public String getFab() {return fab;}
    public String getTool() {return tool;}
    public String getLogType() {return logType;}
    public String getLogTypeStr() {return logTypeStr;}
    public List<FileInfo> getFiles() {return files;}

    /* Setters */
    protected void setFiles(List<FileInfo> files) {
        this.files = files;
    }
}