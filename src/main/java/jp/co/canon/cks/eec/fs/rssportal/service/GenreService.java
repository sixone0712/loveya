package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.vo.GenreVo;

import java.util.Date;
import java.util.List;

public interface GenreService {

    List<GenreVo> getGenreList();
    GenreVo getGenreById(int id);
    GenreVo getGenreByName(String name);
    boolean addGenre(GenreVo genre);
    boolean modifyGenre(GenreVo genre);
    boolean deleteGenre(int id);
    Date getGenreUpdate();
    boolean setGenreUpdate();
    boolean addGenreUpdate();
}
