package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerInfo {
    private String name;
    private String host;
    private int port = 22001;
    private String username;
    private String password;
    private String ftpMode;
    
    public boolean equals(ServerInfo dst){
        if (!this.name.equals(dst.name)){
            return false;
        }
        if (!this.host.equals(dst.host)){
            return false;
        }
        if (this.port != dst.port){
            return false;
        }
        if (!this.username.equals(dst.username)){
            return false;
        }
        if (!this.password.equals(dst.password)){
            return false;
        }

        return true;
    }

}