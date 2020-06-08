package jp.co.canon.cks.eec.fs.rssportal.background;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FileDownloadExecutorTest {

    @Autowired
    private FileDownloader downloader;
    private FileDownloadExecutor executor;


    public FileDownloadExecutorTest() {
        Object obj = new Object();
        executor = new FileDownloadExecutor("manual", null,
                downloader, new ArrayList<>(), true);
    }

    @Test
    void attributesTest() {
        assertNotNull(executor);
        executor.setAttrCompression(true);
        assertTrue(executor.isAttrCompression());
        executor.setAttrDownloadFilesViaMultiSessions(true);
        assertTrue(executor.isAttrDownloadFilesViaMultiSessions());
        executor.setAttrEmptyAllPathBeforeDownload(true);
        assertTrue(executor.isAttrEmptyAllPathBeforeDownload());
        executor.setAttrReplaceFileForSameFileName(true);
        assertTrue(executor.isAttrReplaceFileForSameFileName());
    }


}