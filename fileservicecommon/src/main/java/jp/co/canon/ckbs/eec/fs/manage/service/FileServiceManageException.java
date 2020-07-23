package jp.co.canon.ckbs.eec.fs.manage.service;

public class FileServiceManageException extends Exception{
    int code;

    public FileServiceManageException(int code, String message){
        super(message);
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }
}
