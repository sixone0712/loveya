package jp.co.canon.cks.eec.fs.rssportal.downloadlist;

import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.springframework.lang.NonNull;

import java.util.List;

public interface DownloadListService {

    DownloadListVo get(int id);
    List<DownloadListVo> getList();
    List<DownloadListVo> getList(int offset, int limit);
    boolean insert(DownloadListVo item);
    boolean insert(CollectPlanVo plan, String filePath);
    boolean updateDownloadStatus(int id);
}
