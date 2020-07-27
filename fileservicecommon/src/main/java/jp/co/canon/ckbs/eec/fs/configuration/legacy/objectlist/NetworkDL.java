package jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist;

import lombok.Getter;
import lombok.Setter;

public class NetworkDL {
    @Getter @Setter
    String user;

    @Getter @Setter
    String password;

    @Getter @Setter
    String urlPrefix;

    @Setter
    String ftpmode="passive";

    public NetworkDL(String user, String password, String urlPrefix, String ftpMode){
        this.user = user;
        this.password = password;
        this.urlPrefix = urlPrefix;
        this.ftpmode = ftpMode;
    }

    public String getFtpmode(){
        if (this.ftpmode != null && this.ftpmode.equalsIgnoreCase("active")){
            return "active";
        }
        return "passive";
    }
}
