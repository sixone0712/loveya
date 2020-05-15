package jp.co.canon.cks.eec.fs.rssportal.downloadlist;

import jp.co.canon.cks.eec.fs.rssportal.connect.postgresql.PostgresSqlSessionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DownloadListDaoImpl implements DownloadListDao {

    private final Log log = LogFactory.getLog(getClass());
    private SqlSessionFactory sessionFactory;

    @Autowired
    public DownloadListDaoImpl(PostgresSqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory.getSqlSessionFactory();
    }

    @Override
    public DownloadListVo findItem(int id) {
        SqlSession session = sessionFactory.openSession();
        DownloadListVo item = session.selectOne("downloadList.findItem", id);
        session.close();
        return item;
    }

    @Override
    public List<DownloadListVo> find() {
        SqlSession session = sessionFactory.openSession();
        List<DownloadListVo> list = session.selectList("downloadList.find");
        session.close();
        return list;
    }

    @Override
    public List<DownloadListVo> find(int limit, int page) {
        return null;
    }

    @Override
    public boolean insert(@NonNull DownloadListVo item) {
        SqlSession session = sessionFactory.openSession(true);
        int ret = session.insert("downloadList.insert", item);
        log.info("insert: ret="+ret);
        session.close();
        return true;
    }

    @Override
    public boolean update(@NonNull DownloadListVo item) {
        SqlSession session = sessionFactory.openSession(true);
        int ret = session.update("downloadList.update", item);
        log.info("update: ret="+ret);
        session.close();
        return true;
    }
}
