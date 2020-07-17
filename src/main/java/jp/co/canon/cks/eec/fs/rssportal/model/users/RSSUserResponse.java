package jp.co.canon.cks.eec.fs.rssportal.model.users;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RSSUserResponse {
    private int userId;
    private String userName;
    private String permission;
    private String created;
    private String modified;
    private String lastAccess;
}
