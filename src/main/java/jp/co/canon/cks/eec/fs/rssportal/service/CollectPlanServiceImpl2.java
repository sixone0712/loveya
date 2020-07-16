package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.background.PlanManager;
import jp.co.canon.cks.eec.fs.rssportal.common.Tool;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.PlanStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class CollectPlanServiceImpl2 implements CollectPlanService {

    private final Log log = LogFactory.getLog(getClass());
    private final PlanManager manager;

    @Autowired
    public CollectPlanServiceImpl2(PlanManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean isReady() {
        return manager.isInitialized();
    }

    @Override
    public int addPlan(int userId, String planName, List<String> fabs, List<String> tools, List<String> logTypes, List<String> logTypeStr, Date collectStart, Date start, Date end, String collectType, long interval, String description) {
        CollectPlanVo plan = createPlanObject(userId, planName, fabs, tools, logTypes, logTypeStr, collectStart, start, end, collectType, interval, description);
        return plan==null?-1:manager.addPlan(plan);
    }



    @Override
    public boolean deletePlan(int planId) {
        return manager.deletePlan(planId);
    }

    @Override
    public boolean deletePlan(CollectPlanVo plan) {
        return false;
    }

    @Override
    public List<CollectPlanVo> getAllPlans() {
        return manager.getPlans();
    }

    @Override
    public List<CollectPlanVo> getAllPlansBySchedulePriority() {
        return null;
    }

    @Override
    public CollectPlanVo getPlan(int id) {
        return manager.getPlan(id);
    }

    @Override
    public CollectPlanVo getNextPlan() {
        // not support in this service.
        return null;
    }

    @Override
    public void scheduleAllPlans() {
        // not support
    }

    @Override
    public void schedulePlan(CollectPlanVo plan) {
        // not support
    }

    @Override
    public boolean stopPlan(int planId) {
        return manager.stopPlan(planId);
    }

    @Override
    public boolean restartPlan(int planId) {
        return manager.restartPlan(planId);
    }

    @Override
    public void updateLastCollect(CollectPlanVo plan) {
        // not support
    }

    @Override
    public void addNotifier(Runnable notifier) {
        // not support
    }

    @Override
    public void setLastStatus(int planId, PlanStatus status) {
        // not support
    }

    @Override
    public void setLastStatus(CollectPlanVo plan, PlanStatus status) {
        // not support
    }

    @Override
    public int modifyPlan(int planId, int userId, String planName, List<String> fabs, List<String> tools, List<String> logTypes, List<String> logTypeStr, Date collectStart, Date start, Date end, String collectType, long interval, String description) {
        CollectPlanVo plan = createPlanObject(userId, planName, fabs, tools, logTypes, logTypeStr, collectStart, start, end, collectType, interval, description);
        plan.setId(planId);
        return manager.modifyPlan(plan)?plan.getId():-1;
    }

    private CollectPlanVo createPlanObject(int userId, String planName, List<String> fabs, List<String> tools, List<String> logTypes, List<String> logTypeStr, Date collectStart, Date start, Date end, String collectType, long interval, String description) {
        int collectTypeInt = Tool.getCollectTypeNumber(collectType);

        if(collectTypeInt<0 || start.after(end)) {
            log.error("invalid input");
            return null;
        }

        CollectPlanVo plan = new CollectPlanVo();
        plan.setPlanName(planName);
        plan.setFab(Tool.toCSVString(fabs));
        plan.setTool(Tool.toCSVString(tools));
        plan.setLogType(Tool.toCSVString(logTypes));
        plan.setLogTypeStr(Tool.toCSVString(logTypeStr));
        plan.setCollectionType(collectTypeInt);
        plan.setInterval(interval);
        plan.setCollectStart(new Timestamp(collectStart.getTime()));
        plan.setStart(new Timestamp(start.getTime()));
        plan.setEnd(new Timestamp(end.getTime()));
        plan.setNextAction(new Timestamp(collectStart.getTime()));
        plan.setLastPoint(new Timestamp(start.getTime()));
        plan.setLastStatus(PlanStatus.registered.name());
        if(description!=null) {
            plan.setDescription(description);
        }
        plan.setOwner(userId);
        return plan;
    }
}
