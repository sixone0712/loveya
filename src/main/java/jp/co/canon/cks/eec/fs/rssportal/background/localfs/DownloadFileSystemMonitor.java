package jp.co.canon.cks.eec.fs.rssportal.background.localfs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class DownloadFileSystemMonitor extends FileSystemMonitor {

    private final Log log = LogFactory.getLog(getClass());
    private static final String name = "download-fs-monitor";
    private static final String _path = "download";
    private static final int _minFreeSpace = 15;    // gigabytes
    private static final int _minFreeSpacePercent = 25;
    private static final long _interval = 3600*1000;
    private static final long _keepPeriod = 24*3600*1000;

    public DownloadFileSystemMonitor() {
        super(name, _path, _minFreeSpace, _minFreeSpacePercent, _interval);
        log.info(name+" thread starts");
    }

    @Override
    protected boolean checkSpecial() {
        return false;
    }

    @Override
    protected void cleanup() {

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
