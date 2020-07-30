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
    public int addPlan(String planType, int userId, String planName, List<String> fabs, List<String> tools, List<String> logTypes,
                       List<String> logTypeStr, Date collectStart, Date start, Date end, String collectType,
                       long interval, String description) {

        planType = planType.toLowerCase();
        if(!planType.equals("ftp")) {
            log.error("invalid type in this method "+planType);
            return -1;
        }
        CollectPlanVo plan =
                createFtpPlanObject(userId, planName, fabs, tools, logTypes, logTypeStr, collectStart, start, end,
                        collectType, interval, description);
        return plan==null?-1:manager.addPlan(plan);
    }

    @Override
    public int addPlan(String planType, int userId, String planName, List<String> fabs, List<String> tools,
                       List<String> commandsOrDirectories, Date collectStart, Date start, Date end, String collectType, long interval,
                       String description) {
        CollectPlanVo plan;
        planType = planType.toLowerCase();

        switch (planType) {
            case "vftp_compat":
                plan = createVFtpCompatPlanObject(userId, planName, fabs, tools, commandsOrDirectories, collectStart, start, end, collectType, interval, description);
                break;
            case "vftp_sss":
                plan = createVFtpSssPlanObject(userId, planName, fabs, tools, commandsOrDirectories, collectStart, start, end, collectType, interval, description);
                break;
            default:
                log.error("invalid type in this method "+planType);
                return -1;
        }
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
    public int modifyPlan(int planId, String planType, int userId, String planName, List<String> fabs, List<String> tools,
                          List<String> logTypes, List<String> logTypeStr, Date collectStart, Date start, Date end,
                          String collectType, long interval, String description) {

        CollectPlanVo plan = createFtpPlanObject(userId, planName, fabs, tools, logTypes, logTypeStr, collectStart, start, end, collectType, interval, description);
        plan.setId(planId);
        return manager.modifyPlan(plan)?plan.getId():-1;
    }

    @Override
    public int modifyPlan(int planId, String planType, int userId, String planName, List<String> fabs, List<String> tools, List<String> commandsOrDirectories, Date collectStart, Date start, Date end, String collectType, long interval, String description) {

        CollectPlanVo plan;
        if(planType.equalsIgnoreCase("vftp-compat"))
            plan = createVFtpCompatPlanObject(userId, planName, fabs, tools, commandsOrDirectories, collectStart, start, end, collectType, interval, description);
        else
            plan = createVFtpSssPlanObject(userId, planName, fabs, tools, commandsOrDirectories, collectStart, start, end, collectType, interval, description);

        plan.setId(planId);
        return manager.modifyPlan(plan)?plan.getId():-1;
    }

    private CollectPlanVo createFtpPlanObject(int userId, String planName, List<String> fabs, List<String> tools,
                                              List<String> logTypes, List<String> logTypeStr, Date collectStart,
                                              Date start, Date end, String collectType, long interval, String description) {

        CollectPlanVo plan = createCommonPlanObject("ftp", userId, planName, fabs, tools, collectStart, start, end, collectType, interval, description);
        if(plan==null)
            return null;

        plan.setLogType(Tool.toCSVString(logTypes));
        plan.setLogTypeStr(Tool.toCSVString(logTypeStr));
        return plan;
    }

    private CollectPlanVo createVFtpCompatPlanObject(int userId, String planName, List<String> fabs, List<String> tools,
                                                     List<String> commands, Date collectStart, Date start, Date end,
                                                     String collectType, long interval, String description) {

        CollectPlanVo plan = createCommonPlanObject("vftp_compat", userId, planName, fabs, tools, collectStart, start, end, collectType, interval, description);
        if(plan==null)
            return null;

        plan.setCommand(Tool.toCSVString(commands));
        return plan;
    }

    private CollectPlanVo createVFtpSssPlanObject(int userId, String planName, List<String> fabs, List<String> tools,
    List<String> directories, Date collectStart, Date start, Date end,
    String collectType, long interval, String description) {

        CollectPlanVo plan = createCommonPlanObject("vftp_sss", userId, planName, fabs, tools, collectStart, start, end, collectType, interval, description);
        if(plan==null)
            return null;

        plan.setDirectory(Tool.toCSVString(directories));
        return plan;
    }

    private CollectPlanVo createCommonPlanObject(String planType, int userId, String planName, List<String> fabs, List<String> tools, Date collectStart, Date start, Date end,
                                                 String collectType, long interval, String description) {

        CollectPlanVo plan = new CollectPlanVo();

        plan.setPlanType(planType);
        plan.setPlanName(planName);
        plan.setFab(Tool.toCSVString(fabs));
        plan.setTool(Tool.toCSVString(tools));
        plan.setInterval(interval);

        Timestamp collectStartTs = new Timestamp(collectStart.getTime());
        Timestamp startTs = new Timestamp(start.getTime());
        plan.setCollectStart(collectStartTs);
        plan.setStart(startTs);
        plan.setEnd(new Timestamp(end.getTime()));

        plan.setNextAction(collectStartTs);
        plan.setLastPoint(startTs);
        plan.setLastStatus(PlanStatus.registered.name());
        if(description!=null) {
            plan.setDescription(description);
        }
        plan.setOwner(userId);

        int collectTypeCode = Tool.getCollectTypeNumber(collectType);
        if(collectTypeCode<0 || start.after(end)) {
            log.error("invalid input");
            return null;
        }
        plan.setCollectionType(collectTypeCode);
        return plan;
    }
}
