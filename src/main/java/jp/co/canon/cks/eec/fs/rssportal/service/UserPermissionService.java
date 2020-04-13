package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.vo.UserPermissionVo;

import java.util.List;

public interface UserPermissionService {

    List<UserPermissionVo> findAll();
    UserPermissionVo find(int id);
    void add(String name);
    boolean delete(int id);

}
