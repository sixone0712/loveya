package jp.co.canon.cks.eec.fs.rssportal.model.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class RefreshToken {
    private String sub;
    private Date exp;
    private Date iat;
    private int userId;
    private String userName;
}