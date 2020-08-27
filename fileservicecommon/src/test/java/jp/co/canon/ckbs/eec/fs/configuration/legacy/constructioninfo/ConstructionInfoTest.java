package jp.co.canon.ckbs.eec.fs.configuration.legacy.constructioninfo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class ConstructionInfoTest {
    @Test
    void test_001(){
        File file = new File("/usr/local/canon/esp/CanonFileService/ConstructionInfo.xml");
        ConstructionInfo constructionInfo = new ConstructionInfo(file);
    }

    @Test
    void test_002(){
        File file = new File("/usr/local/canon/esp/ConstructionService/ConstructionInfo.xml");
        ConstructionInfo constructionInfo = new ConstructionInfo(file);

        Ots[] otsList = constructionInfo.getAllOts();
        Assertions.assertNotNull(otsList);

        Ots ots = constructionInfo.getEquipmentOts("MPA_1");
        Assertions.assertNotNull(ots);

        ots = constructionInfo.getEquipmentOts("__");
        Assertions.assertNull(ots);

        Equipment[] equipmentList = constructionInfo.getEquipmentList();
        Assertions.assertNotNull(equipmentList);
    }
}
