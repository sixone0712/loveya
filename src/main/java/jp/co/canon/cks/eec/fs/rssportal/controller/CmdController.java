package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.service.CommandService;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.CommandVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rss/rest/cmd")
public class CmdController {

    private final HttpSession httpSession;
    private final CommandService serviceCmd;
    private final String CMD_RESULT = "result";
    private final String CMD_DATA = "data";

    @Autowired
    public CmdController(HttpSession httpSession, CommandService serviceCmd) {
        log.info("/command/Controller");
        this.httpSession = httpSession;
        this.serviceCmd = serviceCmd;
    }


    @GetMapping("/getList")
    @ResponseBody
    public Map<String, Object> getCmdList(@RequestParam Map<String, Object> param)  throws Exception {
        int res = 0;
        log.info("/getList");
        String type = param.containsKey("cmd_type")?(String)param.get("cmd_type"):null;
        Map<String, Object> returnData = new HashMap<>();

        if(type==null || type.isEmpty())
        {
            returnData.put(CMD_RESULT,  201);
            log.info("COMMAND_FAIL_EMPTY_NAME");
        }
        else
        {
            List<CommandVo> list = serviceCmd.getCommandList(type);
            if(list == null)
            {
                log.info("List data is null");
            }
            else {
                returnData.put(CMD_RESULT,  0);
                returnData.put(CMD_DATA, list);
            }
        }
        return returnData;
    }

    @GetMapping("/add")
    @ResponseBody
    public int addCmd(@RequestParam Map<String, Object> param)  throws Exception {
        int res = 0;
        log.info("/addCmd");
        String type = param.containsKey("cmd_type")?(String)param.get("cmd_type"):null;
        String name = param.containsKey("cmd_name")?(String)param.get("cmd_name"):null;

        if(type==null || type.isEmpty() || name==null || name.isEmpty())
        {
            res = 201; //COMMAND_FAIL_EMPTY_NAME
            log.info("COMMAND_FAIL_EMPTY_NAME");
        }
        else
        {

            CommandVo returnObj = serviceCmd.findCommand(name,type);
            if(returnObj != null )
            {
                log.info("already exist command");
                res = 200; //COMMAND_FAIL_SAME_NAME
            }
            else
            {
                CommandVo cmdObj = new CommandVo();
                cmdObj.setCmd_type(type);
                cmdObj.setCmd_name(name);
                serviceCmd.addCmd(cmdObj);
                log.info("add new Command");
            }
        }
        return res;
    }

    @GetMapping("/delete")
    @ResponseBody
    public int deleteCmd(@RequestParam Map<String, Object> param)  throws Exception {
        int res = 0;
        log.info("/delete command");
        String id = param.containsKey("id")?(String)param.get("id"):null;
        log.info("id" + id);

        if(id==null || id.isEmpty())
        {
            res = 203; //COMMAND_NO_SELECT_COMMAND
            log.info("COMMAND_NO_SELECT_COMMAND");
        }
        else
        {
            CommandVo cmdObj = new CommandVo();
            CommandVo returnObj = serviceCmd.getCommand(Integer.parseInt(id));
            if(returnObj != null )
            {
                log.info("delete Command");
                log.info("delete Command id = " + returnObj.getId());
                log.info("delete Command id = " + returnObj.getCmd_type());
                log.info("delete Command id = " + returnObj.getCmd_name());
                serviceCmd.deleteCmd(Integer.parseInt(id));
            }
            else
            {
                log.info("COMMAND_NO_SUCH_COMMAND");
                res = 202; //COMMAND_NO_SUCH_COMMAND
            }
        }
        return res;
    }



    private final Log log = LogFactory.getLog(getClass());

}
