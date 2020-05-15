package jp.co.canon.cks.eec.fs.rssportal.downloadlist;

import java.util.List;

public interface DownloadListDao {

    DownloadListVo findItem(int id);
    List<DownloadListVo> find();
    List<DownloadListVo> find(int limit, int page);
    boolean insert(DownloadListVo item);
    boolean update(DownloadListVo item);
}
