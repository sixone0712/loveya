package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.dao.CommandDao;
import jp.co.canon.cks.eec.fs.rssportal.vo.CommandVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommandServiceImpl implements CommandService {

    private final CommandDao dao;
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    public CommandServiceImpl(CommandDao dao) {
        this.dao = dao;
    }

    @Override
    public List<CommandVo> getCommandListAll() {
        return dao.findAll();
    }

    @Override
    public List<CommandVo> getCommandList(@NonNull String type) {
        Map<String, Object> param = new HashMap<>();
        param.put("cmd_type",type);
        return dao.findCommandList(param);
    }

    @Override
    public CommandVo getCommand(@NonNull int id) {
        Map<String, Object> param = new HashMap<>();
        param.put("id",id);

        return dao.find(param);
    }
    @Override
    public CommandVo findCommand(String name, String type) {
        Map<String, Object> param = new HashMap<>();
        param.put("cmd_type",type);
        List<CommandVo> list = dao.findCommandList(param);
        if(list!=null) {
            for (CommandVo commandVo : list) {
                if (commandVo.getCmd_name().equals(name)) {
                    return commandVo;
                }
            }
        }
        return null;
    }

    @Override
    public boolean addCmd(@NonNull CommandVo cmd) {
        if(cmd.getCmd_name().isEmpty() || cmd.getCmd_type().isEmpty()) {
            return false;
        }
        return dao.add(cmd);
    }

    @Override
    public boolean modifyCmd(@NonNull CommandVo cmd) {
        CommandVo temp = getCommand(cmd.getId());
        if(temp==null) {
            return false;
        }
        return dao.modify(cmd);
    }

    @Override
    public boolean deleteCmd(int id){
        CommandVo temp = getCommand(id);
        if(temp==null) {
            return false;
        }
        temp.setValidity(false);
        return dao.delete(temp);
    }
}
