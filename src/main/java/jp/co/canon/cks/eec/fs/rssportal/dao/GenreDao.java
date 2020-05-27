package jp.co.canon.cks.eec.fs.rssportal.dao;

import jp.co.canon.cks.eec.fs.rssportal.vo.GenreVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface GenreDao {

    List<GenreVo> findAll();
    GenreVo findById(Map<String, Object> param);
    GenreVo findByName(Map<String, Object> param);
    boolean add(GenreVo genre);
    boolean modify(GenreVo genre);
    boolean delete(Map<String, Object> param);
    Date findUpdate();
    boolean modifyUpdate();
    boolean addUpdate();
}
