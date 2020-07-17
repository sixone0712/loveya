package jp.co.canon.ckbs.eec.fs.configuration.legacy.logcollectdefinition;

import lombok.Getter;
import lombok.Setter;

public class Log {
    @Getter @Setter
    String kind;

    @Getter @Setter
    String user;

    @Getter @Setter
    String password;

    @Getter @Setter
    String ftpmode;

    @Getter @Setter
    String urls;
}
