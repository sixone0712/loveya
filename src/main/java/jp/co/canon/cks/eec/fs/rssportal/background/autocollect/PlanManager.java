package jp.co.canon.cks.eec.fs.rssportal.background.autocollect;

import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import jp.co.canon.cks.eec.fs.rssportal.dao.CollectionPlanDao;
import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListService;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.PlanStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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
    private boolean inited;
    private boolean halted;

    public PlanManager() {
        inited = false;
        halted = false;
    }

    @PostConstruct
    public void postConstruct() {
        this.start();
    }

    @Override
    public void run() {
        log.info("PlanManager start");
        waitDatabaseReady();
        initPlanProcess();

        try {
            while (true) {
                killThread();

                if(halted) {
                    log.warn("PlanManager halted");
                    sleep(30000);
                    continue;
                }
                sleep(5000);

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
            if(iSchedule!=null && iSchedule.before(nextTime)) {
                nextIdx = i;
                nextTime = iSchedule;
            }
        }
        return nextIdx;
    }

    private void initPlanProcess() {
        log.info("initialize all plans");
        collects = new ArrayList<>();
        killList = new ArrayList<>();
        List<CollectPlanVo> plans = planDao.findAll();
        for(CollectPlanVo plan: plans) {
            createCollectProcess(plan);
            log.info(plan.toString());
        }
        inited = true;
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
        return planRootDir;
    }

    public boolean isInitialized() {
        return inited;
    }

    public int addPlan(CollectPlanVo plan) {
        int planId = planDao.addPlan(plan);
        CollectPlanVo added = planDao.find(planId);
        createCollectProcess(added);
        return planId;
    }

    private void createCollectProcess(CollectPlanVo plan) {
        CollectProcess p;
        switch (plan.getPlanType()) {
            case "ftp":
                p = new FtpCollectProcess(this, plan, planDao, downloader, log);
                break;
            case "vftp_compat":
                p = new VFtpCompatCollectProcess(this, plan, planDao, downloader, log);
                break;
            case "vftp_sss":
                p = new VFtpSssCollectProcess(this, plan, planDao, downloader, log);
                break;
            default:
                log.error("createCollectProcess: undefined plan type "+plan.getPlanType());
                return;
        }
        collects.add(p);
    }

    public List<CollectPlanVo> getPlans() {
        List<CollectPlanVo> list = new ArrayList<>();
        for(CollectProcess process: collects) {
            CollectPlanVo resp = process.getPlan().createCollectPlanResponse();
            resp.setStop(process.isStop());
            list.add(resp);
        }
        return list;
    }

    public List<CollectPlanVo> getPlans(int userId) {
        List<CollectPlanVo> myList = new ArrayList<>();
        List<CollectPlanVo> otherList = new ArrayList<>();
        for(CollectProcess process: collects) {
            if(process.getPlan().getOwner()==userId)
                myList.add(process.getPlan());
            else
                otherList.add(process.getPlan());
        }
        //Collections.sort(myList);
        //Collections.sort(otherList);
        myList = orderPlansByStatus(myList);
        otherList = orderPlansByStatus(otherList);
        List<CollectPlanVo> list = new ArrayList<>();
        list.addAll(myList);
        list.addAll(otherList);
        return list;
    }

    private List<CollectPlanVo> orderPlansByStatus(List<CollectPlanVo> list) {
        List<CollectPlanVo> completes = new ArrayList<>();
        List<CollectPlanVo> others = new ArrayList<>();

        for(CollectPlanVo plan: list) {
            if(plan.getLastStatus().equalsIgnoreCase("completed")) {
                completes.add(plan);
            } else {
                others.add(plan);
            }
        }

        Collections.sort(completes);
        Collections.sort(others);
        list.clear();
        list.addAll(others);
        list.addAll(completes);
        return list;
    }

    public CollectPlanVo getPlan(int planId) {
        CollectProcess process = getPlanProcess(planId);
        return process==null?null:process.getPlan();
    }

    public boolean stopPlan(int planId) {
        CollectProcess process = getPlanProcess(planId);
        if(process==null) {
            log.warn("stopPlan: invalid request. planid="+planId);
            return false;
        }
        process.stop();
        log.info("request stop "+process.getPlan().getId());
        return true;
    }

    public boolean restartPlan(int planId) {
        CollectProcess process = getPlanProcess(planId);
        if(process==null) {
            log.warn("restartPlan: invalid request. planid="+planId);
            return false;
        }
        if(process.isStop()) {
            process.setStop(false);
        }
        log.info("restartPlan "+process.getPlan().getId());
        return true;
    }

    public boolean modifyPlan(CollectPlanVo plan) {
        CollectProcess process = getPlanProcess(plan.getId());
        if(process==null) {
            log.error("modifyPlan: invalid plan "+plan.getId());
            return false;
        }
        if(process.getPlan().getDetail().equalsIgnoreCase(PlanStatus.collecting.name())) {
            log.error("modifyPlan: failed to modify operating plan");
            return false;
        }
        return process.modifyPlan(plan);
    }

    public boolean deletePlan(int planId) {
        CollectProcess process = getPlanProcess(planId);
        if(process==null) {
            log.error("deletePlan: invalid plan "+process.getPlan().getId());
            return false;
        }
        boolean result = process.deletePlan();
        if(result) {
            collects.remove(process);
            log.info("deletePlan: CollectProcess deleted "+planId);
        }
        return result;
    }

    public void setHalted(boolean halted) {
        log.info("setHalted "+halted);
        this.halted = halted;
    }

    private CollectProcess getPlanProcess(int planId) {
        for(CollectProcess process: collects) {
            if(process.getPlan().getId()==planId) {
                return process;
            }
        }
        return null;
    }



}
