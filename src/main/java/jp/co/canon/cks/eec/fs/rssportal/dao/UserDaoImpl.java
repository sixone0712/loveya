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
        List list = session.selectList("users.selectAll");
        session.close();
        return list;
    }

    @Override
    public UserVo find(@NonNull Map<String, Object> param) {
        if(param.containsKey("id")==false && param.containsKey("username")==false) {
            return null;
        }
        SqlSession session = sessionFactory.openSession();
        UserVo user = session.selectOne("users.select", param);
        session.close();
        return user;
    }

    @Override
    public boolean add(@NonNull UserVo user) {
        SqlSession session = sessionFactory.openSession(true);
        session.insert("users.insert", user);
        session.close();
        return true;
    }

    @Override
    public boolean modify(@NonNull UserVo user) {
        SqlSession session = sessionFactory.openSession(true);
        session.update("users.update", user);
        session.close();
        return true;
    }

    @Override
    public boolean delete(@NonNull UserVo user) {
        SqlSession session = sessionFactory.openSession(true);
        session.delete("users.delete", user);
        session.close();
        return true;
    }


    @Override
    public boolean UpdateAccessDate(@NonNull Map<String, Object> param) {
        if(!param.containsKey("id")) {
            return false;
        }
        SqlSession session = sessionFactory.openSession();
        session.update("users.updateAccessDate", param);
        session.close();
        return true;
    }

    @Override
    public boolean updateRefreshToken(@NonNull Map<String, Object> param) {
        if(!param.containsKey("id")) {
            return false;
        }

        SqlSession session = sessionFactory.openSession();
        session.update("users.updateRefreshToken", param);
        session.close();
        return true;
    }

    @Override
    public boolean getToken(@NonNull String token) {
        SqlSession session = sessionFactory.openSession();
        int savedToken = session.selectOne("users.selectToken", token);
        session.close();
        return savedToken > 0;
    }

    @Override
    public boolean setToken(@NonNull String token) {
        SqlSession session = sessionFactory.openSession();
        session.insert("users.insertToken", token);
        session.close();
        return true;
    }

    @Override
    public boolean cleanBlacklist() {
        SqlSession session = sessionFactory.openSession();
        int result = session.delete("users.truncateBlacklist");

        if (result > 0) {
            session.commit();
        } else {
            session.rollback();
        }

        session.close();
        return true;
    }
}
