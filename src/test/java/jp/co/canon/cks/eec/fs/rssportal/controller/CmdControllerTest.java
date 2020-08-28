package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.service.CommandService;
import jp.co.canon.cks.eec.fs.rssportal.vo.CommandVo;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class CmdControllerTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MockMvc mockMvc;

/*
    private final CmdController cmdController;
    private final CommandService serviceCommand;
    private final String CMD_RESULT = "result";
    private final String CMD_DATA = "data";
*/

    @Autowired
    public CmdControllerTest() {
    }

    @Test
    void getCmdListTest() throws Exception {


        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        String url = "/rss/api/auths/login";
        info.add("username", "ymkwon");
        info.add("password", "46f94c8de14fb36680850768ff1b7f2a");
        // when
        MvcResult result= mockMvc.perform(get(url).params(info).servletPath(url))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        JSONObject content = new JSONObject(result.getResponse().getContentAsString());
        log.info("content : "+content);
        log.info("accessToken : "+content.getString("accessToken"));

        String url2 = "/rss/api/vftp/command";
        mockMvc.perform(get(url2).servletPath(url2).param("type","1").header("Authorization", "Bearer " + content.getString("accessToken")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        MultiValueMap<String, String> temp = new LinkedMultiValueMap<>();
        temp.add("cmd_type",null);
        mockMvc.perform(post(url2).params(temp).servletPath(url2).header("Authorization", "Bearer " + content.getString("accessToken")))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());

        temp.clear();
        temp.add("cmd_type","300");
        mockMvc.perform(post(url2).params(temp).servletPath(url2).header("Authorization", "Bearer " + content.getString("accessToken")))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());



/*        Map<String, Object> param = new HashMap<>();
        Map<String, Object> resp = null;

        *//*test 1 - in case : Type is null*//*
        param.put("cmd_type",null);
        resp = cmdController.getCmdList(param);
        assertEquals(201, resp.get(CMD_RESULT));

        *//*test 2 - in case : Type is wrong*//*
        param.put("cmd_type","300");
        resp = cmdController.getCmdList(param);
        assertEquals(1, resp.get(CMD_RESULT));  //RSS_FAIL

        *//*test 3 - in case : Type is normal*//*
        param.put("cmd_type","1");
        resp = cmdController.getCmdList(param);
        assertEquals(0, resp.get(CMD_RESULT));  //RSS_SUCCESS*/

    }

    @Test   /*test 1 - in case : Type is normal   name normal*/
    void addCmdTest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/rest/cmd/add");

        Map<String, Object> param = new HashMap<>();
        int result = 0;

//        /*test 1 - in case : type is null command*/
//        param.put("cmd_type",null);
//        param.put("cmd_name","DE_TEST");
//        result = cmdController.addCmd(param);
//        assertEquals(201, result);  //COMMAND_FAIL_EMPTY_NAME
//
//        /*test 2 - in case : name is null command*/
//        param.put("cmd_type","1");
//        param.put("cmd_name",null);
//        result = cmdController.addCmd(param);
//        assertEquals(201, result);  //COMMAND_FAIL_EMPTY_NAME
//
//        /*test 3 - in case : Type is normal   name normal*/
//        param.put("cmd_type","1");
//        param.put("cmd_name","DE_TEST");
//        result = cmdController.addCmd(param);
//        assertEquals(0, result);  //RSS_SUCCESS
//
//        /*test 4 - in case : already exist command*/
//        param.put("cmd_type","1");
//        param.put("cmd_name","DE_TEST");
//        result = cmdController.addCmd(param);
//        assertEquals(200, result);  //COMMAND_FAIL_SAME_NAME

    }
}