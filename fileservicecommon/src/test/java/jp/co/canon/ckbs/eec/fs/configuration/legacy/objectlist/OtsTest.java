package jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OtsTest {
    @Test
    void test_001(){
        Ots ots = new Ots("OTS_01", "01", "001");
        Assertions.assertEquals("OTS_01", ots.getName());
        Assertions.assertEquals("01", ots.getStructId());
        Assertions.assertEquals("001", ots.getCollectFsId());

        ots.setName("OTS_02");
        Assertions.assertEquals("OTS_02", ots.getName());
        ots.setStructId("02");
        Assertions.assertEquals("02", ots.getStructId());
        ots.setCollectFsId("002");
        Assertions.assertEquals("002", ots.getCollectFsId());
    }
}
