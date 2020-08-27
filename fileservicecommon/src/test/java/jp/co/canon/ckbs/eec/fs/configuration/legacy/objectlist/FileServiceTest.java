package jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileServiceTest {
    @Test
    void test_001(){
        FileService fileService = new FileService("id", "name", "host", "structid");
        Assertions.assertEquals("id", fileService.getId());
        Assertions.assertEquals("name", fileService.getName());
        Assertions.assertEquals("host", fileService.getHost());
        Assertions.assertEquals("structid", fileService.getStructId());

        fileService.setId("id2");
        fileService.setName("name2");
        fileService.setHost("host2");
        fileService.setStructId("structid2");

        Assertions.assertEquals("id2", fileService.getId());
        Assertions.assertEquals("name2", fileService.getName());
        Assertions.assertEquals("host2", fileService.getHost());
        Assertions.assertEquals("structid2", fileService.getStructId());

        NetworkDL dl = new NetworkDL("root", "password", "ftp://", "passive");

        fileService.setNetworkDL(dl);
        Assertions.assertEquals(dl, fileService.getNetworkDL());
    }
}
