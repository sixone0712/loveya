package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.dao.DownloadHistoryDao;
import jp.co.canon.cks.eec.fs.rssportal.vo.DownloadHistoryVo;
import org.springframework.beans.factory.annotation.Autowired;
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
}
