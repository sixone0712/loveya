package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.dao.UserPermissionDao;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserPermissionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    private final UserPermissionDao dao;

    @Autowired
    public UserPermissionServiceImpl(UserPermissionDao dao) {
        this.dao = dao;
    }

    @Override
    public List<UserPermissionVo> findAll() {
        return dao.getPermissions();
    }

    @Override
    public UserPermissionVo find(int id) {
        return dao.getPermission(id);
    }

    @Override
    public void add(@NonNull String name) {
        dao.setPermission(name);
    }

    @Override
    public boolean delete(int id) {
        UserPermissionVo perm = dao.getPermission(id);
        if(perm!=null) {
            return false;
        }
        perm.setValidity(false);
        dao.updatePermission(perm);
        return true;
    }

}
