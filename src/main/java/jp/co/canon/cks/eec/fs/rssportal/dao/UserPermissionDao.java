package jp.co.canon.cks.eec.fs.rssportal.dao;

import jp.co.canon.cks.eec.fs.rssportal.vo.UserPermissionVo;

import java.util.List;

public interface UserPermissionDao {

    List<UserPermissionVo> getPermissions();
    UserPermissionVo getPermission(int id);
    void setPermission(String permname);
    void updatePermission(UserPermissionVo perm);
}
