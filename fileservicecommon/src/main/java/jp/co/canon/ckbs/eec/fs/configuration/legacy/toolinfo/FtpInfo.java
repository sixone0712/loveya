package jp.co.canon.ckbs.eec.fs.configuration.legacy.toolinfo;

import lombok.Getter;
import lombok.Setter;

public class FtpInfo {
    @Getter @Setter
    String id;

    @Getter @Setter
    int port;

    @Getter @Setter
    String user;

    @Getter @Setter
    String password;
}
