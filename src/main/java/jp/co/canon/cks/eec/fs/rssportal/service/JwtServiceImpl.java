package jp.co.canon.cks.eec.fs.rssportal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import jp.co.canon.cks.eec.fs.rssportal.model.error.ExpiredException;
import jp.co.canon.cks.eec.fs.rssportal.model.error.UnauthorizedException;
import jp.co.canon.cks.eec.fs.rssportal.model.auth.AccessToken;
import jp.co.canon.cks.eec.fs.rssportal.model.auth.RefreshToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

@Service("jwtService")
public class JwtServiceImpl implements JwtService{
    private final Log log = LogFactory.getLog(getClass());
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    @Value("${rssportal.jwt.salt}")
    private String SALT;
    @Value("${rssportal.jwt.accessTokenExp}")
    private long accessTokenExp;
    @Value("${rssportal.jwt.refreshTokenExp}")
    private long refreshTokenExp;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public <T> String create(T data, String subject){
        Date createdDate = new Date();
        Date expirationDate;

        if(data == null) {
            return null;
        }

        if(subject.equals("accessToken")) {
            expirationDate = new Date(createdDate.getTime() + accessTokenExp * 60 * 1000);
        } else if(subject.equals("refreshToken")) {
            expirationDate = new Date(createdDate.getTime() + (refreshTokenExp * 60 * 1000));
        } else {
            return null;
        }

        String jwt = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(objectMapper.convertValue(data, Map.class))
                .setSubject(subject)
                .setExpiration(expirationDate)
                .setIssuedAt(createdDate)
                .signWith(SignatureAlgorithm.HS256, this.generateKey())
                .compact();
        return jwt;
    }

    private byte[] generateKey(){
        byte[] key = null;

        try {
            key = SALT.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("[generateKey]: " + e);
        }

        return key;
    }

    @Override
    public boolean isUsable(String jwt) {
        if(jwt == null) {
            return false;
        }

        String claimsJws = jwt.replace(TOKEN_PREFIX, "");

        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(this.generateKey())
                    .parseClaimsJws(claimsJws);
            return true;
        } catch (Exception e) {
            log.error("[decodeAccessToken]: " + e);
            return false;
        }
    }

    @Override
    public String getCurrentAccessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String reqJwt = request.getHeader(HEADER_STRING);
        if(reqJwt == null || !reqJwt.startsWith(TOKEN_PREFIX)) {
            return null;
        }
        return reqJwt.replace(TOKEN_PREFIX, "");
    }

    @Override
    public String getCurAccTokenUserName() {
        String curAccessToken = getCurrentAccessToken();

        if (curAccessToken == null || curAccessToken.isEmpty()) {
            return null;
        }

        String claimsJws = curAccessToken.replace(TOKEN_PREFIX, "");
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(this.generateKey())
                    .parseClaimsJws(claimsJws);
            return (String) claims.getBody().get("userName");
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    @Override
    public int getCurAccTokenUserID() {
        String curAccessToken = getCurrentAccessToken();

        if (curAccessToken == null || curAccessToken.isEmpty()) {
            return 0;
        }

        String claimsJws = curAccessToken.replace(TOKEN_PREFIX, "");
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(this.generateKey())
                    .parseClaimsJws(claimsJws);
            return (int) claims.getBody().get("userId");
        } catch (Exception e) {
            log.error(e);
            return 0;
        }
    }

    @Override
    public AccessToken decodeAccessToken(String jwt) {
        String claimsJws = jwt.replace(TOKEN_PREFIX, "");
        log.info("[decodeAccessToken]claimsJws: " + claimsJws);
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(this.generateKey())
                    .parseClaimsJws(claimsJws);

            log.info(claims.getBody().getSubject());
            log.info(claims.getBody().getIssuedAt());
            log.info(claims.getBody().getExpiration());
            log.info((int)claims.getBody().get("userId"));
            log.info((String)claims.getBody().get("userName"));
            log.info((String)claims.getBody().get("permission"));

            AccessToken accessToken = new AccessToken();
            accessToken.setSub(claims.getBody().getSubject());
            accessToken.setIat(claims.getBody().getIssuedAt());
            accessToken.setExp(claims.getBody().getExpiration());
            accessToken.setUserId((int)claims.getBody().get("userId"));
            accessToken.setUserName((String)claims.getBody().get("userName"));
            accessToken.setPermission((String)claims.getBody().get("permission"));
            if(accessToken.getSub().equals("accessToken")) return accessToken;
        } catch (ExpiredJwtException e) {
            log.error("[decodeAccessToken]: " + e);
            throw new ExpiredException();
        } catch (Exception e) {
            log.error("[decodeAccessToken]: " + e);
            throw new UnauthorizedException();
        }
        return null;
    }

    @Override
    public RefreshToken decodeRefreshToken(String jwt) {
        String claimsJws = jwt.replace(TOKEN_PREFIX, "");
        log.info("[decodeRefreshToken]claimsJws: " + claimsJws);
        Jws<Claims> claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SALT.getBytes("UTF-8"))
                    .parseClaimsJws(claimsJws);
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setSub(claims.getBody().getSubject());
            refreshToken.setIat(claims.getBody().getIssuedAt());
            refreshToken.setExp(claims.getBody().getExpiration());
            refreshToken.setUserId((int)claims.getBody().get("userId"));
            refreshToken.setUserName((String)claims.getBody().get("userName"));
            if(refreshToken.getSub().equals("refreshToken")) return refreshToken;
        } catch (ExpiredJwtException e) {
            log.error("[decodeRefreshToken]: " + e);
            throw new ExpiredException();
        } catch (Exception e) {
            log.error("[decodeRefreshToken]: " + e);
            throw new UnauthorizedException();
        }
        return null;
    }
}