package jp.co.canon.ckbs.eec.fs.collect.service.configuration;

import lombok.Getter;
import lombok.Setter;

public class FtpServerInfo {
    @Getter
    String host;

    @Getter @Setter
    int port = 21;

    @Getter @Setter
    String user;

    @Getter @Setter
    String password;

    @Setter
    String ftpmode;

    public void setHost(String host){
        int index = host.indexOf(":");
        if (index >= 0){
            String portStr = host.substring(index+1);
            this.host = host.substring(0, index);
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e){

            }
        }
    }

    public String getFtpmode(){
        if (ftpmode != null && ftpmode.compareToIgnoreCase("active") == 0){
            return "active";
        }
        return "passive";
    }
}
