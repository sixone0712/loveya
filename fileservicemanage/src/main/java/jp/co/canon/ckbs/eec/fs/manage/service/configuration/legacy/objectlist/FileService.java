package jp.co.canon.ckbs.eec.fs.manage.service.configuration.legacy.objectlist;

import lombok.Getter;
import lombok.Setter;

public class FileService {
    @Getter @Setter
    String id;

    @Getter @Setter
    String name;

    @Getter @Setter
    String host;

    @Getter @Setter
    String structId;

    @Getter @Setter
    NetworkDL networkDL;

    public FileService(String id, String name, String host, String structId){
        this.id = id;
        this.name = name;
        this.host = host;
        this.structId = structId;
    }

}
