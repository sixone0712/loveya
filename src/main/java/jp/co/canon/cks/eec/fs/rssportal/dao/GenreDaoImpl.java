package jp.co.canon.cks.eec.fs.rssportal.dao;

import jp.co.canon.cks.eec.fs.rssportal.Defines.Genre;
import jp.co.canon.cks.eec.fs.rssportal.connect.postgresql.PostgresSqlSessionFactory;
import jp.co.canon.cks.eec.fs.rssportal.vo.GenreVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class GenreDaoImpl implements GenreDao {

    private final SqlSessionFactory sessionFactory;
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    public GenreDaoImpl(PostgresSqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory.getSqlSessionFactory();
    }

    /*
    public List<GenreVo> parseData(List<Map<String, Object>> data) {
        List<GenreVo> rtn = new ArrayList<GenreVo>();

        for(Map<String, Object> list : data) {
            GenreVo genre = new GenreVo();
            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList((String[])list.get("category")));
            genre.setId((int) list.get("id"));
            genre.setCategory(arrayList);
            genre.setName((String)list.get("name"));
            genre.setValidity(true);
            genre.setModified((Date)list.get("modified"));
            genre.setCreated((Date)list.get("created"));
            rtn.add(genre);
        }
        return rtn;
    }

    @Override
    public List<GenreVo> findAll() {
        SqlSession session = sessionFactory.openSession();
        List<Map<String, Object>> result = session.selectList("genres.selectAll");
        return parseData(result);
    }
    */

    @Override
    public List<GenreVo> findAll() {
        SqlSession session = sessionFactory.openSession();
        List<GenreVo> data = session.selectList("genres.selectAll");
        session.close();
        return data;
    }

    @Override
    public GenreVo findById(@NonNull Map<String, Object> param) {
        if(param.containsKey("id") == false) {
            return null;
        }
        SqlSession session = sessionFactory.openSession();
        GenreVo data =  session.selectOne("genres.selectById", param);
        session.close();
        return data;
    }

    @Override
    public GenreVo findByName(@NonNull Map<String, Object> param) {
        if(param.containsKey("name") == false) {
            return null;
        }
        SqlSession session = sessionFactory.openSession();
        GenreVo data =  session.selectOne("genres.selectByName", param);
        session.close();
        return data;
    }

    @Override
    public boolean add(@NonNull GenreVo genre) {
        SqlSession session = sessionFactory.openSession();
        session.insert("genres.insert", genre);
        session.commit();
        session.close();
        return true;
    }

    @Override
    public boolean modify(@NonNull GenreVo genre) {
        SqlSession session = sessionFactory.openSession();
        session.update("genres.update", genre);
        session.commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(@NonNull Map<String, Object> param) {
        if(param.containsKey("id") == false) {
            return false;
        }
        SqlSession session = sessionFactory.openSession();
        session.delete("genres.delete", param);
        session.close();
        return true;
    }

    @Override
    public Date findUpdate() {
        SqlSession session = sessionFactory.openSession();
        Date data =  session.selectOne("genres.selectUpdate");
        session.close();
        return data;
    }

    @Override
    public boolean modifyUpdate() {
        SqlSession session = sessionFactory.openSession();
        session.update("genres.updateUpdate");
        session.commit();
        session.close();
        return true;
    }

    @Override
    public boolean addUpdate() {
        SqlSession session = sessionFactory.openSession();
        session.insert("genres.insertUpdate");
        session.commit();
        session.close();
        return true;
    }
}
