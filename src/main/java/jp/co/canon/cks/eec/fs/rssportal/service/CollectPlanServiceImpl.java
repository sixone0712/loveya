package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.dao.CollectionPlanDao;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.PlanStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CollectPlanServiceImpl implements CollectPlanService {

    private static final int COLLECTTYPE_CYCLE = 1;
    private static final int COLLECTTYPE_CONTINUOUS = 2;
    //private static final int CONTINUOUS_DEFAULT_INTERVAL = 10*60*1000; // 10 minutes
    private static final int CONTINUOUS_DEFAULT_INTERVAL = 60*1000; // 1 minute

    private final HttpSession session;
    private final CollectionPlanDao dao;

    private List<Runnable> notifiers;

    @Autowired
    public CollectPlanServiceImpl(HttpSession session, CollectionPlanDao dao) {
        this.session = session;
        this.dao = dao;
        notifiers = new ArrayList<>();
    }

    @Override
    public int addPlan(@NonNull String planName,
                       @NonNull List<String> fabs,
                       @NonNull List<String> tools,
                       @NonNull List<String> logTypes,
                       @NonNull List<String> logTypeStr,
                       @NonNull Date collectStart,
                       @NonNull Date start,
                       @NonNull Date end,
                       @NonNull String collectType,
                       @NonNull long interval,
                       @Nullable String description) {

        SessionContext context = null;
        if(session!=null) {
            context = (SessionContext) session.getAttribute("context");
        }

        int colType = toCollectTypeInteger(collectType);
        if (colType<0) {
            log.error("invalid collectionType "+collectType);
            return -1;
        }

        CollectPlanVo plan = new CollectPlanVo();
        plan.setPlanName(planName);
        plan.setFab(toSingleString(fabs));
        plan.setTool(toSingleString(tools));
        plan.setLogType(toSingleString(logTypes));
        plan.setLogTypeStr(toSingleString(logTypeStr));
        plan.setCollectionType(colType);
        plan.setInterval(interval);
        if(start.after(end)) {
            log.error("start must be before end (start="+start.toString()+" end="+end.toString());
            return -1;
        }
        plan.setCollectStart(new Timestamp(collectStart.getTime()));
        plan.setStart(new Timestamp(start.getTime()));
        plan.setEnd(new Timestamp(end.getTime()));
        plan.setNextAction(new Timestamp(start.getTime()));
        plan.setLastPoint(new Timestamp(start.getTime()-(3600*1000)));
        plan.setLastStatus(PlanStatus.registered.name());

        if(description!=null) {
            plan.setDescription(description);
        }
        if(context==null) {
            log.warn("sessionContext is null");
            plan.setOwner(0);
        } else {
            plan.setOwner(context.getUser().getId());
        }
        int planId = dao.addPlan(plan);
        notifyChanges();
        return planId;
    }

    private int toCollectTypeInteger(@NonNull String collectType) {
        switch (collectType) {
            case "cycle":
                return COLLECTTYPE_CYCLE;
            case "continuous":
                return COLLECTTYPE_CONTINUOUS;
            default:
                return -1;
        }
    }

    @Override
    @Deprecated
    public boolean modifyPlan(int planId,
                              List<String> fabs,
                              List<String> tools,
                              List<String> logTypes,
                              List<String> logTypeStr,
                              Date collectStart,
                              Date start,
                              Date end,
                              String collectType,
                              long interval,
                              String description) {

        CollectPlanVo plan = getPlan(planId);
        if(plan==null) {
            log.error("invalid planId="+planId);
            return false;
        }
        if(tools!=null)
            plan.setTool(toSingleString(tools));
        if(logTypes!=null)
            plan.setLogType(toSingleString(logTypes));
        if(collectStart!=null)
            plan.setCollectStart(new Timestamp(collectStart.getTime()));
        if(start!=null)
            plan.setStart(new Timestamp(start.getTime()));
        if(end!=null)
            plan.setEnd(new Timestamp(end.getTime()));
        if(collectType!=null) {
            int iCollectType = toCollectTypeInteger(collectType);
            if(iCollectType<0) {
                log.error("invalid collectionType "+collectType);
                return false;
            }
            plan.setCollectionType(iCollectType);
        }
        if(interval!=plan.getInterval())
            plan.setInterval(interval);
        if(description!=null)
            plan.setDescription(description);

        return false;
    }

    @Override
    public boolean deletePlan(int planId) {
        CollectPlanVo plan = dao.find(planId);
        if(plan==null) {
            log.error("invalid planId "+planId);
            return false;
        }
        dao.deletePlan(planId);
        return true;
    }

    @Override
    public boolean deletePlan(CollectPlanVo plan) {
        return false;
    }

    @Override
    public List<CollectPlanVo> getAllPlans() {
        return dao.findAll();
    }


    @Override
    public List<CollectPlanVo> getAllPlansBySchedulePriority() {
        return dao.findAll(true, 0);
    }

    @Override
    public CollectPlanVo getPlan(int id) {
        return dao.find(id);
    }

    @Override
    public CollectPlanVo getNextPlan() {
        List<CollectPlanVo> list = dao.findAll(true, 1);
        if(list==null || list.size()==0) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public void scheduleAllPlans() {
        log.info("scheduling all plans");

        List<CollectPlanVo> plans = getAllPlans();
        plans.forEach(plan->{
            if(!plan.isStop()) {
                if (!plan.getDetail().equals(PlanStatus.completed.name())) {
                    long intv = plan.getCollectionType() == COLLECTTYPE_CYCLE ?
                            plan.getInterval() :
                            CONTINUOUS_DEFAULT_INTERVAL;

                    long cur = System.currentTimeMillis();
                    Date next = new Date(cur + intv);
                    if (next.after(plan.getEnd())) {
                        log.info("plan " + plan.getPlanName() + " has been completed");
                        plan.setLastStatus(PlanStatus.completed.name());
                    } else {
                        plan.setNextAction(new Timestamp(next.getTime()));
                    }
                    dao.updatePlan(plan);
                }
            }
        });
        notifyChanges();
    }

    @Override
    public void schedulePlan(@NonNull CollectPlanVo plan) {

        long cur = System.currentTimeMillis();
        long endTime = plan.getEnd().getTime();

        if(plan.isStop())
            return;

        if(endTime<=cur) {
            plan.setNextAction(null);
            log.info("plan "+plan.getPlanName()+" has been completed");
            plan.setLastStatus(PlanStatus.completed.name());
        } else {
            Date last = plan.getLastCollect();
            long interval = plan.getCollectionType()==COLLECTTYPE_CYCLE ?plan.getInterval():CONTINUOUS_DEFAULT_INTERVAL;
            long nextTime;
            if (last == null)
                nextTime = plan.getStart().getTime() + interval;
            else
                nextTime = last.getTime() + interval;
            plan.setNextAction(new Timestamp(nextTime));
        }
        dao.updatePlan(plan);
        notifyChanges();
    }


    @Override
    public boolean stopPlan(int planId) {
        CollectPlanVo plan = getPlan(planId);
        if(plan==null) {
            log.error("invalid planId "+planId);
            return false;
        }
        if(!plan.isStop()) {
            plan.setStop(true);
            plan.setNextAction(null);
            dao.updatePlan(plan);
            notifyChanges();
        }
        return true;
    }

    @Override
    public boolean restartPlan(int planId) {
        CollectPlanVo plan = getPlan(planId);
        if(plan==null) {
            log.error("invalid planId "+planId);
            return false;
        }
        if(plan.isStop()) {
            plan.setStop(false);
            schedulePlan(plan);
        }
        return true;
    }

    @Override
    public void updateLastCollect(@NonNull CollectPlanVo plan) {
        log.info("updateLastCollect: planId="+plan.getId()+" lastCollect="+plan.getLastCollect());
        plan.setLastCollect(new Timestamp(System.currentTimeMillis()));
        dao.updatePlan(plan);
    }

    @Override
    public void setLastStatus(int planId, PlanStatus status) {
        CollectPlanVo plan = getPlan(planId);
        if(plan==null) {
            log.error("setLastStatus invalid planId "+planId);
            return;
        }
        setLastStatus(plan, status);
    }

    @Override
    public void setLastStatus(@NonNull CollectPlanVo plan, PlanStatus status) {
        plan.setLastStatus(status.name());
        if(dao.updateStatus(plan))
            log.info("update statue (plan="+plan.getPlanName()+" status="+status.name()+")");
        else
            log.info("update status failed (plan="+plan.getPlanName()+" status="+status.name()+")");
    }

    @Override
    public void addNotifier(@NonNull Runnable notifier) {
        notifiers.add(notifier);
    }

    private void notifyChanges() {
        for(Runnable notifier: notifiers) {
            notifier.run();
        }
    }

    private String toSingleString(@NonNull List<String> list) {
        StringBuilder sb = new StringBuilder("");
        boolean comma = false;
        for(String item: list) {
            if(comma==false) {
                comma = true;
            } else {
                sb.append(",");
            }
            sb.append(item);
        }
        return sb.toString();
    }

    private final Log log = LogFactory.getLog(getClass());
}
