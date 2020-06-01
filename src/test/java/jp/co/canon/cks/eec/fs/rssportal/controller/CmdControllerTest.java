package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.service.CommandService;
import jp.co.canon.cks.eec.fs.rssportal.vo.CommandVo;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CmdControllerTest {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final CmdController cmdController;
    private final CommandService serviceCommand;
    private final String CMD_RESULT = "result";
    private final String CMD_DATA = "data";

    @Autowired
    public CmdControllerTest(CmdController cmdController, CommandService serviceCommand) {
        this.cmdController = cmdController;
        this.serviceCommand = serviceCommand;
    }

    @Test
    void getCmdListTest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/cmd/getList");
        Map<String, Object> param = new HashMap<>();
        Map<String, Object> resp = null;

        /*test 1 - in case : Type is null*/
        param.put("cmd_type",null);
        resp = cmdController.getCmdList(param);
        assertEquals(201, resp.get(CMD_RESULT));

        /*test 2 - in case : Type is wrong*/
        param.put("cmd_type","300");
        resp = cmdController.getCmdList(param);
        assertEquals(1, resp.get(CMD_RESULT));  //RSS_FAIL

        /*test 3 - in case : Type is normal*/
        param.put("cmd_type","1");
        resp = cmdController.getCmdList(param);
        assertEquals(0, resp.get(CMD_RESULT));  //RSS_SUCCESS

    }

    @Test   /*test 1 - in case : Type is normal   name normal*/
    void addCmdTest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/cmd/add");

        Map<String, Object> param = new HashMap<>();
        int result = 0;

        /*test 1 - in case : type is null command*/
        param.put("cmd_type",null);
        param.put("cmd_name","DE_TEST");
        result = cmdController.addCmd(param);
        assertEquals(201, result);  //COMMAND_FAIL_EMPTY_NAME

        /*test 2 - in case : name is null command*/
        param.put("cmd_type","1");
        param.put("cmd_name",null);
        result = cmdController.addCmd(param);
        assertEquals(201, result);  //COMMAND_FAIL_EMPTY_NAME

        /*test 3 - in case : Type is normal   name normal*/
        param.put("cmd_type","1");
        param.put("cmd_name","DE_TEST");
        result = cmdController.addCmd(param);
        assertEquals(0, result);  //RSS_SUCCESS

        /*test 4 - in case : already exist command*/
        param.put("cmd_type","1");
        param.put("cmd_name","DE_TEST");
        result = cmdController.addCmd(param);
        assertEquals(200, result);  //COMMAND_FAIL_SAME_NAME

    }

    @Test
    void deleteCmd() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/cmd/delete");

        Map<String, Object> param = new HashMap<>();
        int result = 0;

        /*test 1 - in case : delete id is null command*/
        param.put("id",null);
        result = cmdController.deleteCmd(param);
        assertEquals(203, result);  //COMMAND_NO_SELECT_COMMAND

        /*test 2 - in case : delete id not exist command*/
        param.put("id","1000000");
        result = cmdController.deleteCmd(param);
        assertEquals(202, result);  //COMMAND_NO_SUCH_COMMAND

        /*test 3 - in case : delete id exist command*/
        CommandVo returnObj = serviceCommand.findCommand("DE_TEST","1");
        if(returnObj != null )
        {
            param.put("id",String.valueOf(returnObj.getId()));
            result = cmdController.deleteCmd(param);
            assertEquals(0, result);  //RSS_SUCCESS
        }

    }
}