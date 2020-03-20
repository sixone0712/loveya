package jp.co.canon.cks.eec.fs.rssportal.model;

import java.util.ArrayList;
import java.util.List;

public class DownloadForm {
    private final String mSystem = "FS_P"; // FS_P fixed
    private String mTool;   // Machine
    private String mLogType; // Category
    private List<FileInfo> mFiles;

    public DownloadForm(String tool, String logType) {
        mTool = tool;
        mLogType = logType;
        mFiles = new ArrayList<>();
    }

    public void addFile(final String file, final long size) {
        mFiles.add(new FileInfo(file, size));
    }

    /* Getters */
    public String getSystem() {return mSystem;}
    public String getTool() {return mTool;}
    public String getLogType() {return mLogType;}
    public List<FileInfo> getFiles() {return mFiles;}
}