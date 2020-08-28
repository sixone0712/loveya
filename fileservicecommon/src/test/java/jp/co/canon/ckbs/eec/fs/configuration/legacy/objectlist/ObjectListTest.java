package jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class ObjectListTest {
    @Test
    void test_001(){
        File file = new File("/usr/local/canon/esp/CanonFileService/definitions/ObjectList.xml");
        ObjectList objectList = new ObjectList(file);

        FileService fileService = objectList.findFileServiceById("__");
        Assertions.assertNull(fileService);

        objectList.load();

        Tool tool = objectList.getTool("___");
        Assertions.assertNull(tool);
        tool = objectList.getTool("MPA_1");
        Assertions.assertNotNull(tool);

        fileService = objectList.getFileServiceByToolName("MPA_1");
        Assertions.assertNotNull(fileService);

        FileService[] fileServices = objectList.getAllFileService();
        Assertions.assertNotNull(fileServices);
    }

    @Test
    void test_002(){
        File file = new File("/usr/local/canon/esp/CanonFileService/definitions/ObjectList.xml");
        ObjectList objectList = new ObjectList(file);

        List<Tool> toolList = objectList.getToolList();
        Assertions.assertNotNull(toolList);
        List<Ots> otsList = objectList.getOtsList();
        Assertions.assertNotNull(otsList);
        List<Common> commonList = objectList.getCommonList();
        Assertions.assertNotNull(commonList);
        List<FileService> fileServiceList = objectList.getFileServiceList();
        Assertions.assertNotNull(fileServiceList);
    }
}
