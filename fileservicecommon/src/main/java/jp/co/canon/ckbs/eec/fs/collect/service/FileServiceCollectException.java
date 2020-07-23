package jp.co.canon.ckbs.eec.fs.collect.service;

public class FileServiceCollectException extends Exception{
    int code;
    public FileServiceCollectException(int code, String message){
        super(message);
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }
}
