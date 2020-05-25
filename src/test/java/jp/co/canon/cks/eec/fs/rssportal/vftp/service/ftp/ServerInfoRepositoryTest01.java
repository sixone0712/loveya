package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import jp.co.canon.cks.eec.fs.rssportal.vftp.ServerInfoRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("vftptest")
public class ServerInfoRepositoryTest01 {
    @Autowired
    ServerInfoRepository serverInfoRepository;
    
    @Test
    public void test_001(){
        ServerInfo[] serverInfos = serverInfoRepository.getAllServerInfos();
        for (ServerInfo info : serverInfos){
            System.out.println(info.getName());
        }

        String serverName;
        serverName = serverInfoRepository.getServerNameByDevice("MPA_1");
        Assertions.assertEquals("OTS01_FS", serverName);

        serverName = serverInfoRepository.getServerNameByDevice("MPA_2");
        Assertions.assertEquals("OTS01_FS", serverName);

        serverName = serverInfoRepository.getServerNameByDevice("MPA_3");
        Assertions.assertEquals("OTS02_FS", serverName);

        serverName = serverInfoRepository.getServerNameByDevice("MPA_4");
        Assertions.assertEquals("OTS02_FS", serverName);

        serverName = serverInfoRepository.getServerNameByDevice("MPA_5");
        Assertions.assertEquals("OTS02_FS", serverName);

        serverName = serverInfoRepository.getServerNameByDevice("MPA_6");
        Assertions.assertEquals("OTS02_FS", serverName);

        serverName = serverInfoRepository.getServerNameByDevice("AAAA");
        Assertions.assertNull(serverName);
    } 
}