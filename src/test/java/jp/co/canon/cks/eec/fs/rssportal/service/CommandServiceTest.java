package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.controller.CmdController;
import jp.co.canon.cks.eec.fs.rssportal.vo.CommandVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CommandServiceTest {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final CommandService commandService;

    @Autowired
    public CommandServiceTest(CommandService commandService) {
        this.commandService = commandService;
    }

//    @Test
//    void TestScenario() {
//        Boolean ret = false;
//        CommandVo cVo = new CommandVo();
//        cVo.setCmd_name("AAA");
//        cVo.setCmd_type("1");
//        cVo.setValidity(true);
//        ret=commandService.addCmd(cVo);
//        if(ret)
//        {
//            CommandVo modifyVo = null;
//            modifyVo = commandService.findCommand("AAA","1");
//            if(modifyVo!= null)
//            {
//                CommandVo confirmVo = null;
//                modifyVo.setCmd_name("BBB");
//                commandService.modifyCmd(modifyVo);
//                confirmVo = commandService.getCommand(modifyVo.getId());
//                assertEquals(modifyVo.getCmd_name(),confirmVo.getCmd_name());
//                assertNotNull(commandService.getCommandListAll());
//                commandService.deleteCmd(modifyVo.getId());
//            }
//        }
//
//    }

    @Test
    void modifyCmd() {
    }
}