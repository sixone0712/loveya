package jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist;

import lombok.Getter;
import lombok.Setter;

public class Common {
    @Getter @Setter
    String name;

    @Getter @Setter
    String structId;

    @Getter @Setter
    String collectFsId;

    public Common(String name, String structId, String collectFsId){
        this.name = name;
        this.structId = structId;
        this.collectFsId = collectFsId;
    }
}
