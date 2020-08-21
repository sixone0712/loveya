package jp.co.canon.cks.eec.fs.rssportal.service;

import jp.co.canon.cks.eec.fs.rssportal.model.auth.AccessToken;
import jp.co.canon.cks.eec.fs.rssportal.model.auth.RefreshToken;

public interface JwtService {
    <T> String create(T data, String subject);
    String getCurrentAccessToken();
    String getCurAccTokenUserName();
    int getCurAccTokenUserID();
    String getCurAccTokenUserPermission();
    AccessToken decodeAccessToken(String jwt);
    RefreshToken decodeRefreshToken(String jwt);
    boolean isUsable(String jwt);
}
