package jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommonTest {
    @Test
    void test_001(){
        Common common = new Common("COMMON_01", "01", "001");
        Assertions.assertEquals("COMMON_01", common.getName());
        Assertions.assertEquals("01", common.getStructId());
        Assertions.assertEquals("001", common.getCollectFsId());

        common.setName("OTS_02");
        Assertions.assertEquals("OTS_02", common.getName());
        common.setStructId("02");
        Assertions.assertEquals("02", common.getStructId());
        common.setCollectFsId("002");
        Assertions.assertEquals("002", common.getCollectFsId());

    }
}
