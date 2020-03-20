package jp.co.canon.cks.eec.fs.rssportal.model;

import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import org.springframework.lang.NonNull;

import java.util.List;

public class DownloadStatusResponseBody {

    // Don't use field prefix 'm' here. This is response body entity.
    private String dlId;
    private String status = "invalid-id";
    private int totalFiles = -1;
    private int completeFiles = -1;
    private List<String> totalFileList = null;

    public DownloadStatusResponseBody(@NonNull final String dlId) {
        this.dlId = dlId;
        FileDownloader dl = FileDownloader.getInstance();
        if(dl.isValidId(dlId)==false) {
            status = "invalid-id";
            return;
        }
        status = dl.getStatus(dlId);
        totalFileList = dl.getTotalFileList(dlId);
        // FIXME
    }

    public String getDlId() {
        return dlId;
    }

    public String getStatus() {
        return status;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getCompleteFiles() {
        return completeFiles;
    }

    public List<String> getTotalFileList() {
        return totalFileList;
    }

    public void setDlId(String dlId) {
        this.dlId = dlId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public void setCompleteFiles(int completeFiles) {
        this.completeFiles = completeFiles;
    }

    public void setTotalFileList(List<String> totalFileList) {
        this.totalFileList = totalFileList;
    }
}
