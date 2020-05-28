package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.list.ListRequest;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.list.ListRequestRepository;

@SpringBootTest
@Profile("vftptest3")
public class ListRequestRepositoryTest {
    private static String host = "10.1.36.118";
    private static int port = 22001;

    @Autowired
    ListRequestRepository repository;

    private ServerInfo getServerInfo() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setName("OTS_01");
        serverInfo.setHost(host);
        serverInfo.setPort(port);
        serverInfo.setUsername("trkang");
        serverInfo.setPassword("1234");
        return serverInfo;
    }

    @Test
    public void test_listRequestRepository_001(){
        ListRequest listRequest1 = new ListRequest();
        ListRequest listRequest2 = new ListRequest();
        repository.save(listRequest1);
        repository.save(listRequest2);

        Assertions.assertNotEquals(listRequest1.getRequestNo(), listRequest2.getRequestNo());
        repository.delete(listRequest1.getRequestNo());
        repository.delete(listRequest2.getRequestNo());
    }

    @Test
    public void test_listRequestRepository_002(){
        File dir = new File(repository.getRootDir());        
        repository.delete("LIST_NOT_EXISTS_DIR");
        File requestDir = new File(dir, "LIST_EXISTS_DIR");
        requestDir.mkdirs();
        repository.delete("LIST_EXISTS_DIR");
    }

/*
    @Test
    public void test_002() {
        ListRequest req = repository.getRequestById("TMP_LIST_REQUEST");

        System.out.println(req.getPath());
        System.out.println(req.getDirectory());
        // System.out.println(req.getCreatedTime());

        // repository.delete(req.getRequestNo());
    }

    @Test
    public void test_003() {
        ServerInfo[] serverInfos = new ServerInfo[] { getServerInfo() };

        ListRequest request = new ListRequest();
        request.setPath("/VROOT/SSS/Optional");
        request.setDirectory("AAAA");
        for(ServerInfo serverInfo : serverInfos){
            request.add(serverInfo);
        }
        request.execute();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    */
}