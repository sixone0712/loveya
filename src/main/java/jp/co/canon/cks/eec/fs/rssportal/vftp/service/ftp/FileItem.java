package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import lombok.Getter;
import lombok.Setter;

public class FileItem {
    private @Getter @Setter String server;
    private @Getter @Setter String path;
    private @Getter @Setter String filename;
    private @Getter @Setter long filesize;
}