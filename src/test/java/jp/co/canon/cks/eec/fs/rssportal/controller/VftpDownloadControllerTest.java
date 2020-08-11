package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpSssListRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.service.VFtpFileInfo;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnector;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnectorFactory;
import jp.co.canon.ckbs.eec.fs.manage.service.MachineList;
import jp.co.canon.ckbs.eec.fs.manage.service.configuration.Machine;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadStatusResponseBody;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VftpDownloadControllerTest {

    private Log log = LogFactory.getLog(getClass());

    @Autowired
    private VftpDownloadController controller;

    @Autowired
    private FileServiceManageConnectorFactory connectorFactory;

    @Value("${rssportal.file-service-manager.addr}")
    private String fileServiceAddress;

    @Test
    void requestVFtpCompat1() throws InterruptedException {
        log.info("requestVFtpCompat1");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/api/vftp/compat/download");

        assertNotNull(connectorFactory);
        assertNotNull(fileServiceAddress);
        FileServiceManageConnector connector = connectorFactory.getConnector(fileServiceAddress);

        MachineList machines = connector.getMachineList();
        assertTrue(machines.getMachineCount()>0);

        Map<String, Object> param = new HashMap<>();
        List<String> machineNames = new ArrayList<>();
        List<String> fabNames = new ArrayList<>();

        Machine[] _machines = machines.getMachines();
        for(int i=0; i<_machines.length&&i<4; ++i) {
            machineNames.add(_machines[i].getMachineName());
            fabNames.add(_machines[i].getFabName());
        }
        param.put("fabNames", fabNames);
        param.put("machineNames", machineNames);
        param.put("command", "");

        ResponseEntity response = controller.vftpCompatDownloadRequest(request, param);
        assertNotNull(response);
        assertTrue(response.getStatusCode()==HttpStatus.OK);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        String downloadId = (String) body.get("downloadId");
        log.info("downloadId="+downloadId);

        request.setServletPath("/rss/api/vftp/compat/download/"+downloadId);
        while(true) {
            Thread.sleep(3000);
            ResponseEntity<DownloadStatusResponseBody> resp =
                    (ResponseEntity<DownloadStatusResponseBody>) controller.vftpCompatDownloadStatus(request, downloadId);
            DownloadStatusResponseBody status = resp.getBody();
            if(status.getStatus().equals("done")) {
                log.info("download done");
                log.info("download url="+status.getDownloadUrl());
                break;
            }
            log.info("downloading... ");
        }
    }

    @Test
    void requestVFtpSss1() throws InterruptedException {
        log.info("requestVFtpSss1");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/rss/api/vftp/sss/download");

        assertNotNull(connectorFactory);
        assertNotNull(fileServiceAddress);
        FileServiceManageConnector connector = connectorFactory.getConnector(fileServiceAddress);

        MachineList machines = connector.getMachineList();
        assertTrue(machines.getMachineCount()>0);

        List<Map> lists = new ArrayList<>();

        Machine[] _machines = machines.getMachines();
        for(int i=0; i<_machines.length&&i<4; ++i) {
            String machine = _machines[i].getMachineName();
            String fab = _machines[i].getFabName();
            VFtpSssListRequestResponse listResponse = connector.createVFtpSssListRequest(machine, "IP_AS_RAW_ERR-DE_TEST_PR_2nd");
            VFtpFileInfo[] files = listResponse.getRequest().getFileList();
            for(VFtpFileInfo file: files) {
                Map<String, Object> f = new HashMap<>();
                f.put("fabName", fab);
                f.put("machineName", machine);
                f.put("command", "IP_AS_RAW_ERR-DE_TEST_PR_2nd");
                f.put("fileName", file.getFileName());
                f.put("fileSize", (int)file.getFileSize());
                lists.add(f);
            }
        }

        Map<String, Object> param = new HashMap<>();
        param.put("lists", lists);

        ResponseEntity response = controller.vftpSSSDownloadRequest(request, param);
        assertNotNull(response);
        assertTrue(response.getStatusCode()==HttpStatus.OK);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        String downloadId = (String) body.get("downloadId");
        log.info("downloadId="+downloadId);

        request.setServletPath("/rss/api/vftp/sss/download/"+downloadId);
        while(true) {
            Thread.sleep(3000);
            ResponseEntity<DownloadStatusResponseBody> resp =
                    (ResponseEntity<DownloadStatusResponseBody>) controller.vftpCompatDownloadStatus(request, downloadId);
            DownloadStatusResponseBody status = resp.getBody();
            if(status.getStatus().equals("done")) {
                log.info("download done");
                log.info("download url="+status.getDownloadUrl());
                break;
            }
            log.info("downloading... ");
        }
    }
}