package jp.co.canon.ckbs.eec.fs.collect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultFileServiceCollectConnectorTest {
    FileServiceCollectConnectorFactory connectorFactory = new DefaultFileServiceCollectConnectorFactory();

    @Test
    void test_001(){
        FileServiceCollectConnector connector = null;
        connector = connectorFactory.getConnector("10.1.36.118");
        Assertions.assertNotNull(connector);
    }
}
