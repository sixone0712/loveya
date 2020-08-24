package jp.co.canon.ckbs.eec.fs.manage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultFileServiceManageConnectorTest {
    FileServiceManageConnectorFactory connectorFactory = new DefaultFileServiceManageConnectorFactory();
    @Test
    void test_001(){
        FileServiceManageConnector connector = connectorFactory.getConnector("10.1.36.118");
        Assertions.assertNotNull(connector);
    }
}
