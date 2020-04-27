package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

public interface CollectPlanService {

    /**
     * Add a new plan.
     * @param tools
     * @param logTypes
     * @param start
     * @param end
     * @param collectType   "cycle" or "continuous"
     * @param interval      milliseconds. it has to be 60000 over.
     * @param description
     * @return
     */
    int addPlan(@NonNull List<String> tools,
                @NonNull List<String> logTypes,
                @NonNull Date start,
                @NonNull Date end,
                @NonNull String collectType,
                @NonNull long interval,
                @Nullable String description);

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
}