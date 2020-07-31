package jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class FileDownloadInfo {
    private long requestFiles;
    private long downloadFiles;
    private long requestBytes;
    private long downloadBytes;
    private boolean error;
    private boolean finish;
}
