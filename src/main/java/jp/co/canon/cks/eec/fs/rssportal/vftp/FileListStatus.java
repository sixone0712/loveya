package jp.co.canon.cks.eec.fs.rssportal.vftp;

import java.util.ArrayList;

import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.FileItem;
import lombok.Getter;
import lombok.Setter;

public class FileListStatus {
    private @Getter @Setter String requestNo;
    private @Getter @Setter String path;
    private @Getter @Setter String directory;
    private @Getter @Setter Status status;
    private @Getter @Setter FileItem[] filelist;

    public static enum Status {
        NONE,
        PROCESSING,
        COMPLETED,
        FAILED,
    }
}