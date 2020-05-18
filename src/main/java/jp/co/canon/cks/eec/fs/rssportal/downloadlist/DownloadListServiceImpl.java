package jp.co.canon.cks.eec.fs.rssportal.downloadlist;

import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class DownloadListServiceImpl implements DownloadListService{

    private Log log = LogFactory.getLog(getClass());
    private DownloadListDao dao;

    @Autowired
    private DownloadListServiceImpl(DownloadListDao dao) {
        if(dao==null)
            throw new BeanInitializationException("DownloadList repository is null");
        this.dao = dao;
    }

    @Override
    public DownloadListVo get(int id) {
        return dao.findItem(id);
    }

    @Override
    public List<DownloadListVo> getList(int planId) {
        return dao.find(planId);
    }

    @Override
    public List<DownloadListVo> getList(int planId, int offset, int limit) {
        return null;
    }

    @Override
    public boolean insert(DownloadListVo item) {
        return dao.insert(item);
    }

    @Override
    public boolean insert(@NonNull CollectPlanVo plan, @NonNull String filePath) {
        DownloadListVo item = new DownloadListVo(new Timestamp(System.currentTimeMillis()), "new",
                plan.getId(), filePath);
        return dao.insert(item);
    }

    @Override
    public boolean updateDownloadStatus(int id) {
        DownloadListVo item = dao.findItem(id);
        if(item==null)
            return false;
        if(!item.getStatus().equals("finished")) {
            item.setStatus("finished");
            dao.update(item);
        }
        return true;
    }

    @Override
    public boolean delete(int id) {
        return dao.delete(id);
    }
}
