package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.vo.CommandVo;

import java.util.List;
import java.util.Map;

public interface CommandService {

    List<CommandVo> getCommandListAll();
    List<CommandVo> getCommandList(String type);
    CommandVo getCommand(int id);
    CommandVo findCommand(String name, String type);
    int addCmd(CommandVo cmd);
    boolean modifyCmd(CommandVo cmd);
    boolean deleteCmd(int id);
}
