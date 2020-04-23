package jp.co.canon.cks.eec.fs.rssportal.model;

import java.util.ArrayList;
import java.util.List;

public class DownloadForm {
    private final String mSystem;
    private String mTool;
    private String mLogType;
    private List<FileInfo> mFiles;

    public DownloadForm(String tool, String logType) {
        this("FS_P", tool, logType);
    }

    public DownloadForm(String system, String tool, String logType) {
        this.mSystem = system;
        mTool = tool;
        mLogType = logType;
        mFiles = new ArrayList<>();
    }

    public void addFile(final String file, final long size, final String date) {
        mFiles.add(new FileInfo(file, size, date));
    }

    /* Getters */
    public String getSystem() {return mSystem;}
    public String getTool() {return mTool;}
    public String getLogType() {return mLogType;}
    public List<FileInfo> getFiles() {return mFiles;}

    /* Setters */
    protected void setFiles(List<FileInfo> files) {
        mFiles = files;
    }
}