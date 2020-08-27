package jp.co.canon.ckbs.eec.fs.configuration.legacy.logcollectdefinition;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class LogCollectDefinitionTest {
    @Test
    void test_001(){
        File file = new File("/usr/local/canon/esp/CanonFileService/definitions/ConstructDisplay.xml");
        try {
            LogCollectDefinition logCollectDefinition = new LogCollectDefinition(file);

        } catch (Exception e){

        }

    }

    @Test
    void test_002(){
        File file = new File("/usr/local/canon/esp/CanonFileService/definitions/LogCollectDef_AAA.xml");
        try {
            LogCollectDefinition logCollectDefinition = new LogCollectDefinition(file);

        } catch (Exception e){

        }

    }

    @Test
    void test_003(){
        File file = new File("/usr/local/canon/esp/CanonFileService/definitions/LogCollectDef_MPA_1.xml");
        try {
            LogCollectDefinition logCollectDefinition = new LogCollectDefinition(file);
            Log[] logList = logCollectDefinition.getLogList();
            Assertions.assertNotNull(logList);
        } catch (Exception e){

        }
    }
}
