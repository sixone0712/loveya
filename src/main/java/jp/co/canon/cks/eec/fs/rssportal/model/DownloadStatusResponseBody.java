package jp.co.canon.cks.eec.fs.rssportal.model;

import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import org.springframework.lang.NonNull;

import java.util.List;

public class DownloadStatusResponseBody {

    // Don't use field prefix 'm' here. This is response body entity.
    private String dlId;
    private String status = "invalid-id";
    private int totalFiles = -1;
    private int downloadFiles = -1;
    private List<String> totalFileList = null;

    public DownloadStatusResponseBody(@NonNull FileDownloader fileDownloader, @NonNull final String dlId) {
        this.dlId = dlId;
        if(fileDownloader.isValidId(dlId)==false) {
            status = "invalid-id";
            return;
        }
        status = fileDownloader.getStatus(dlId);
        //totalFileList = dl.getTotalFileList(dlId);
        totalFiles = fileDownloader.getTotalFiles(dlId);
        downloadFiles = fileDownloader.getDownloadFiles(dlId);
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

    public int getDownloadFiles() {
        return downloadFiles;
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

    public void setDownloadFiles(int downloadFiles) {
        this.downloadFiles = downloadFiles;
    }

    public void setTotalFileList(List<String> totalFileList) {
        this.totalFileList = totalFileList;
    }
}
