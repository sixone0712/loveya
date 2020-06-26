package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.portal.bussiness.CustomURL;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
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

    private final String jobType;
    private final String id;
    private final String user;
    private final String comment;
    private final String system;
    private final String tool;
    private final String logType;
    private final String logTypeStr;

    private final int files;
    private final String[] fileNames;
    private final long[] fileSizes;
    private final Calendar[] fileDates;

    private final DownloadForm downloadForm;

    private FileServiceManage fileManager;
    private FileServiceModel fileService;

    private String requestNo;
    private boolean downloadComplete;
    private boolean ftpProcComplete;
    private CustomURL achieveUrl;
    private String localFilePath;

    private File rootDir;
    private File outFile;
    private String outPath;
    private String subDir;

    private int downloadFiles;

    public FileDownloadContext(@NonNull String jobType, @NonNull String id, @NonNull DownloadForm form, @NonNull String baseDir) {

        this.jobType = jobType;
        this.downloadForm = form;
        this.id = id;
        this.system = form.getSystem();
        this.tool = form.getTool();
        this.logType = form.getLogType();
        this.logTypeStr = form.getLogTypeStr();
        this.user = "eecAdmin";
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

        // check the first file name in the array and, if a file name includes '/'
        // we consider it placed in a sub directory.
        String[] firsts = fileNames[0].split("/");
        if(firsts.length!=1) {
            subDir = firsts[0];
        } else {
            subDir = null;
        }

        Path path = Paths.get(baseDir, tool, logTypeStr==null?logType:logTypeStr);
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

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
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

    public FileServiceManage getFileManager() {
        return fileManager;
    }

    public void setFileManager(FileServiceManage fileManager) {
        this.fileManager = fileManager;
    }

    public FileServiceModel getFileService() {
        return fileService;
    }

    public void setFileService(FileServiceModel fileService) {
        this.fileService = fileService;
    }

    public String getJobType() {
        return jobType;
    }

    public String getSubDir() {
        return subDir;
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
