package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get;

import lombok.Getter;
import lombok.Setter;

public class GetFileParam {
    private @Getter @Setter String server;
    private @Getter @Setter String path;
    private @Getter @Setter String filename;
}