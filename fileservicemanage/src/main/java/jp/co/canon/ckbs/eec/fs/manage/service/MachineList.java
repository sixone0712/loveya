package jp.co.canon.ckbs.eec.fs.manage.service;

import jp.co.canon.ckbs.eec.fs.manage.service.configuration.Machine;
import lombok.Getter;
import lombok.Setter;

public class MachineList {
    @Getter @Setter
    Machine[] machines;

    public int getMachineCount(){
        if (machines == null){
            return 0;
        }
        return machines.length;
    }
}
