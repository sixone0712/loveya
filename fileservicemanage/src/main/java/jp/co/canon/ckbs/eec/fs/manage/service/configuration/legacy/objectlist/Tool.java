package jp.co.canon.ckbs.eec.fs.manage.service.configuration.legacy.objectlist;

import lombok.Getter;
import lombok.Setter;

public class Tool {
    @Getter @Setter
    String name;

    @Getter @Setter
    String toolType;

    @Getter @Setter
    String structId;

    @Getter @Setter
    String collectFsId;

    public Tool(String name, String toolType, String structId, String collectFsId){
        this.name = name;
        this.toolType = toolType;
        this.structId = structId;
        this.collectFsId = collectFsId;
    }
}
