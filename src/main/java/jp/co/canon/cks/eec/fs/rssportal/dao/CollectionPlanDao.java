package jp.co.canon.cks.eec.fs.rssportal.dao;

import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CollectionPlanDao {

    List<CollectPlanVo> findAll();
    List<CollectPlanVo> findAll(boolean ordering, int limit);
    CollectPlanVo find(int id);
    boolean addPlan(CollectPlanVo plan);
    boolean updatePlan(@NonNull CollectPlanVo plan);
    boolean deletePlan(int id);
}
