package jp.co.canon.cks.eec.fs.rssportal.vo;

public class UserPermissionVo {

    private int id;
    private String permname;
    private boolean validity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPermname() {
        return permname;
    }

    public void setPermname(String permname) {
        this.permname = permname;
    }

    public boolean isValidity() {
        return validity;
    }

    public void setValidity(boolean validity) {
        this.validity = validity;
    }

    @Override
    public String toString() {
        return "UserPermissionVo{" +
                "id=" + id +
                ", permission='" + permname + '\'' +
                ", validity=" + validity +
                '}';
    }
}
