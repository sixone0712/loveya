package jp.co.canon.cks.eec.fs.rssportal.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RssControllerTest {

    private final RssController rssController;

    @Autowired
    public RssControllerTest(RssController rssController) {
        this.rssController = rssController;
    }

    @Test
    void rss() {
        String ret = null;
        ret = rssController.rss(null);
        assertEquals("index.html",ret);
    }

    @Test
    void redirect() {
        String ret = null;
        ret = rssController.redirect();
        assertEquals("redirect:/rss",ret);
    }
}