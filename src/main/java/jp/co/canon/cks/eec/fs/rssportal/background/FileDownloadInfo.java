package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.portal.bussiness.CustomURL;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import org.springframework.lang.NonNull;

import java.io.File;
import java.net.MalformedURLException;

public class FileDownloadInfo extends DownloadForm {

    private boolean downloadComplete;
    private boolean ftpProcComplete;
    private CustomURL url;
    private File rootDir;
    private File localPath;

    public FileDownloadInfo(@NonNull String tool, @NonNull String logType) {
        super(tool, logType);
    }

    public FileDownloadInfo(@NonNull DownloadForm form) {
        this(form.getTool(), form.getLogType());
        setFiles(form.getFiles());
        downloadComplete = false;
        ftpProcComplete = false;
    }

    public void setUrl(@NonNull final String url) {
        try {
            this.url = new CustomURL(url);
            downloadComplete = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setLocalPath(@NonNull final File localPath) {
        this.localPath = localPath;
        ftpProcComplete = true;
    }

    public CustomURL getUrl() {
        return url;
    }

    public boolean isDownloadComplete() {
        return downloadComplete;
    }

    public boolean isFtpProcComplete() {
        return ftpProcComplete;
    }

    public void setRootDir(@NonNull final File rootDir) {
        this.rootDir = rootDir;
    }

    public File getRootDir() {
        return rootDir;
    }
}
