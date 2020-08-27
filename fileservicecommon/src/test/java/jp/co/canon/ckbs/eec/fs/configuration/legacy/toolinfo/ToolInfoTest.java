package jp.co.canon.ckbs.eec.fs.configuration.legacy.toolinfo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ToolInfoTest {
    @Test
    void test_001(){
        ToolInfo toolInfo= new ToolInfo();
        toolInfo.setName("MPA_1");
        Assertions.assertEquals("MPA_1", toolInfo.getName());

        toolInfo.setVersion("001");
        Assertions.assertEquals("001", toolInfo.getVersion());

        toolInfo.addFtpInfo(null);
        toolInfo.addLogData(null);

        toolInfo.addFtpInfo(new FtpInfo());
        toolInfo.addLogData(new LogData());
    }
}
