package jp.co.canon.cks.eec.fs.rssportal.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class RssController {
    private Log log = LogFactory.getLog(getClass());

    @RequestMapping(value={"/", "/rss"})
    public String rss(Model model) {
        log.info("[RssController] rss url called");
        //model.addAttribute("pageName", "index");
        //return "page";
		return "index.html";
    }

    @RequestMapping(value={"/rss/page/**"})
    public String redirect() {
        log.info("[RssController] other url called");
        return "redirect:/rss";
    }

    @RequestMapping(value={"/rss/version"})
    public void version(HttpServletResponse response) throws IOException {
        String version = "Rapid_Collector_V20_03_02";
        response.getWriter().print(version);
    }
}
