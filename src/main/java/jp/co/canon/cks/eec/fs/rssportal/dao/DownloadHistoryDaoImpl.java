package jp.co.canon.cks.eec.fs.rssportal.dao;

import jp.co.canon.cks.eec.fs.rssportal.connect.postgresql.PostgresSqlSessionFactory;
import jp.co.canon.cks.eec.fs.rssportal.vo.DownloadHistoryVo;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DownloadHistoryDaoImpl implements DownloadHistoryDao {

    private final SqlSessionFactory sessionFactory;

    @Autowired
    public DownloadHistoryDaoImpl(PostgresSqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory.getSqlSessionFactory();
    }

    @Override
    public List<DownloadHistoryVo> findAll() {
        SqlSession session = sessionFactory.openSession();
        List list = session.selectList("dlHistory.selectAll");
        session.close();
        return list;
    }

/*
    @Override
    public DownloadHistoryVo find(@NonNull Map<String, Object> param) {
        if(!param.containsKey("id")) {
            return null;
        }
        SqlSession session = sessionFactory.openSession();
        DownloadHistoryVo history = session.selectOne("dlHistory.select", param);
        session.close();
        return history;
    }
*/

    @Override
    public boolean add(@NonNull DownloadHistoryVo history) {
        SqlSession session = sessionFactory.openSession(true);
        session.insert("dlHistory.insert", history);
        session.close();
        return true;
    }

/*    @Override
    public boolean modify(@NonNull DownloadHistoryVo history) {
        SqlSession session = sessionFactory.openSession(true);
        session.update("dlHistory.update", history);
        session.close();
        return true;
    }

    @Override
    public boolean delete(@NonNull DownloadHistoryVo history) {
        SqlSession session = sessionFactory.openSession(true);
        session.delete("dlHistory.delete", history);
        session.close();
        return true;
    }*/

}
