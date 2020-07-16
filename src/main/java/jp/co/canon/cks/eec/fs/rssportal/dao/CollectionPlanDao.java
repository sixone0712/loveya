package jp.co.canon.cks.eec.fs.rssportal.dao;

import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CollectionPlanDao {

    boolean exists();
    List<CollectPlanVo> findAll();
    List<CollectPlanVo> findAll(boolean ordering, int limit);
    CollectPlanVo find(int id);
    int addPlan(CollectPlanVo plan);
    boolean updatePlan(CollectPlanVo plan);
    boolean deletePlan(int id);
    void update(CollectPlanVo plan);
    boolean updateStatus(CollectPlanVo plan);
    void updateStop(int id, boolean value);
}
