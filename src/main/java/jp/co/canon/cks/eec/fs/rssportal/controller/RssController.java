package jp.co.canon.cks.eec.fs.rssportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rss")
public class RssController {
    @RequestMapping(value={"", "*"})
    public String rss(Model model) {
        model.addAttribute("pageName", "index");
        return "page";
    }
}