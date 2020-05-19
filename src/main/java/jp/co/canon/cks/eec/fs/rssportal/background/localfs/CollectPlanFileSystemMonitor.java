package jp.co.canon.cks.eec.fs.rssportal.background.localfs;

import jp.co.canon.cks.eec.fs.rssportal.background.CollectPlanner;
import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListService;
import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

@Component
public class CollectPlanFileSystemMonitor extends FileSystemMonitor {

    private final Log log = LogFactory.getLog(getClass());
    private static final String name = "collect-plan-fs-monitor";
    private static final String _path = "planroot";
    private static final int _minFreeSpace = 10;    // gigabytes
    private static final int _minFreeSpacePercent = 20;
    private static final long _interval = 1800*1000;
    private static final long _keepPeriod = 24*3600*1000;

    private final DownloadListService downloadService;
    private final CollectPlanner collectPlanner;

    private List<DownloadListVo> cleanupList;

    @Autowired
    public CollectPlanFileSystemMonitor(DownloadListService downloadService, CollectPlanner collectPlanner) {
        super(name, _path, _minFreeSpace, _minFreeSpacePercent, _interval);
        this.downloadService = downloadService;
        this.collectPlanner = collectPlanner;
        log.info(name+" thread starts");
    }

    @Override
    protected boolean checkSpecial() {
        List<DownloadListVo> list = downloadService.getFinishedList();
        cleanupList.clear();
        for(DownloadListVo item: list) {
            Timestamp keepPoint = new Timestamp(System.currentTimeMillis()-_keepPeriod);
            if(item.getCreated().before(keepPoint)) {
                cleanupList.add(item);
            }
        }
        return cleanupList.size()>0?true:false;
    }

    @Override
    protected void cleanup() {
        if(cleanupList.size()==0) {
            log.warn("no item to cleanup");
            return;
        }
        for(DownloadListVo item: cleanupList) {
            File file = new File(item.getPath());
            if(file.exists()) {
                file.delete();
            }
            downloadService.delete(item.getId());
            log.info("downloadlist/"+item.getPlanId()+"/"+item.getTitle()+" deleted");
        }
    }

    @Override
    protected void restart() {
        log.info("restart collecting");
        collectPlanner.restart();
    }

    @Override
    protected void halt() {
        log.info("halt collecting");
        collectPlanner.halt();
    }

    @Override
    protected boolean errorHandler(String error) {
        log.error(error);
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
