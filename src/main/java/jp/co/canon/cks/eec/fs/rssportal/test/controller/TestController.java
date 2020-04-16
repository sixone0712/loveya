package jp.co.canon.cks.eec.fs.rssportal.test.controller;

import jp.co.canon.cks.eec.fs.rssportal.service.UserPermissionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/download")
    public ModelAndView download(@RequestParam Map<String, Object> param) {

        log.info("test download feature");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("test/download");
        return mav;
    }

    @RequestMapping("/login")
    public ModelAndView loginDummy() {
        log.info("loginDummy");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login");
        return mav;
    }


    private final Log log = LogFactory.getLog(getClass());
}
