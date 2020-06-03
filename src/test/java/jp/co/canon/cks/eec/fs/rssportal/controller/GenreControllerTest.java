package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.vo.GenreVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GenreControllerTest {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GenreController genreController;

    @Autowired
    public GenreControllerTest(GenreController genreController) {
        this.genreController = genreController;
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void getGenre() {
        Map<String, Object> ret= new HashMap<>();
        ret = genreController.getGenre();
        assertEquals(0,ret.get("result"));
        assertNotNull(ret.get("update"));
        assertNotNull(ret.get("data"));
    }

    @Test
    void addGenre() {
        Map<String, Object> ret= new HashMap<>();
        GenreVo genreVo = new GenreVo();

        /*test 1 - in case : name is empty*/
        genreVo.setName("");
        genreVo.setCategory("");
        ret = genreController.addGenre(genreVo);
        assertEquals(15,ret.get("result"));
        assertNull(ret.get("data"));

        /*test 2 - in case : normal add */
        genreVo.setName("TEST");
        genreVo.setCategory("001");
        ret = genreController.addGenre(genreVo);
        assertEquals(0,ret.get("result"));
        assertNotNull(ret.get("data"));

        /*test 2 - in case : name is empty*/
        genreVo.setName("TEST");
        genreVo.setCategory("002");
        ret = genreController.addGenre(genreVo);
        assertEquals(11,ret.get("result"));
        assertNull(ret.get("data"));


    }

    @Test
    void modifyGenre() {
        Map<String, Object> ret= null;
        GenreVo genreVo = new GenreVo();

        /*test 1 - in case : name is empty*/
        ret = genreController.modifyGenre(genreVo);
        assertEquals(15,ret.get("result"));
        assertNull(ret.get("data"));
        assertNotNull(ret.get("update"));

        /*test 2 - in case : not found id */
        genreVo.setId(100000);
        genreVo.setName("1");
        genreVo.setCategory("1");
        ret = genreController.modifyGenre(genreVo);
        assertEquals(16,ret.get("result"));
        assertNull(ret.get("data"));
        assertNotNull(ret.get("update"));

        /*test 3 - in case : same name modify */
        genreVo.setName("TEST");
        genreVo.setCategory("001");
        genreController.addGenre(genreVo);
        genreVo.setName("TEST1");
        genreVo.setCategory("001");
        genreController.addGenre(genreVo);

        Map<String, Object> getReturn= null;
        getReturn = genreController.getGenre();
        if(getReturn.get("result").toString().equals("0"))
        {
            List<GenreVo> list = (List<GenreVo>) getReturn.get("data");
            for (GenreVo genre : list)
                if ("TEST".equals(genre.getName())) {
                    genre.setName("TEST1");
                    ret = genreController.modifyGenre(genre);
                    assertEquals(11,ret.get("result"));
                    assertNull(ret.get("data"));
                    assertNotNull(ret.get("update"));

                    genre.setName("TEST");
                    ret = genreController.modifyGenre(genre);
                    assertEquals(0,ret.get("result"));
                    assertNotNull(ret.get("data"));

                }
        }


    }

    @Test
    void delete() {
        Map<String, Object> ret= null;
        Map<String, Object> getReturn= null;
        GenreVo genreVo = new GenreVo();

        /*test 1 - in case : name is empty*/
        genreVo.setName("");
        genreVo.setCategory("");
        ret = genreController.delete(genreVo);
        assertEquals(15,ret.get("result"));
        assertNull(ret.get("data"));
        assertNotNull(ret.get("update"));

        /*test 1 - in case : not found id */
        genreVo.setId(100000);
        genreVo.setName("");
        genreVo.setCategory("");
        ret = genreController.delete(genreVo);
        assertEquals(16,ret.get("result"));
        assertNull(ret.get("data"));
        assertNotNull(ret.get("update"));

        /*test 1 - in case :delete test genre  */
        genreVo.setName("TEST");
        genreVo.setCategory("001");
        genreController.addGenre(genreVo);
        getReturn = genreController.getGenre();
        if(getReturn.get("result").toString().equals("0"))
        {
            List<GenreVo> list = (List<GenreVo>) getReturn.get("data");
            for (GenreVo genre : list)
                if ("TEST".equals(genre.getName())) {
                    genreController.delete(genre);
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