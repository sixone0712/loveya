package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get;

import lombok.Getter;

public class GetFileItem {
    private @Getter String path;
    private @Getter String name;
    private @Getter String destDir;

    public GetFileItem(String path, String name, String destDir){
        this.path = path;
        this.name = name;
        this.destDir = destDir;
    }
}