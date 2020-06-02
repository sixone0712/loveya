package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.manage.FileServiceManageServiceLocator;
import jp.co.canon.cks.eec.fs.manage.FileServiceManageSoapBindingStub;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceUsedSOAP;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.rpc.ServiceException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileDownloadExecutorTest {

    private FileDownloadExecutor executor;

    public FileDownloadExecutorTest() {
        FileServiceModel service = new FileServiceUsedSOAP(DownloadConfig.FCS_SERVER_ADDR);
        FileServiceManageServiceLocator serviceLocator = new FileServiceManageServiceLocator();
        FileServiceManage manager = null;
        try {
            manager = serviceLocator.getFileServiceManage();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        Object obj = new Object();
        executor = new FileDownloadExecutor("manual", null,
                manager, service, new ArrayList<>(), true);
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