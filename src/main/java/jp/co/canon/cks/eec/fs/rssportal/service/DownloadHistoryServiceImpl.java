package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.dao.DownloadHistoryDao;
import jp.co.canon.cks.eec.fs.rssportal.vo.DownloadHistoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class DownloadHistoryServiceImpl implements DownloadHistoryService {

    private final DownloadHistoryDao dao;

    @Autowired
    public DownloadHistoryServiceImpl(DownloadHistoryDao dao) {
        this.dao = dao;
    }


    @Override
    public List<DownloadHistoryVo> getHistoryList() {
        return dao.findAll();
    }

    @Override
    public boolean addDlHistory(@NonNull DownloadHistoryVo dlHistory) {
        if(dlHistory.getDl_user().isEmpty() || dlHistory.getDl_type().isEmpty()) {
            return false;
        }
        return dao.add(dlHistory);
    }
}
