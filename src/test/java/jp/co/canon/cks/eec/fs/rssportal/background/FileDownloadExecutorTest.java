package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnector;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnectorFactory;
import jp.co.canon.ckbs.eec.fs.manage.service.MachineList;
import jp.co.canon.ckbs.eec.fs.manage.service.configuration.Machine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FileDownloadExecutorTest {

    private Log log = LogFactory.getLog(getClass());

    @Autowired
    private FileDownloader downloader;

    @Autowired
    private FileServiceManageConnectorFactory connectorFactory;

    @Value("${rssportal.file-service-manager.addr}")
    private String fileServiceAddress;

    private FileDownloadExecutor executor;

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

    @Test
    @Timeout(360)
    void vFtpCompatBasicTest() throws InterruptedException {
        assertNotNull(connectorFactory);
        assertNotNull(fileServiceAddress);
        FileServiceManageConnector connector = connectorFactory.getConnector(fileServiceAddress);

        MachineList machines = connector.getMachineList();
        assertTrue(machines.getMachineCount()>0);
        Machine machine = machines.getMachines()[0];
        assertNotNull(machine);

        List list = new ArrayList();
        list.add(new VFtpCompatDownloadRequestForm("Fab1", machine.getMachineName(), "AABBCC_DDDD"));
        FileDownloadExecutor e = new FileDownloadExecutor("vftp-compat", "test", downloader, list, false);
        e.start();

        log.info("waiting");
        while(!e.getStatus().equals("complete")) {
            Thread.sleep(1000);
        }
        log.info("download complete");
        log.info("download path="+e.getDownloadPath());
    }


}