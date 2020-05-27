package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.service.GenreService;
import jp.co.canon.cks.eec.fs.rssportal.vo.GenreVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rss/rest")
public class GenreController {

    private final GenreService serviceGenre;
    private final Log log = LogFactory.getLog(getClass());
    private final String GENRE_RESULT = "result";
    private final String GENRE_UPDATE = "update";
    private final String GENRE_DATA = "data";
    private final int RSS_SUCCESS = 0;
    private final int RSS_FAIL = 1;
    private final int GENRE_SET_FAIL_NO_ITEM = 10;
    private final int GENRE_SET_FAIL_SAME_NAME = 11;
    private final int GENRE_SET_FAIL_EMPTY_NAME = 12;
    private final int GENRE_SET_FAIL_SEVER_ERROR = 13;
    private final int GENRE_SET_FAIL_NOT_SELECT_GENRE = 14;
    private final int GENRE_SET_FAIL_PARAMETAR_ERROR = 15;
    private final int GENRE_SET_FAIL_NOT_EXIST_GENRE = 16;

    @Autowired
    public GenreController(GenreService serviceGenre) {
        this.serviceGenre = serviceGenre;
    }

    @RequestMapping("/genre/get")
    @ResponseBody
    public Map<String, Object> getGenre() {
        log.info("[/genre/get] start");

        Map<String, Object> returnData = new HashMap<>();
        // select all data from db
        List<GenreVo> list = serviceGenre.getGenreList();
        // get update
        Date date = serviceGenre.getGenreUpdate();

        returnData.put(GENRE_RESULT, RSS_SUCCESS);
        returnData.put(GENRE_UPDATE, date);
        returnData.put(GENRE_DATA, list);

        log.info("[/genre/get] end");
        return returnData;
    }

    /*
    @RequestMapping("/genre/add")
    @ResponseBody
    public Map<String, Object> addGenre(@RequestBody Map<String, Object> param) {

        log.info("[/genre/add]");
        log.info("param.size() : " + param.size());
        log.info("param.name : " + param.get("name"));
        log.info("param.category : " + param.get("category"));

        // return json data
        Map<String, Object> returnData = new HashMap<>();
        returnData.put(GENRE_RESULT, GENRE_FAIL);
        returnData.put(GENRE_DATA, null);

        String name = param.containsKey("name") ? (String)param.get("name") : null;
        String category = param.containsKey("category") ? (String)param.get("category") : null;

        if(name==null || category==null || name.isEmpty() || category.isEmpty()) {
            log.error("[/genre/add] parameter error");
            returnData.put(GENRE_RESULT, GENRE_FAIL_PARAMETER);
            return returnData;
        }

        GenreVo parseGenre = new GenreVo();
        parseGenre.setName((String)param.get("name"));
        parseGenre.setCategory((String)param.get("category"));

        // find same name
        GenreVo findSameName = serviceGenre.getGenreByName(parseGenre.getName());
        if(findSameName != null) {
            log.error("[/genre/add] found same name");
            returnData.put(GENRE_RESULT, GENRE_FAIL_SAME_NAME);
            return returnData;
        }

        // querying add to db
        if(serviceGenre.addGenre(parseGenre)) {
            // querying select all data from db
            List<GenreVo> list = serviceGenre.getGenreList();
            returnData.put(GENRE_RESULT, GENRE_SUCCESS);
            returnData.put(GENRE_DATA, list);
        } else {
            log.error("[/genre/add] db set error");
        }

        return returnData;
    }
    */

    @RequestMapping("/genre/add")
    @ResponseBody
    public Map<String, Object> addGenre(@RequestBody GenreVo param) {

        log.info("[/genre/add] start");

        // get update
        Date date = serviceGenre.getGenreUpdate();

        // return json data
        Map<String, Object> returnData = new HashMap<>();
        returnData.put(GENRE_RESULT, RSS_FAIL);
        returnData.put(GENRE_UPDATE, date);
        returnData.put(GENRE_DATA, null);

        String name = param.getName();
        String category = param.getCategory();

        log.info("name : " + name);
        log.info("category : " + category);

        if(name == null || category == null || name.isEmpty() || category.isEmpty()) {
            log.error("[/genre/modify] parameter error");
            returnData.put(GENRE_RESULT, GENRE_SET_FAIL_PARAMETAR_ERROR);
            return returnData;
        }

        // find same name
        GenreVo findSameName = serviceGenre.getGenreByName(name);
        if(findSameName != null) {
            log.error("[/genre/add] found same name");
            returnData.put(GENRE_RESULT, GENRE_SET_FAIL_SAME_NAME);
            return returnData;
        }

        // querying add to db
        if(serviceGenre.addGenre(param)) {
            // querying set update
            serviceGenre.setGenreUpdate();
            // querying select all data from db
            List<GenreVo> list = serviceGenre.getGenreList();
            returnData.put(GENRE_RESULT, RSS_SUCCESS);
            returnData.put(GENRE_DATA, list);
        } else {
            log.error("[/genre/add] db set error");
        }

        log.info("[/genre/add] end");
        return returnData;
    }

    /*
    @RequestMapping("/genre/modify")
    @ResponseBody
    public Map<String, Object> modifyGenre(@RequestBody Map<String, Object> param) {

        log.info("[/genre/modify]");
        log.info("param.size() : " + param.size());
        log.info("param.id : " + param.get("id"));
        log.info("param.name : " + param.get("name"));
        log.info("param.category : " + param.get("category"));

        // return json data
        Map<String, Object> returnData = new HashMap<>();
        returnData.put(GENRE_RESULT, GENRE_FAIL);
        returnData.put(GENRE_DATA, null);

        int id = param.containsKey("id") ? (int)param.get("id") : 0;
        String name = param.containsKey("name") ? (String)param.get("name") : null;
        String category = param.containsKey("category") ? (String)param.get("category") : null;

        if(id == 0 || name==null || category==null || name.isEmpty() || category.isEmpty()) {
            log.error("[/genre/modify] parameter error");
            returnData.put(GENRE_RESULT, GENRE_FAIL_PARAMETER);
            return returnData;
        }

        GenreVo parseGenre = new GenreVo();
        parseGenre.setId(id);
        parseGenre.setName(name);
        parseGenre.setCategory(category);

        // find same name
        GenreVo findSameName = serviceGenre.getGenreByName(parseGenre.getName());
        if(findSameName != null) {
            // check same id
            if(parseGenre.getId() != findSameName.getId()) {
                log.error("[/genre/add] found same name");
                returnData.put(GENRE_RESULT, GENRE_FAIL_SAME_NAME);
                return returnData;
            }
        }

        // querying add to db
        if(serviceGenre.modifyGenre(parseGenre)) {
            // querying select all data from db
            List<GenreVo> list = serviceGenre.getGenreList();
            returnData.put(GENRE_RESULT, GENRE_SUCCESS);
            returnData.put(GENRE_DATA, list);
        } else {
            log.error("[/genre/add] db set error");
        }

        log.info("modifiy end");

        return returnData;
    }
    */

    @RequestMapping("/genre/modify")
    @ResponseBody
    public Map<String, Object> modifyGenre(@RequestBody GenreVo param) {

        log.info("[/genre/modify] start");

        // get update
        Date date = serviceGenre.getGenreUpdate();

        // return json data
        Map<String, Object> returnData = new HashMap<>();
        returnData.put(GENRE_RESULT, RSS_FAIL);
        returnData.put(GENRE_UPDATE, date);
        returnData.put(GENRE_DATA, null);

        int id = param.getId();
        String name = param.getName();
        String category = param.getCategory();

        if(id == 0 || name == null || category == null || name.isEmpty() || category.isEmpty()) {
            log.error("[/genre/modify] parameter error");
            returnData.put(GENRE_RESULT, GENRE_SET_FAIL_PARAMETAR_ERROR);
            return returnData;
        }

        // find same id form DB
        GenreVo findSameId = serviceGenre.getGenreById(id);
        if(findSameId == null) {
            log.error("[/genre/add] there is no db data for id : " + id);
            returnData.put(GENRE_RESULT, GENRE_SET_FAIL_NOT_EXIST_GENRE);
            return returnData;
        }

        // find same name
        GenreVo findSameName = serviceGenre.getGenreByName(name);
        if(findSameName != null) {
            // check same id
            if(id != findSameName.getId()) {
                log.error("[/genre/modify] found same name");
                returnData.put(GENRE_RESULT, GENRE_SET_FAIL_SAME_NAME);
                return returnData;
            }
        }

        // querying add to db
        if(serviceGenre.modifyGenre(param)) {
            // querying set update
            serviceGenre.setGenreUpdate();
            // querying select all data from db
            List<GenreVo> list = serviceGenre.getGenreList();
            returnData.put(GENRE_RESULT, RSS_SUCCESS);
            returnData.put(GENRE_DATA, list);
        } else {
            log.error("[/genre/modify] db set error");
        }

        log.info("[/genre/modify] end");

        return returnData;
    }

    @RequestMapping("/genre/delete")
    @ResponseBody
    public Map<String, Object> delete(@RequestBody GenreVo param) {

        log.info("[/genre/delete] start");

        // get update
        Date date = serviceGenre.getGenreUpdate();

        // return json data
        Map<String, Object> returnData = new HashMap<>();
        returnData.put(GENRE_RESULT, RSS_FAIL);
        returnData.put(GENRE_UPDATE, date);
        returnData.put(GENRE_DATA, null);

        int id = param.getId();
        log.info("id : " + id);

        if(id <= 0) {
            log.error("[/genre/add] parameter error");
            returnData.put(GENRE_RESULT, GENRE_SET_FAIL_PARAMETAR_ERROR);
            return returnData;
        }

        // find same id
        GenreVo findSameId = serviceGenre.getGenreById(id);
        //log.info("findSameId" + findSameId.getName());
        if(findSameId == null) {
            log.error("[/genre/add] there is no db data for id : " + id);
            returnData.put(GENRE_RESULT, GENRE_SET_FAIL_NOT_EXIST_GENRE);
            return returnData;
        }

        // querying delete to db
        if(serviceGenre.deleteGenre(id)) {
            // querying set update
            serviceGenre.setGenreUpdate();
            // querying select all data from db
            List<GenreVo> list = serviceGenre.getGenreList();
            returnData.put(GENRE_RESULT, RSS_SUCCESS);
            returnData.put(GENRE_DATA, list);
        } else {
            log.error("[/genre/add] db set error");
        }
        log.info("[/genre/delete] end");
        return returnData;
    }

    @RequestMapping("/genre/getUpdate")
    @ResponseBody
    public Map<String, Object> getUpdate() {
        Date date = serviceGenre.getGenreUpdate();

        // return json data
        Map<String, Object> returnData = new HashMap<>();
        returnData.put(GENRE_UPDATE, date);

        return returnData;
    }
}
