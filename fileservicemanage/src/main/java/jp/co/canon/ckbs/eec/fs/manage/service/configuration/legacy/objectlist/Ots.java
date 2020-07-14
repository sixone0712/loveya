package jp.co.canon.ckbs.eec.fs.manage.service.configuration.legacy.objectlist;

import lombok.Getter;
import lombok.Setter;

public class Ots {
    @Getter @Setter
    String collectFsId;

    @Getter @Setter
    String name;

    @Getter @Setter
    String structId;

    public Ots(String name, String structId, String collectFsId){
        this.name = name;
        this.structId = structId;
        this.collectFsId = collectFsId;
    }
}
