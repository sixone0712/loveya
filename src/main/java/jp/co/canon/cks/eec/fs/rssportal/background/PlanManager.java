package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.rssportal.dao.CollectionPlanDao;
import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListService;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class PlanManager extends Thread {

    private final Log log = LogFactory.getLog(getClass());

    @Value("${rssportal.collect.logBase}")
    private String planRootDir;

    @Autowired
    private CollectionPlanDao planDao;

    @Autowired
    private DownloadListService downloadListService;

    @Autowired
    private FileDownloader downloader;

    @Autowired
    private CollectThreadPool thread;
    private List<CollectThread> killList;

    private List<CollectProcess> collects;

    public PlanManager() {

    }

    @PostConstruct
    public void postConstruct() {
        this.start();
    }

    @Override
    public void run() {
        log.info("PlanManager start");
        waitDatabaseReady();
        initPlanModels();

        try {
            while (true) {
                sleep(5000);
                killThread();

                int nextIdx = findNextScheduledPlan();
                if(nextIdx!=-1) {
                    CollectProcess process = collects.get(nextIdx);
                    if (process.getSchedule().before(new Timestamp(System.currentTimeMillis()))) {
                        CollectThread t = thread.getThread();
                        if (t == null) {
                            log.info("no thread available now");
                            continue;
                        }
                        process.setNotifyJobDone(()->{
                            process.freeThreadContainer();
                            killList.add(t);
                        });
                        process.allocateThreadContainer(t);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("error");
            e.printStackTrace();
        }
    }

    private void killThread() {
        for(CollectThread t: killList) {
            thread.putThread(t);
        }
        killList.clear();
    }

    private int findNextScheduledPlan() {
        final long max = System.currentTimeMillis()+(365*24*3600000);
        int nextIdx = -1;
        Timestamp nextTime = new Timestamp(max);
        for(int i = 0; i< collects.size(); ++i) {
            if(collects.get(i).isThreading()) {
                continue;
            }
            Timestamp iSchedule = collects.get(i).getSchedule();
            if(iSchedule.before(nextTime)) {
                nextIdx = i;
                nextTime = iSchedule;
            }
        }
        return nextIdx;
    }

    private void initPlanModels() {
        log.info("initialize all plans");
        collects = new ArrayList<>();
        killList = new ArrayList<>();
        List<CollectPlanVo> plans = planDao.findAll();
        for(CollectPlanVo plan: plans) {
            collects.add(new CollectProcess(this, plan, planDao, downloader, log));
            log.info(plan.toString());
        }
    }

    private void waitDatabaseReady() {
        try {
            while(!planDao.exists()) {
                log.info("wait for database ready");
                sleep(10000);
            }
        } catch (InterruptedException e) {
            log.error("waitDatabaseReady() error");
            e.printStackTrace();
        }
    }

    public void addCollectLog(CollectPlanVo plan, String outputPath) {
        if(plan==null || outputPath==null) {
            log.error("addCollectLog() invalid params");
            return;
        }
        log.info("addCollectLog()");
        downloadListService.insert(plan, outputPath);
    }

    public String getCollectRoot() {
        return "test/"+planRootDir;
    }
}
