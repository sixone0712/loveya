package jp.co.canon.ckbs.eec.fs.collect.service;

public class FileServiceCollectException extends Exception{
    String code;
    public FileServiceCollectException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}
