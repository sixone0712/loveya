package jp.co.canon.cks.eec.fs.rssportal.session;

import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;

public class SessionContext {

    private String desc;
    private boolean authorized = false;
    private UserVo user;
    

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public UserVo getUser() {
        return user;
    }

    public void setUser(UserVo user) {
        this.user = user;
    }
}
