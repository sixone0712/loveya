package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.dao.GenreDao;
import jp.co.canon.cks.eec.fs.rssportal.vo.GenreVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreDao dao;
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    public GenreServiceImpl(GenreDao dao) {
        this.dao = dao;
    }

    @Override
    public List<GenreVo> getGenreList() {
        return dao.findAll();
    }

    @Override
    public GenreVo getGenreById(int id) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        return dao.findById(param);
    }

    @Override
    public GenreVo getGenreByName(@NonNull String name) {
        Map<String, Object> param = new HashMap<>();
        param.put("name", name);
        return dao.findByName(param);
    }

    @Override
    public boolean addGenre(@NonNull GenreVo genre) {
        if(genre.getName().isEmpty() || genre.getCategory().isEmpty()) {
            return false;
        }
        return dao.add(genre);
    }

    @Override
    public boolean modifyGenre(@NonNull GenreVo genre) {
        GenreVo temp = getGenreById(genre.getId());
        if(temp==null) {
            return false;
        }
        return dao.modify(genre);
    }

    @Override
    public boolean deleteGenre(int id) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        return dao.delete(param);
    }

    @Override
    public Date getGenreUpdate() {
        return dao.findUpdate();
    }

    @Override
    public boolean setGenreUpdate() {
        return dao.modifyUpdate();
    }
}
