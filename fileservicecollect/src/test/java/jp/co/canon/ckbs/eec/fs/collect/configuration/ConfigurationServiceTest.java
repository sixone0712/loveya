package jp.co.canon.ckbs.eec.fs.collect.configuration;

import jp.co.canon.ckbs.eec.fs.collect.service.configuration.ConfigurationService;
import jp.co.canon.ckbs.eec.fs.collect.service.configuration.FtpServerInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ConfigurationServiceTest {
    @Autowired
    ConfigurationService configurationService;

    @Test
    void test_001(){
        FtpServerInfo ftpServerInfo = configurationService.getFtpServerInfo("MPA_1");
        System.out.println(ftpServerInfo.getHost());
    }
}
