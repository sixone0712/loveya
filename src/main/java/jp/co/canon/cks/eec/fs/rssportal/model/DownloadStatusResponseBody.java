package jp.co.canon.cks.eec.fs.rssportal.model;

import jp.co.canon.cks.eec.fs.rssportal.background.CollectType;
import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Getter
@Setter
public class DownloadStatusResponseBody {

    // Don't use field prefix 'm' here. This is response body entity.
    private String downloadId;
    private String status = "invalid-id";
    private int totalFiles = -1;
    private long downloadedFiles = -1;
    private String downloadUrl = "";

    public DownloadStatusResponseBody(@NonNull FileDownloader fileDownloader, @NonNull final String dlId) {
        this.downloadId = dlId;
        status = fileDownloader.getStatus(dlId);
        totalFiles = fileDownloader.getTotalFiles(dlId);
        downloadedFiles = fileDownloader.getDownloadFiles(dlId);

        if(status.equals("done")) {
            CollectType collectType = fileDownloader.getFtpType(dlId);
            if(collectType==CollectType.vftp_compat) {
                downloadUrl = "/rss/api/vftp/compat/storage/" + dlId;
            } else if(collectType==CollectType.vftp_sss) {
                downloadUrl = "/rss/api/vftp/sss/storage/" + dlId;
            } else {
                downloadUrl = "/rss/api/ftp/storage/" + dlId;
            }
        }
    }
}
