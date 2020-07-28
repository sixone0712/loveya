package jp.co.canon.cks.eec.fs.rssportal.dao;

import jp.co.canon.cks.eec.fs.rssportal.vo.CommandVo;

import java.util.List;
import java.util.Map;

public interface CommandDao {

    List<CommandVo> findAll();
    List<CommandVo> findCommandList(Map<String, Object> param);
    CommandVo find(Map<String, Object> param);
    int add(CommandVo cmd);
    boolean modify(CommandVo cmd);
    boolean delete(CommandVo cmd);
}
