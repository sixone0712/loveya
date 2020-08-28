package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.vo.GenreVo;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final GenreController genreController;

    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    GenreControllerTest(GenreController genreController) {
        this.genreController = genreController;
    }

    @Test
    void Genre() {

        Map<String, Object> ret;
        GenreVo genreVo = new GenreVo();

        genreVo.setName("");
        genreVo.setCategory("");
        ret = genreController.addGenre(genreVo);
        assertEquals(15,ret.get("result"));
        assertNull(ret.get("data"));

        genreVo.setName("TEST");
        genreVo.setCategory("001");
        ret = genreController.addGenre(genreVo);
        assertEquals(0,ret.get("result"));
        assertNotNull(ret.get("data"));

        genreVo.setName("TEST");
        genreVo.setCategory("002");
        ret = genreController.addGenre(genreVo);
        assertEquals(11,ret.get("result"));
        assertNull(ret.get("data"));

        //=====================================================================
        Map<String, Object> getReturn= null;
        getReturn = genreController.getGenre();
        if(getReturn.get("result").toString().equals("0"))
        {
            List<GenreVo> list = (List<GenreVo>) getReturn.get("data");
            for (GenreVo genre : list)
                if ("TEST".equals(genre.getName())) {

                    genreVo.setName(null);
                    genreVo.setCategory(null);
                    ret = genreController.delete(genreVo);
                    assertEquals(15,ret.get("result"));
                    assertNull(ret.get("data"));
                    assertNotNull(ret.get("update"));

                    genreVo.setCategory("003");
                    ret = genreController.modifyGenre(genre);
                    assertEquals(0,ret.get("result"));
                    assertNotNull(ret.get("data"));
                    assertNotNull(ret.get("update"));

                }
        }
        GenreVo genre = new GenreVo();
        ret = genreController.modifyGenre(genre);
        assertEquals(15,ret.get("result"));

        //==========================================
        ret = genreController.delete(genreVo);
        assertEquals(15,ret.get("result"));

        genreVo.setId(100000);
        genreVo.setName("");
        genreVo.setCategory("");
        ret = genreController.delete(genreVo);
        assertEquals(16,ret.get("result"));
        assertNull(ret.get("data"));
        assertNotNull(ret.get("update"));

        genreVo.setName("TEST");
        genreVo.setCategory("003");
        genreController.addGenre(genreVo);
        getReturn = genreController.getGenre();
        if(getReturn.get("result").toString().equals("0"))
        {
            List<GenreVo> list = (List<GenreVo>) getReturn.get("data");
            for (GenreVo genre2 : list)
                if ("TEST".equals(genre2.getName())) {
                    genreController.delete(genre2);
                }
        }
    }



    @Test
    void getUpdate() {
        Map<String, Object> ret= null;
        ret = genreController.getUpdate();
        assertNotNull(ret.get("update"));
    }
}