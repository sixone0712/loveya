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

    String netName;

    public RequestFileInfo(){

    }

    public RequestFileInfo(String name){
        this.name = name;
        netName = this.name;

        int idx = this.name.lastIndexOf("/");
        if (idx >= 0){
            netName = this.name.substring(idx + 1);
        }
    }

    public String netFileName(){
        return this.netName;
    }
}
