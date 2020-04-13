package jp.co.canon.cks.eec.fs.rssportal.dao;

import jp.co.canon.cks.eec.fs.rssportal.connect.postgresql.PostgresSqlSessionFactory;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserDaoImpl implements UserDao {

    private final SqlSessionFactory sessionFactory;

    @Autowired
    public UserDaoImpl(PostgresSqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory.getSqlSessionFactory();
    }

    @Override
    public List<UserVo> findAll() {
        SqlSession session = sessionFactory.openSession();
        return session.selectList("users.selectAll");
    }

    @Override
    public UserVo find(@NonNull Map<String, Object> param) {
        if(param.containsKey("id")==false && param.containsKey("username")==false) {
            return null;
        }
        SqlSession session = sessionFactory.openSession();
        return session.selectOne("users.select", param);
    }

    @Override
    public boolean add(@NonNull UserVo user) {
        SqlSession session = sessionFactory.openSession();
        session.insert("users.insert", user);
        session.commit();
        return true;
    }

    @Override
    public boolean modify(@NonNull UserVo user) {
        SqlSession session = sessionFactory.openSession();
        session.update("users.update", user);
        session.commit();
        return true;
    }
}
