package jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ToolTest {
    @Test
    void test_001(){
        Tool tool = new Tool("TOOL_01", "t01", "01", "001");
        Assertions.assertEquals("TOOL_01", tool.getName());
        Assertions.assertEquals("t01", tool.getToolType());
        Assertions.assertEquals("01", tool.getStructId());
        Assertions.assertEquals("001", tool.getCollectFsId());

        tool.setName("TOOL_02");
        Assertions.assertEquals("TOOL_02", tool.getName());
        tool.setToolType("t02");
        Assertions.assertEquals("t02", tool.getToolType());
        tool.setStructId("02");
        Assertions.assertEquals("02", tool.getStructId());
        tool.setCollectFsId("002");
        Assertions.assertEquals("002", tool.getCollectFsId());

    }
}
