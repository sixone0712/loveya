package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import jp.co.canon.cks.eec.fs.rssportal.vftp.ServerInfoRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("vftptest2")
public class ServerInfoRepositoryTest02 {
    @Autowired
    ServerInfoRepository serverInfoRepository;

    @Test
    public void test_001(){
        ServerInfo serverInfo = serverInfoRepository.getServerInfoByName("OTS01_FS");
        Assertions.assertNotNull(serverInfo);
    }
    
}