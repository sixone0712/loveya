package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.rssportal.dao.CollectionPlanDao;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.PlanStatus;
import org.apache.commons.logging.Log;

import java.sql.Timestamp;

public class CollectPlanModel {

    private CollectPlanVo plan;

    private final CollectionPlanDao dao;
    private final Log log;
    private final String planName;
    private Timestamp jobStartTime;
    private Timestamp syncTime;
    private boolean stop;

    public CollectPlanModel(CollectPlanVo plan, CollectionPlanDao dao, Log log) {
        this.plan = plan;
        this.dao = dao;
        this.log = log;
        this.planName = plan.getPlanName();
        this.stop = plan.isStop();
        updateSyncTimestamp();
        schedule();
    }

    public void pull() {
        if(!isChangeable()) {
            printError("pull failed in collecting");
            return;
        }
        plan = dao.find(plan.getId());
        updateSyncTimestamp();
    }

    public void push() {
        if(!isChangeable()) {
            printError("push failed in collecting");
            return;
        }
        dao.updatePlan(plan);
        updateSyncTimestamp();
    }

    public void schedule() {
        if(!isChangeable()) {
            printError("failed to schedule the plan");
            return;
        }

        Timestamp planning;
        if(stop || isExpired()) {
            planning = null;
        } else {
            Timestamp lastCollectedTime = plan.getLastCollect();
            if(lastCollectedTime==null) {
                // In this case, collect it asap.
                planning = new Timestamp(System.currentTimeMillis());
            }
            long interval;
            if(plan.getCollectionType()==1 /*COLLECTTYPE_CYCLE*/) {
                interval = plan.getInterval();
            } else {
                interval = 60000; /*CONTINUOUS_DEFAULT_INTERVAL*/
            }
            planning = new Timestamp(lastCollectedTime.getTime()+interval);
        }
        if(planning!=null) {
            if(planning.after(new Timestamp(System.currentTimeMillis()))) {
                planning = plan.getEnd();
            }
        }
        plan.setNextAction(planning);
        printInfo(toString());
        //push();
    }

    private boolean isChangeable() {
        if(plan.getDetail().equalsIgnoreCase(PlanStatus.collecting.name())) {
            return false;
        }
        return true;
    }

    private boolean isExpired() {
        if(plan.getDetail().equalsIgnoreCase(PlanStatus.completed.name()))
            return true;
        return false;
    }

    private void updateSyncTimestamp() {
        syncTime = new Timestamp(System.currentTimeMillis());
    }

    private void updateJobStartTimestamp() {
        jobStartTime = new Timestamp(System.currentTimeMillis());
    }

    private void printInfo(String str) {
        if(log!=null) {
            String formed = String.format("[%s] %s", planName, str);
            log.info(formed);
        }
    }

    private void printError(String str) {
        if(log!=null) {
            String formed = String.format("[%s] %s", planName, str);
            log.error(formed);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("plan::");
        sb.append(plan.getPlanName()).append(":");
        sb.append(plan.getDetail()).append(":");
        sb.append(plan.getNextAction());
        return sb.toString();
    }
}
