package jp.co.canon.cks.eec.fs.rssportal.model;

public class FileInfo {

    private String mName;
    private long mSize;

    public FileInfo(String name, long size) {
        mName = name;
        mSize = size;
    }

    public String getName() {return mName;}
    public long getSize() {return mSize;}
}