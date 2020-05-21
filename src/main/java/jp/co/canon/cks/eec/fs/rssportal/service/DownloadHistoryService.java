package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.vo.DownloadHistoryVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;

import java.util.List;

public interface DownloadHistoryService {
    List<DownloadHistoryVo> getHistoryList();
}
