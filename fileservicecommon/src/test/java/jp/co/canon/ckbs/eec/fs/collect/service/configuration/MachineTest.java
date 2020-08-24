package jp.co.canon.ckbs.eec.fs.collect.service.configuration;

import jp.co.canon.ckbs.eec.fs.manage.service.configuration.Machine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MachineTest {
    @Test
    void test_001(){
        Machine machine = new Machine("MPA_1", "Fab1");
        Assertions.assertEquals("MPA_1", machine.getMachineName());
        Assertions.assertEquals("Fab1", machine.getFabName());
    }

    @Test
    void test_002(){
        Machine machine = new Machine();
        machine.setMachineName("MPA_2");
        Assertions.assertEquals("MPA_2", machine.getMachineName());
        machine.setFabName("Fab2");
        Assertions.assertEquals("Fab2", machine.getFabName());
    }
}
