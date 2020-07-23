package jp.co.canon.ckbs.eec.fs.manage.service.configuration;

import lombok.Getter;
import lombok.Setter;

public class Machine {
    @Getter @Setter
    String name;

    @Getter @Setter
    String fabName;

    public Machine(String name, String fabName){
        this.name = name;
        this.fabName = fabName;
    }
}
