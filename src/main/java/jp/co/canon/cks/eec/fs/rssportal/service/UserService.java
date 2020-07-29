package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.springframework.lang.NonNull;

import java.util.List;

public interface UserService {

    List<UserVo> getUserList();
    UserVo getUser(int id);
    UserVo getUser(String username);
/*    UserVo getUserById(String id);*/
    boolean addUser(UserVo user);
    boolean modifyUser(UserVo user);
    boolean deleteUser(UserVo user);
    /*boolean modifyPerm(int id, int[] perms);*/
    int verify(String username, String password);
    boolean UpdateLastAccessTime(int id);
    boolean updateRefreshToken(int id, String token);
    boolean getToken(String token);
    boolean setToken(String token);
    boolean cleanBlacklist();
}
