package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.rssportal.dao.CollectionPlanDao;
import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListService;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    private List<CollectPlanModel> models;

    public PlanManager() {
        this.start();
    }

    @Override
    public void run() {
        log.info("PlanManager start");
        waitDatabaseReady();
        initPlanModels();

        try {
            while (true) {
                sleep(1000);
            }
        } catch (InterruptedException e) {
            log.error("error");
            e.printStackTrace();
        }
    }

    private void initPlanModels() {
        log.info("initialize all plans");
        models = new ArrayList<>();
        List<CollectPlanVo> plans = planDao.findAll();
        for(CollectPlanVo plan: plans) {
            models.add(new CollectPlanModel(plan, planDao, log));
        }
    }

    private void waitDatabaseReady() {
        try {
            while(!planDao.exists()) {
                sleep(10000);
            }
        } catch (InterruptedException e) {
            log.error("waitDatabaseReady() error");
            e.printStackTrace();
        }
    }
}
