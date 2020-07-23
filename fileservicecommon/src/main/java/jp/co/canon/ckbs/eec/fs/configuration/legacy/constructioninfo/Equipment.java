package jp.co.canon.ckbs.eec.fs.configuration.legacy.constructioninfo;

import lombok.Getter;
import lombok.Setter;

public class Equipment {
    @Getter @Setter
    String host;

    @Getter @Setter
    String id;

    @Getter @Setter
    String name;

    @Getter @Setter
    String version;

    @Getter @Setter
    String ftpmode;

    @Getter @Setter
    String fabName;

    @Getter @Setter
    String otsName;
}
