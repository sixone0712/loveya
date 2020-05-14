package jp.co.canon.cks.eec.fs.rssportal.dao;

import jp.co.canon.cks.eec.fs.rssportal.connect.postgresql.PostgresSqlSessionFactory;
import jp.co.canon.cks.eec.fs.rssportal.vo.CommandVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CommandDaoImpl implements CommandDao {

    private final SqlSessionFactory sessionFactory;
    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    public CommandDaoImpl(PostgresSqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory.getSqlSessionFactory();
    }

    @Override
    public List<CommandVo> findAll() {
        List<CommandVo> list;
        SqlSession session = sessionFactory.openSession();
        list = session.selectList("cmd.selectAll");
        session.close();
        return list;
    }

    @Override
    public List<CommandVo> findCommandList(Map<String, Object> param) {
        if(!param.containsKey("cmd_type")) {
            return null;
        }
        SqlSession session = sessionFactory.openSession();
        List<CommandVo> list = session.selectList("cmd.getList", param);
        session.close();
        return list;
    }

    @Override
    public CommandVo find(Map<String, Object> param) {
        if(!param.containsKey("id")) {
            return null;
        }
        SqlSession session = sessionFactory.openSession();
        CommandVo result = session.selectOne("cmd.selectById", param);
        session.close();
        return result;
    }

    @Override
    public boolean add(@NonNull CommandVo cmd) {
        SqlSession session = sessionFactory.openSession(true);
        session.insert("cmd.insert", cmd);
        session.close();
        return true;
    }

    @Override
    public boolean modify(@NonNull CommandVo cmd) {
        SqlSession session = sessionFactory.openSession(true);
        session.update("cmd.update", cmd);
        session.close();
        return true;
    }

    public boolean delete(@NonNull CommandVo cmd) {
        boolean ret = false;
        SqlSession session = sessionFactory.openSession(true);
        session.delete("cmd.delete", cmd);
        session.close();
        return true;
    }

}
