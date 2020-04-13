package jp.co.canon.cks.eec.fs.rssportal.dao;

import jp.co.canon.cks.eec.fs.rssportal.connect.postgresql.PostgresSqlSessionFactory;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserPermissionVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserPermissionDaoImpl implements UserPermissionDao {

    private final SqlSessionFactory sessionFactory;

    @Autowired
    public UserPermissionDaoImpl(PostgresSqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory.getSqlSessionFactory();
    }

    @Override
    public List<UserPermissionVo> getPermissions() {
        SqlSession session = sessionFactory.openSession();
        List<UserPermissionVo> permissions = session.selectList("userperm.selectAll");
        for(UserPermissionVo perm: permissions) {
            log.info("name="+perm.getPermname());
        }
        return permissions;
    }

    @Override
    public UserPermissionVo getPermission(int id) {
        SqlSession session = sessionFactory.openSession();
        UserPermissionVo perm = session.selectOne("userperm.select", id);
        return perm;
    }

    @Override
    public void setPermission(@NonNull String permname) {
        SqlSession session = sessionFactory.openSession();
        session.insert("userperm.insert", permname);
        session.commit();
    }

    @Override
    public void updatePermission(@NonNull UserPermissionVo perm) {
        SqlSession session = sessionFactory.openSession();
        session.update("userperm.update", perm);
        session.commit();
    }

    private final Log log = LogFactory.getLog(getClass());
}
