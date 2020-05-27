package jp.co.canon.cks.eec.fs.rssportal.downloadlist;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DownloadListControllerTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final DownloadListController downloadController;
    private final DownloadListService downloadService;

    @Autowired
    public DownloadListControllerTest(DownloadListController controller,
                                      DownloadListService downloadService) {
        this.downloadController = controller;
        this.downloadService = downloadService;
    }

    @BeforeEach
    void setUp() {
        log.info("setup");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getList() {
        log.info("test getList()");
        MockHttpServletRequest request = new MockHttpServletRequest();
        ResponseEntity<List<DownloadListVo>> resp = downloadController.getList(request, 0);
        assertEquals(resp.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void delete() {
        log.info("test delete()");
        final int planId = 99990001;

        // insert a row temporarily.
        DownloadListVo item = new DownloadListVo(new Timestamp(System.currentTimeMillis()),
                "new", planId, "/test/tmp");
        assertTrue(downloadService.insert(item));

        // get a download list index
        MockHttpServletRequest request = new MockHttpServletRequest();
        List<DownloadListVo> list = downloadController.getList(request, planId).getBody();
        assertNotNull(list);
        assertNotEquals(list.size(), 0);
        int downloadId = list.get(0).getId();

        // request delete an item
        assertEquals(downloadController.delete(request, -1).getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(downloadController.delete(request, downloadId).getStatusCode(), HttpStatus.OK);

        // check that the item was deleted
        assertEquals(downloadController.getList(request, planId).getBody().size(), 0);
    }

}