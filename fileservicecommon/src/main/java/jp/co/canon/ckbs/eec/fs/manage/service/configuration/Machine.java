package jp.co.canon.ckbs.eec.fs.manage.service.configuration;

import lombok.Getter;
import lombok.Setter;

public class Machine {
    @Getter @Setter
    String machineName;

    @Getter @Setter
    String fabName;

    public Machine(String machineName, String fabName){
        this.machineName = machineName;
        this.fabName = fabName;
    }
}
