package jp.co.canon.ckbs.eec.fs.manage.service;

public class FileServiceManageException extends Exception{
    String code;

    public FileServiceManageException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}
