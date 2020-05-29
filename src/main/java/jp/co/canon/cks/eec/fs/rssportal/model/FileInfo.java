package jp.co.canon.cks.eec.fs.rssportal.model;

public class FileInfo {

    private String mName;
    private long mSize;
    private String mDate;
    private long milliTime;

    public FileInfo(String name, long size, String date) {
        mName = name;
        mSize = size;
        mDate = date;
    }

    public FileInfo(String name, long size, String date, long millis) {
        mName = name;
        mSize = size;
        mDate = date;
        this.milliTime = millis;
    }

    public String getName() {return mName;}
    public long getSize() {return mSize;}
    public String getDate() {return mDate;}
    public long getMilliTime() {return milliTime;}
}