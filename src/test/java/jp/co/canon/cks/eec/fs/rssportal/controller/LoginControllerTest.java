package jp.co.canon.cks.eec.fs.rssportal.controller;

import com.google.gson.JsonObject;
import jdk.nashorn.internal.parser.JSONParser;
import jp.co.canon.cks.eec.fs.rssportal.service.CommandService;
import jp.co.canon.cks.eec.fs.rssportal.service.UserService;
import org.json.JSONObject;
import org.json.JSONString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.Charset;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MockMvc mockMvc;
    private final UserService userService;

    @Autowired
    LoginControllerTest(UserService userService) { this.userService = userService;}


    @Test
    void isLogin() throws Exception {
        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        String url = "/rss/api/auths/login";
        info.clear();
        info.add("username", "Administrator");
        info.add("password", "5f4dcc3b5aa765d61d8327deb882cf99");
        // when
        String url2 = "/rss/api/auths/me";
        mockMvc.perform(get(url2).servletPath(url2))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        MvcResult result= mockMvc.perform(get(url).params(info).servletPath(url))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JSONObject content = new JSONObject(result.getResponse().getContentAsString());
        log.info("content : "+content);
        log.info("accessToken : "+content.getString("accessToken"));

        mockMvc.perform(get(url2).servletPath(url2).header("Authorization", "Bearer " + content.getString("accessToken")))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test   //incorrect
    void login_ng() throws Exception {
        //
        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        String url = "/rss/api/auths/login";
        info.clear();
        mockMvc.perform(get(url).params(info).servletPath(url))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        info.clear();
        info.add("username", "test_user");
        info.add("password", "123123");
        // when
        mockMvc.perform(get(url).params(info).servletPath(url))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        info.clear();
        info.add("username", "Administrator");
        info.add("password", "1");
        // when
        mockMvc.perform(get(url).params(info).servletPath(url))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // when
    }
    @Test
    void login_out() throws Exception {
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

        String url2 = "/rss/api/auths/logout";
        mockMvc.perform(get(url2).servletPath(url2).header("Authorization", "Bearer " + content.getString("accessToken")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void reissueAccessToken() throws Exception {
        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        String url = "/rss/api/auths/login";
        info.add("username", "ymkwon");
        info.add("password", "46f94c8de14fb36680850768ff1b7f2a");
        // when
        //login
        MvcResult result= mockMvc.perform(get(url).params(info).servletPath(url))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        JSONObject content = new JSONObject(result.getResponse().getContentAsString());
        log.info("refreshToken : "+content.getString("refreshToken"));

        // request New Accress Token
        String url2 = "/rss/api/auths/token";
        JSONObject object = new JSONObject();
        mockMvc.perform(post(url2)
                        .params(info)
                        .servletPath(url2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(object))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + content.getString("accessToken"))
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andDo(MockMvcResultHandlers.print());

        object.put("refreshToken",content.getString("refreshToken"));
        mockMvc.perform(post(url2)
                .params(info)
                .servletPath(url2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(object))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + content.getString("accessToken"))
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
        Date curDate = new Date();
        curDate.setTime(curDate.getTime()+10);
        userService.setToken(content.getString("refreshToken"),curDate);
        mockMvc.perform(post(url2)
                .params(info)
                .servletPath(url2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(object))
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + content.getString("accessToken"))
        )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }
}