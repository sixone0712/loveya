package jp.co.canon.ckbs.eec.fs.configuration.legacy.logcollectdefinition;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class LogCollectDefinitionListTest {
    @Test
    void test_001(){
        File file = new File("/usr/local/canon/esp/CanonFileService/definitions");
        LogCollectDefinitionList logCollectDefinitionList = new LogCollectDefinitionList(file);

        LogCollectDefinition def = logCollectDefinitionList.getLogCollectDefinition("MPA_1");
        Assertions.assertNotNull(def);

        def = logCollectDefinitionList.getLogCollectDefinition("___");
        Assertions.assertNull(def);
    }
}
