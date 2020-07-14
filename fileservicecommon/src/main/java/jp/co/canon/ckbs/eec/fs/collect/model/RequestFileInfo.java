package jp.co.canon.ckbs.eec.fs.collect.model;

import lombok.Getter;
import lombok.Setter;

public class RequestFileInfo {
    @Getter @Setter
    String name;

    @Getter @Setter
    long size;

    @Getter @Setter
    boolean downloaded = false;

    @Getter @Setter
    String downloadPath;

    public RequestFileInfo(){

    }

    public RequestFileInfo(String name){
        this.name = name;
    }
}
