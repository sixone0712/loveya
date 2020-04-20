package jp.co.canon.cks.eec.fs.rssportal.dao;

import jp.co.canon.cks.eec.fs.rssportal.connect.postgresql.PostgresSqlSessionFactory;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CollectionPlanDaoImpl implements CollectionPlanDao {

    private final SqlSessionFactory sessionFactory;

    @Autowired
    public CollectionPlanDaoImpl(PostgresSqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory.getSqlSessionFactory();
    }

    @Override
    public List<CollectPlanVo> findAll() {
        SqlSession session = sessionFactory.openSession();
        List list = session.selectList("colplan.selectAll");
        session.close();
        return list;
    }

    @Override
    public List<CollectPlanVo> findAll(boolean ordering, int limit) {
        SqlSession session = sessionFactory.openSession();
        Map<String, Object> map = new HashMap<>();
        if(ordering==true) {
            map.put("order", true);
        }
        if(limit>0) {
            map.put("limit", limit);
        }
        List list = session.selectList("colplan.selectAllOptions", map);
        session.close();
        return list;
    }

    @Override
    public boolean addPlan(@NonNull CollectPlanVo plan) {
        SqlSession session = sessionFactory.openSession(true);
        session.insert("colplan.insert", plan);
        session.close();
        return true;
    }

    @Override
    public boolean updatePlan(@NonNull CollectPlanVo plan) {
        SqlSession session = sessionFactory.openSession(true);
        session.update("colplan.update", plan);
        session.close();
        return true;
    }

    @Override
    public boolean deletePlan(CollectPlanVo plan) {
        return false;
    }
}
