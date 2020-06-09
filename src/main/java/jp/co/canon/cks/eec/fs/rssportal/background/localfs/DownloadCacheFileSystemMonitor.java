package jp.co.canon.cks.eec.fs.rssportal.background.localfs;

import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import lombok.extern.apachecommons.CommonsLog;
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
public class DownloadCacheFileSystemMonitor extends FileSystemMonitor {

    private final Log log = LogFactory.getLog(getClass());
    private static final String monitorName = "download-cache-fs-monitor";

    @Value("${rssportal.collect.cacheBase}")
    private String monitoringPath;
    @Value("${rssportal.purger.download-cache.min-size}")
    private int minSize;
    @Value("#{T(Integer).parseInt('${rssportal.purger.download-cache.min-percent}')}")
    private int minPercent;
    @Value("${rssportal.purger.download-cache.interval}")
    private long checkInterval;
    @Value("${rssportal.purger.download-cache.keeping-period}")
    private long keepingPeriod;
    private long keepingPeriodMillis;

    private final FileDownloader downloader;
    private List<File> cleanupList;

    @Autowired
    protected DownloadCacheFileSystemMonitor(FileDownloader downloader) {
        super(monitorName);
        this.downloader = downloader;
        cleanupList = new ArrayList<>();
    }

    @PostConstruct
    public void postConstruct() {
        configure(monitoringPath, minSize, minPercent, checkInterval * 1000);
        keepingPeriodMillis = keepingPeriod*3600000;
        log.info(monitorName+" thread starts");
    }

    @Override
    protected boolean isReady() {
        File file = new File(monitoringPath);
        if(file.exists() && file.isDirectory())
            return true;
        log.warn("no directory to check [path="+monitoringPath+"]");
        return false;
    }

    @Override
    protected boolean checkSpecial() {
        cleanupList.clear();
        File dir = new File(monitoringPath);
        if (!dir.exists())
            return false;
        for(File file: dir.listFiles()) {
            if(file.isFile()) {
                cleanupList.add(file);
            } else {
                String downloadId = file.getName();
                long lastUpdate = downloader.getLastUpdateTime(downloadId);
                if(lastUpdate<0) {
                    // it means there isn't a job that has this download id.
                    cleanupList.add(file);
                } else {
                    long cur = System.currentTimeMillis();
                    if((cur-lastUpdate)>keepingPeriodMillis) {
                        cleanupList.add(file);
                    }
                }
            }
        }
        return cleanupList.size()==0?false:true;
    }

    @Override
    protected void cleanup() {
        for(File file: cleanupList)
            deleteDir(file);
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

    }
}
