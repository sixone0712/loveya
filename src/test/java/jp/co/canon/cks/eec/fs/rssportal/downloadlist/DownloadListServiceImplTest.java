package jp.co.canon.cks.eec.fs.rssportal.downloadlist;

import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class DownloadListServiceImplTest {

    private DownloadListService downloadListService;

    @Autowired
    public DownloadListServiceImplTest(DownloadListService downloadListService) {
        this.downloadListService = downloadListService;
    }

    @Test
    void updateDownloadStatus() {
        assertNotNull(downloadListService);
        downloadListService.updateDownloadStatus(0);
    }

    @Test
    void insert() {
        int planId = 999999;
        CollectPlanVo plan = new CollectPlanVo();
        plan.setId(planId);
        assertTrue(downloadListService.insert(plan, "nowhere"));

        List<DownloadListVo> list = downloadListService.getList(planId);
        assertNotNull(list);
        assertNotEquals(list.size(), 0);
        for(DownloadListVo item: list) {
            downloadListService.updateDownloadStatus(item.getId());
            assertTrue(downloadListService.delete(item.getId()));
        }
    }

    @Test
    void getList() {
        // the below is not implemented yet.
        assertNull(downloadListService.getList(0, 0, 0));
    }
}