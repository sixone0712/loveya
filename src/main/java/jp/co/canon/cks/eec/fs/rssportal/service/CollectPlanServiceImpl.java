package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.dao.CollectionPlanDao;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
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
    private static final int CONTINUOUS_DEFAULT_INTERVAL = 10*60*1000; // 10 minutes

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
    public boolean addPlan(@NonNull List<String> tools,
                           @NonNull List<String> logTypes,
                           @NonNull Date start,
                           @NonNull Date end,
                           @NonNull String collectType,
                           @NonNull long interval,
                           @Nullable String description) {

        SessionContext context = null;
        if(session!=null) {
            context = (SessionContext) session.getAttribute("context");
        }

        int colType;
        switch (collectType) {
            case "cycle":
                colType = COLLECTTYPE_CYCLE;
                break;
            case "continuous":
                colType = COLLECTTYPE_CONTINUOUS;
                break;
            default:
                log.error("invalid collectionType "+collectType);
                return false;
        }

        CollectPlanVo plan = new CollectPlanVo();
        plan.setTool(toSingleString(tools));
        plan.setLogType(toSingleString(logTypes));
        plan.setCollectionType(colType);
        plan.setInterval(interval);
        if(start.after(end)) {
            log.error("start must be before end (start="+start.toString()+" end="+end.toString());
            return false;
        }
        plan.setStart(new Timestamp(start.getTime()));
        plan.setEnd(new Timestamp(end.getTime()));
        plan.setNextAction(new Timestamp(start.getTime()));

        if(description!=null) {
            plan.setDescription(description);
        }
        if(context==null) {
            log.warn("sessionContext is null");
            plan.setOwner(0);
        } else {
            plan.setOwner(context.getUser().getId());
        }

        boolean ret = dao.addPlan(plan);
        if(ret==true) {
            notifyChanges();
        }
        return ret;
    }

    @Override
    public boolean deletePlan(int planId) {
        return false;
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
            if(plan.isExpired()==false) {
                long intv = plan.getCollectionType() == COLLECTTYPE_CYCLE ?
                        plan.getInterval() :
                        CONTINUOUS_DEFAULT_INTERVAL;

                long cur = System.currentTimeMillis();
                Date next = new Date(cur+intv);
                if(next.after(plan.getEnd())) {
                    log.info("planId "+plan.getId()+" was expired");
                    plan.setExpired(true);
                } else {
                    plan.setNextAction(new Timestamp(next.getTime()));
                }
                dao.updatePlan(plan);
            }
        });
        notifyChanges();
    }

    @Override
    public void schedulePlan(@NonNull CollectPlanVo plan) {

        long cur = System.currentTimeMillis();
        long endTime = plan.getEnd().getTime();

        if(endTime<=cur) {
            plan.setNextAction(null);
            plan.setExpired(true);
        } else {
            Date last = plan.getLastCollect();
            long nextTime;
            if (last == null) {
                nextTime = plan.getStart().getTime() + plan.getInterval();
            } else {
                nextTime = last.getTime() + plan.getInterval();
            }
            plan.setNextAction(new Timestamp(nextTime));
        }
        dao.updatePlan(plan);
        notifyChanges();
    }

    @Override
    public void updateLastCollect(@NonNull CollectPlanVo plan) {
        plan.setLastCollect(new Timestamp(System.currentTimeMillis()));
        log.info("updateLastCollect: planId="+plan.getId()+" lastCollect="+plan.getLastCollect().toString());
        dao.updatePlan(plan);
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
