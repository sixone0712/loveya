package jp.co.canon.cks.eec.fs.rssportal.background.autocollect;

import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnector;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnectorFactory;
import jp.co.canon.ckbs.eec.fs.manage.service.MachineList;
import jp.co.canon.ckbs.eec.fs.manage.service.configuration.Machine;
import jp.co.canon.cks.eec.fs.rssportal.common.Tool;
import jp.co.canon.cks.eec.fs.rssportal.service.CollectPlanService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlanManagerTest {

    private Log log = LogFactory.getLog(getClass());

    @Autowired
    private CollectPlanService service;

    @Autowired
    private FileServiceManageConnectorFactory connectorFactory;

    @Value("${rssportal.file-service-manager.addr}")
    private String fileServiceAddress;

    @Test
    @Timeout(10)
    void addVFtpCompatPlan1() {
        log.info("addVFtpCompatPlan1");
        assertNotNull(service);

        assertNotNull(connectorFactory);
        assertNotNull(fileServiceAddress);
        FileServiceManageConnector connector = connectorFactory.getConnector(fileServiceAddress);

        MachineList machines = connector.getMachineList();
        assertTrue(machines.getMachineCount()>0);

        List<String> machineNames = new ArrayList<>();
        List<String> fabNames = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        for(int i=0; i<3 && i<machines.getMachines().length; ++i) {
            Machine machine = machines.getMachines()[i];
            machineNames.add(machine.getMachineName());
            fabNames.add(machine.getFabName());
        }
        commands.add("");
        log.info("commands="+commands.size());

        SimpleDateFormat dateFormat = Tool.getVFtpSimpleDateFormat();
        Date start, end;
        try {
            start = dateFormat.parse("20200810_000000");
            end = dateFormat.parse("20200810_235959");
        } catch (ParseException e) {
            assertNotNull(null);
            return;
        }

        int planId = service.addPlan("vftp_compat", 10000, "vftp-compat-test", fabNames, machineNames,
                commands, start, start, end, "continuous", 1000, "vftp-compat-test");
        assertNotEquals(planId, -1);
    }

    @Test
    @Timeout(10)
    void addVFtpSssPlan1() {
        log.info("addVFtpCompatPlan1");
        assertNotNull(service);

        assertNotNull(connectorFactory);
        assertNotNull(fileServiceAddress);
        FileServiceManageConnector connector = connectorFactory.getConnector(fileServiceAddress);

        MachineList machines = connector.getMachineList();
        assertTrue(machines.getMachineCount()>0);

        List<String> machineNames = new ArrayList<>();
        List<String> fabNames = new ArrayList<>();
        List<String> directories = new ArrayList<>();

        for(int i=0; i<3 && i<machines.getMachines().length; ++i) {
            Machine machine = machines.getMachines()[i];
            machineNames.add(machine.getMachineName());
            fabNames.add(machine.getFabName());
        }
        directories.add("IP_AS_RAW");
        directories.add("IP_AS");

        SimpleDateFormat dateFormat = Tool.getVFtpSimpleDateFormat();
        Date start, end;
        try {
            start = dateFormat.parse("20200810_000000");
            end = dateFormat.parse("20200810_235959");
        } catch (ParseException e) {
            assertNotNull(null);
            return;
        }

        int planId = service.addPlan("vftp_sss", 10000, "vftp-sss-test", fabNames, machineNames,
                directories, start, start, end, "continuous", 1000, "vftp-sss-test");
        assertNotEquals(planId, -1);
    }
}