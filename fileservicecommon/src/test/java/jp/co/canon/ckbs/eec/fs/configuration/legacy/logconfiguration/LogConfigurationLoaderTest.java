package jp.co.canon.ckbs.eec.fs.configuration.legacy.logconfiguration;

import org.junit.jupiter.api.Test;

import java.io.File;

public class LogConfigurationLoaderTest {
    @Test
    void test_001(){
        File file = new File("/usr/local/canon/esp/CanonFileService/commands");
        LogConfigurationLoader loader = new LogConfigurationLoader(file);

        LogConfiguration conf = loader.loadLogConfiguration("aa", "eesp_data_CKBSTest_non", "001");

        conf = loader.loadLogConfiguration("aa", "eesp_data_CKBSTest_1.0.0", "001");
        conf.getCompoId();
        conf.getName();
        conf.getDescription();
        conf.getExecuteCommand();
    }
}
