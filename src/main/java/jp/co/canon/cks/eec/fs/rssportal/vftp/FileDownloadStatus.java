package jp.co.canon.cks.eec.fs.rssportal.vftp;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

public class FileDownloadStatus {
    private @Getter @Setter String requestNo;
    private @Getter @Setter Status status;
    private @Getter @Setter int requestFileCount;
    private @Getter @Setter int downloadFileCount;
    private @Setter String downloadPath;
    private @Getter @Setter String downloadFileName;

    public static enum Status {
        NONE,
        PROCESSING_DOWNLOAD,
        PROCESSING_COMPRESS,
        COMPLETED,
        FAILED,
    }

    public File createDownloadFile(){
        if (downloadPath == null || downloadFileName == null){
            return null;
        }
        File f = new File(downloadPath, downloadFileName);
        if (f.exists()){
            return f;
        }
        return null;
    }
}