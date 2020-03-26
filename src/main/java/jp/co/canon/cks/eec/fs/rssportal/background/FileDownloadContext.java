package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.portal.bussiness.CustomURL;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.model.FileInfo;
import org.springframework.lang.NonNull;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FileDownloadContext implements DownloadConfig {

    private final String id;
    private final String user;
    private final String comment;
    private final String system;
    private final String tool;
    private final String logType;

    private final int files;
    private final String[] fileNames;
    private final long[] fileSizes;
    private final Calendar[] fileDates;

    private final DownloadForm downloadForm;

    private String requestNo;
    private boolean downloadComplete;
    private boolean ftpProcComplete;
    private CustomURL achieveUrl;

    private File rootDir;
    private File outFile;
    private String outPath;

    private int downloadFiles;

    public FileDownloadContext(@NonNull String id, @NonNull DownloadForm form) {

        this.downloadForm = form;
        this.id = id;
        this.system = form.getSystem();
        this.tool = form.getTool();
        this.logType = form.getLogType();
        this.user = null;
        this.comment = "";

        files = form.getFiles().size();
        fileNames = new String[files];
        fileSizes = new long[files];
        fileDates = new Calendar[files];

        for(int i=0; i<files; ++i) {
            FileInfo fileInfo = form.getFiles().get(i);
            fileNames[i] = fileInfo.getName();
            fileSizes[i] = fileInfo.getSize();
            fileDates[i] = convertStringToCalendar(fileInfo.getDate());
        }

        Path path = Paths.get(DownloadConfig.ROOT_PATH, id, tool, logType);
        this.outPath = path.toString();

        downloadFiles = 0;
        downloadComplete = false;
        ftpProcComplete = false;
    }

    public void setAchieveUrl(@NonNull final String achieveUrl) {
        try {
            this.achieveUrl = new CustomURL(achieveUrl);
            downloadComplete = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setOutFile(@NonNull final File outFile) {
        this.outFile = outFile;
        ftpProcComplete = true;
    }

    public CustomURL getAchieveUrl() {
        return achieveUrl;
    }

    public boolean isDownloadComplete() {
        return downloadComplete;
    }

    public boolean isFtpProcComplete() {
        return ftpProcComplete;
    }

    public void setFtpProcComplete(boolean ftpProcComplete) {
        this.ftpProcComplete = ftpProcComplete;
    }

    public void setRootDir(@NonNull final File rootDir) {
        this.rootDir = rootDir;
    }

    public File getRootDir() {
        return rootDir;
    }

    public String getSystem() {
        return system;
    }

    public String getTool() {
        return tool;
    }

    public String getComment() {
        return comment;
    }

    public String getUser() {
        return user;
    }

    public String getLogType() {
        return logType;
    }

    public int getFileCount() {
        return files;
    }

    public String[] getFileNames() {
        return fileNames;
    }

    public long[] getFileSizes() {
        return fileSizes;
    }

    public Calendar[] getFileDates() {
        return fileDates;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getOutPath() {
        return outPath;
    }

    public int getDownloadFiles() {
        return downloadFiles;
    }

    public void setDownloadFiles(int downloadFiles) {
        this.downloadFiles = downloadFiles;
    }

    private Calendar convertStringToCalendar(@NonNull final String str) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat fmter = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            cal.setTime(fmter.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }
}
