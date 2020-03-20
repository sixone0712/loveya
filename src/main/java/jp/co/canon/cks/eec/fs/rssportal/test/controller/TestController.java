package jp.co.canon.cks.eec.fs.rssportal.test.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class TestController {

    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping("test/download")
    public ModelAndView download(@RequestParam Map<String, Object> param) {

        log.warn("test download feature");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("test/download");
        return mav;
    }
}
