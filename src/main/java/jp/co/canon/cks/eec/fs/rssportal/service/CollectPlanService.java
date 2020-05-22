package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.PlanStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

public interface CollectPlanService {

    int addPlan(String planName,
                List<String> tools,
                List<String> logTypes,
                List<String> logTypeStr,
                Date collectStart,
                Date start,
                Date end,
                String collectType,
                long interval,
                String description);

    @Deprecated
    boolean modifyPlan(int planId,
                       List<String> tools,
                       List<String> logTypes,
                       List<String> logTypeStr,
                       Date collectStart,
                       Date start,
                       Date end,
                       String collectType,
                       long interval,
                       String description);

    boolean deletePlan(int planId);
    boolean deletePlan(CollectPlanVo plan);
    List<CollectPlanVo> getAllPlans();
    List<CollectPlanVo> getAllPlansBySchedulePriority();
    CollectPlanVo getPlan(int id);
    CollectPlanVo getNextPlan();
    void scheduleAllPlans();

    /**
     * Schedule the specified plan and update next action time for the plan.
     * @param plan
     */
    void schedulePlan(CollectPlanVo plan);

    boolean stopPlan(int planId);

    boolean restartPlan(int planId);

    /**
     * Update the last date that the collecting has been done.
     * @param plan
     */
    void updateLastCollect(CollectPlanVo plan);

    /**
     * Add a notifier that is called when changes occur.
     * @param notifier
     */
    void addNotifier(Runnable notifier);

    /**
     * Set the last operation status
     * @param planId
     * @param status
     */
    void setLastStatus(int planId, PlanStatus status);
    void setLastStatus(CollectPlanVo plan, PlanStatus status);
}
