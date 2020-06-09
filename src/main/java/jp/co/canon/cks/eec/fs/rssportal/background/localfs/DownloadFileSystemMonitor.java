package jp.co.canon.cks.eec.fs.rssportal.background.localfs;

import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class DownloadFileSystemMonitor extends FileSystemMonitor {

    private final Log log = LogFactory.getLog(getClass());
    private static final String name = "download-fs-monitor";
    @Value("${rssportal.collect.resultBase}")
    private String path;
    private int _minFreeSpace = 15;    // gigabytes
    private int _minFreeSpacePercent = 25;
    private long _interval = 3600*1000;

    private final FileDownloader fileDownloader;
    
    private List<File> invalidFileList;

    @Autowired
    public DownloadFileSystemMonitor(FileDownloader fileDownloader) {
        super(name);
        this.fileDownloader = fileDownloader;
        invalidFileList = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        configure(path, _minFreeSpace, _minFreeSpacePercent, _interval);
        log.info(name+" thread starts");
    }

    @Override
    protected boolean checkSpecial() {
        File target = new File(path);
        invalidFileList.clear();
        for(File file: target.listFiles()) {
            if(fileDownloader.getStatus(file.getName()).equalsIgnoreCase("invalid-id")) {
                invalidFileList.add(file);
            }
        }
        return invalidFileList.size()==0?false:true;
    }

    @Override
    protected void cleanup() {
        if(invalidFileList.size()==0) {
            log.warn("no file to cleanup");
            return;
        }
        for(File file: invalidFileList) {
            deleteDir(file);
            log.info("downloaded file "+file.getName()+" deleted");
        }
    }

    @Override
    protected void restart() {
    }

    @Override
    protected void halt() {
    }

    @Override
    protected boolean errorHandler(String error) {
        return false;
    }

    @Override
    protected void report(long total, long usable) {
        log.info("filesystem report for "+name);
        log.info("+ total : "+gigabytes(total)+" GB");
        log.info("+ usable : "+gigabytes(usable)+" GB");
        log.info("+ "+percent(total, usable)+" % free");
    }
}
