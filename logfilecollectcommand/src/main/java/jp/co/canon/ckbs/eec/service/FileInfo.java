package jp.co.canon.ckbs.eec.service;

public class FileInfo {
    String filename;
    int retry_count = 0;
    boolean failed = false;

    public FileInfo(String filename){
        this.setFilename(filename);
    }

    public void setFilename(String filename){
        this.filename = filename;
    }

    public String getFilename(){
        return  this.filename;
    }

    public void increaseRetryCount(){
        this.retry_count++;
    }

    public int getRetryCount(){
        return retry_count;
    }

    public void setFailed(){
        this.failed = true;
    }

    public boolean isFailed(){
        return failed;
    }
}
