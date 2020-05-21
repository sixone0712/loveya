package jp.co.canon.cks.eec.fs.rssportal.dao;

import jp.co.canon.cks.eec.fs.rssportal.vo.DownloadHistoryVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;

import java.util.List;
import java.util.Map;

public interface DownloadHistoryDao {

    List<DownloadHistoryVo> findAll();
    DownloadHistoryVo find( Map<String, Object> param);
    boolean add(DownloadHistoryVo history) ;
    public boolean modify(DownloadHistoryVo history);
    boolean delete(DownloadHistoryVo history);
}
