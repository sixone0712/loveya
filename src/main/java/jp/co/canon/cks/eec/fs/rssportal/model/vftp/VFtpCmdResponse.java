package jp.co.canon.cks.eec.fs.rssportal.model.vftp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VFtpCmdResponse {
    private int id;
    private String cmd_name;
    private String cmd_type;
    private String created;
    private String modified;
    //private boolean validity;
}
