package jp.co.canon.ckbs.eec.fs.manage.service.configuration.legacy.objectlist;

import lombok.Getter;
import lombok.Setter;

public class NetworkDL {
    @Getter @Setter
    String user;

    @Getter @Setter
    String password;

    @Getter @Setter
    String urlPrefix;

    public NetworkDL(String user, String password, String urlPrefix){
        this.user = user;
        this.password = password;
        this.urlPrefix = urlPrefix;
    }
}
