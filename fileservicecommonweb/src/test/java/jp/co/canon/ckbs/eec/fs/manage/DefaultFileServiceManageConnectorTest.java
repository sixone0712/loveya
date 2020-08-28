package jp.co.canon.ckbs.eec.fs.manage;

import jp.co.canon.ckbs.eec.fs.collect.service.LogFileList;
import jp.co.canon.ckbs.eec.fs.manage.service.CategoryList;
import jp.co.canon.ckbs.eec.fs.manage.service.MachineList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultFileServiceManageConnectorTest {
    FileServiceManageConnectorFactory connectorFactory = new DefaultFileServiceManageConnectorFactory();
    @Test
    void test_001(){
        FileServiceManageConnector connector = connectorFactory.getConnector("10.1.31.217");
        Assertions.assertNotNull(connector);

        MachineList machineList = connector.getMachineList();

        CategoryList categoryList = connector.getCategoryList();

        categoryList = connector.getCategoryList("MPA_1");

        LogFileList logFileList = connector.getFtpFileList("MPA_1", "003", "20200827000000", "20200827120000", null, null);


    }
}
