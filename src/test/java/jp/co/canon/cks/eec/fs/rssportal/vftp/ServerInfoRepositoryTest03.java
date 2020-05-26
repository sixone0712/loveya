package jp.co.canon.cks.eec.fs.rssportal.vftp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("vftptest")
public class ServerInfoRepositoryTest03 {
    @Test
    public void test_getset_OtsInfo(){
        OtsInfo otsInfo = new OtsInfo();
        otsInfo.setFsid("1");
        Assertions.assertEquals("1", otsInfo.getFsid());

        otsInfo.setName("n");
        Assertions.assertEquals("n", otsInfo.getName());

        otsInfo.setStructId("s");
        Assertions.assertEquals("s", otsInfo.getStructId());
    }

    @Test
    public void test_getset_DssInfo(){
        DssInfo dssInfo = new DssInfo();

        dssInfo.setFsid("1");
        Assertions.assertEquals("1", dssInfo.getFsid());

        dssInfo.setName("n");
        Assertions.assertEquals("n", dssInfo.getName());

        dssInfo.setStructId("s");
        Assertions.assertEquals("s", dssInfo.getStructId());
    }

    @Test
    public void test_getset_ToolInfo(){
        ToolInfo info = new ToolInfo();

        info.setFsid("1");
        Assertions.assertEquals("1", info.getFsid());

        info.setName("n");
        Assertions.assertEquals("n", info.getName());

        info.setStructId("s");
        Assertions.assertEquals("s", info.getStructId());

        info.setType("Atype");
        Assertions.assertEquals("Atype", info.getType());
    }

}