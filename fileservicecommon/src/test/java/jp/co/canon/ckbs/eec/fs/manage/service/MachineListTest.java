package jp.co.canon.ckbs.eec.fs.manage.service;

import jp.co.canon.ckbs.eec.fs.manage.service.configuration.Machine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MachineListTest {
    @Test
    void test_001(){
        MachineList ml = new MachineList();

        Assertions.assertEquals(0, ml.getMachineCount());

        ArrayList<Machine> machineArray = new ArrayList<>();
        machineArray.add(new Machine("MPA_1", "Fab1"));
        machineArray.add(new Machine("MPA_2", "Fab2"));

        ml.setMachines(machineArray.toArray(new Machine[0]));

        Assertions.assertEquals(2, ml.getMachineCount());

        Assertions.assertArrayEquals(machineArray.toArray(new Machine[0]), ml.getMachines());
    }
}
