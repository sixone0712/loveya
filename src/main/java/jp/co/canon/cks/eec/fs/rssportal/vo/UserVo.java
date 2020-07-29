package jp.co.canon.cks.eec.fs.rssportal.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class UserVo {

    private int id;
    private String username;
    private String password;
    private Date created;
    private Date modified;
    private Date lastAccess;
    private boolean validity;
    private String permissions;
    private String refreshToken;
}
