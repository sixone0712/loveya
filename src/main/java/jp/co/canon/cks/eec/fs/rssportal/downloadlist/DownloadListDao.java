package jp.co.canon.cks.eec.fs.rssportal.downloadlist;

import java.util.List;
import java.util.Map;

public interface DownloadListDao {

    boolean exists();
    DownloadListVo findItem(int id);
    List<DownloadListVo> find(int planId);
    List<DownloadListVo> find(int planId, int limit, int page);
    List<DownloadListVo> find(Map<String, Object> condition);
    boolean insert(DownloadListVo item);
    boolean update(DownloadListVo item);
    boolean delete(int id);
}
