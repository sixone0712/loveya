package jp.co.canon.cks.eec.fs.rssportal.controller;

import jp.co.canon.cks.eec.fs.rssportal.Defines.RSSErrorReason;
import jp.co.canon.cks.eec.fs.rssportal.model.auth.AccessToken;
import jp.co.canon.cks.eec.fs.rssportal.model.auth.RefreshToken;
import jp.co.canon.cks.eec.fs.rssportal.model.error.RSSError;
import jp.co.canon.cks.eec.fs.rssportal.service.UserService;
import jp.co.canon.cks.eec.fs.rssportal.service.JwtService;
import jp.co.canon.cks.eec.fs.rssportal.session.SessionContext;
import jp.co.canon.cks.eec.fs.rssportal.vo.UserVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rss/api/auths")
public class LoginController {
    private final HttpSession httpSession;
    private final UserService serviceUser;
    private final JwtService jwtService;

    @Value("${rssportal.jwt.autoRefresh}")
    private long autoRefresh;

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    public LoginController(HttpSession httpSession, UserService serviceUser, JwtService jwtService) {
        this.httpSession = httpSession;
        this.serviceUser = serviceUser;
        this.jwtService = jwtService;
    }

    @GetMapping("/me")
    @ResponseBody
    public ResponseEntity<?> isLogin(HttpServletRequest request)  throws Exception {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        try {
            AccessToken decodedAccess = jwtService.decodeAccessToken(request.getHeader(HEADER_STRING));
            resBody.put("userId", decodedAccess.getUserId());
            resBody.put("userName", decodedAccess.getUserName());
            resBody.put("permission", decodedAccess.getPermission());
            return ResponseEntity.status(HttpStatus.OK).body(resBody);
        } catch (Exception e) {
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }
    }

    @GetMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(HttpServletRequest request,
                                   @RequestParam(name="username", required = false, defaultValue = "") String username,
                                   @RequestParam(name="password", required = false, defaultValue = "") String password)  throws Exception {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();;

        if(username == null|| username.equals("") || password == null || password.equals("")) {
            error.setReason(RSSErrorReason.INVALID_PARAMETER);
            resBody.put("error", error.getRSSError());
            log.error(String.format("[Get] %s : %s", request.getServletPath(), "Invalid string value: username or password is empty."));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
        }

        int userId = serviceUser.verify(username, password);
        if(userId >= 10000) {
            SessionContext context = new SessionContext();
            UserVo LoginUser = serviceUser.getUser(userId);
            serviceUser.UpdateLastAccessTime(userId);
            context.setUser(LoginUser);
            context.setAuthorized(true);
            httpSession.setAttribute("context", context);
            resBody.put("userName", LoginUser.getUsername());
            resBody.put("userId", LoginUser.getId());
            resBody.put("permission", LoginUser.getPermissions());

            AccessToken accessTokenInfo = new AccessToken();
            accessTokenInfo.setUserId(LoginUser.getId());
            accessTokenInfo.setUserName(LoginUser.getUsername());
            accessTokenInfo.setPermission(LoginUser.getPermissions());
            String accessToken = jwtService.create(accessTokenInfo, "accessToken");
            resBody.put("accessToken", accessToken);

            boolean reissueToken = false;
            String savedRefreshToken = LoginUser.getRefreshToken();

            if (savedRefreshToken == null || !jwtService.isUsable(savedRefreshToken)) {
                reissueToken = true;
            } else {
                RefreshToken decodedRefresh = jwtService.decodeRefreshToken(savedRefreshToken);
                long calculateDate = (decodedRefresh.getExp().getTime() - new Date().getTime()) / (1000 * 60 * autoRefresh);

                if (calculateDate < 1) {
                    reissueToken = true;
                    serviceUser.setToken(savedRefreshToken, decodedRefresh.getExp());
                }
            }

            if (reissueToken) {
                RefreshToken refreshTokenInfo = new RefreshToken();
                refreshTokenInfo.setUserId(LoginUser.getId());
                refreshTokenInfo.setUserName(LoginUser.getUsername());
                String refreshToken = jwtService.create(refreshTokenInfo, "refreshToken");
                serviceUser.updateRefreshToken(LoginUser.getId(), refreshToken);
                resBody.put("refreshToken", refreshToken);
            } else {
                resBody.put("refreshToken", savedRefreshToken);
            }
        }
        else {
            if(userId == 34) {
                error.setReason(RSSErrorReason.INVALID_PASSWORD);
                resBody.put("error", error.getRSSError());
                log.error(String.format("[Get] %s : %s", request.getServletPath(), "Password is incorrect."));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
            } else {
                error.setReason(RSSErrorReason.NOT_FOUND);
                resBody.put("error", error.getRSSError());
                log.error(String.format("[Get] %s : %s", request.getServletPath(), "User does not exist."));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @GetMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletRequest request)  throws Exception {
        log.info(String.format("[Get] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();
        try {
            String accessToken = request.getHeader(HEADER_STRING);
            if (jwtService.isUsable(accessToken)) {
                AccessToken decodedAccess = jwtService.decodeAccessToken(accessToken);
                serviceUser.setToken(accessToken.substring(TOKEN_PREFIX.length()), decodedAccess.getExp());
            }
            return ResponseEntity.status(HttpStatus.OK).body(resBody);
        } catch (Exception e) {
            error.setReason(RSSErrorReason.NOT_FOUND);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        }
    }

    @PostMapping("/token")
    @ResponseBody
    public ResponseEntity<?> reissueAccessToken(HttpServletRequest request,
                                                @RequestBody Map<String, Object> param)  throws Exception {
        log.info(String.format("[Post] %s", request.getServletPath()));
        Map<String, Object> resBody = new HashMap<>();
        RSSError error = new RSSError();

        String refreshToken = param.containsKey("refreshToken") ? (String) param.get("refreshToken") : null;

        if(refreshToken == null) {
            error.setReason(RSSErrorReason.INVALID_REFRESH_TOKEN);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resBody);
        }

        try {
            RefreshToken decodedRefresh = jwtService.decodeRefreshToken(refreshToken);
            UserVo userInfo = serviceUser.getUser(decodedRefresh.getUserId());
            AccessToken tokenInfo = new AccessToken();
            tokenInfo.setUserId(userInfo.getId());
            tokenInfo.setUserName(userInfo.getUsername());
            tokenInfo.setPermission(userInfo.getPermissions());

            String newAccessToken = jwtService.create(tokenInfo, "accessToken");
            resBody.put("accessToken", newAccessToken);
            return ResponseEntity.status(HttpStatus.OK).body(resBody);
        } catch (Exception e) {
            log.error(e);
            error.setReason(RSSErrorReason.INVALID_REFRESH_TOKEN);
            resBody.put("error", error.getRSSError());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resBody);
        }

    }
}
