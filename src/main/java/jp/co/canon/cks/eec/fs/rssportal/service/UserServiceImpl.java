package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.dao.UserDao;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao dao;

    @Autowired
    public UserServiceImpl(UserDao dao) {
        this.dao = dao;
    }

    @Override
    public List<UserVo> getUserList() {
        return dao.findAll();
    }

    @Override
    public UserVo getUser(int id) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        return dao.find(param);
    }


    @Override
    public UserVo getUser(@NonNull String username) {
        Map<String, Object> param = new HashMap<>();
        param.put("username", username);
        return dao.find(param);
    }
   /* @Override
    public UserVo getUserById(@NonNull String id) {
        Map<String, Object> param = new HashMap<>();
        param.put("id",  Integer.parseInt(id));
        return dao.find(param);
    }*/

    @Override
    public boolean addUser(@NonNull UserVo user) {
        if(user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            return false;
        }
        return dao.add(user);
    }

    @Override
    public boolean modifyUser(@NonNull UserVo user) {
        UserVo temp = getUser(user.getId());
        if(temp==null) {
            return false;
        }
        return dao.modify(user);
    }

/*    @Override
    public boolean modifyPerm(int id, int[] perms) {
        UserVo temp = getUser(id);
        if(temp==null) {
            return false;
        }
        return false;
    }*/

    @Override
    public int verify(@NonNull String username, @NonNull String password) {
        UserVo user = getUser(username);
        if(user==null) {
            return 33; // LOGIN_FAIL_NO_REGISTER_USER
        }
        if(user.getPassword().equals(password)) {
            return user.getId();
        }
        return 34; // LOGIN_FAIL_INCORRECT_PASSWORD
    }

    @Override
    public boolean deleteUser(@NonNull UserVo user) {
        UserVo temp = getUser(user.getId());
        if(temp==null) {
            return false;
        }
        return dao.delete(user);
    }
    @Override
    public boolean UpdateLastAccessTime(int id) {
        Map<String, Object> param = new HashMap<>();
        UserVo temp = getUser(id);
        param.put("id", id);
        if(temp==null) {
            return false;
        }
        else
        {
            return  dao.UpdateAccessDate(param);
        }
    }

    @Override
    public boolean updateRefreshToken(int id, String token) {
        Map<String, Object> param = new HashMap<>();
        UserVo temp = getUser(id);

        if (temp == null) {
            return false;
        } else {
            param.put("id", id);
            param.put("token", token);

            return dao.updateRefreshToken(param);
        }
    }

    @Override
    public boolean getToken(String token) { return dao.getToken(token); }

    @Override
    public boolean setToken(String token) {
        return dao.setToken(token);
    }

    @Override
    public boolean cleanBlacklist() { return dao.cleanBlacklist(); }
}
