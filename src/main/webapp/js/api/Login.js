import * as Define from "../define";
import md5 from "md5-hash";

export const setLoginInit= (props) => {
    const { loginActions } = props;
    loginActions.loginInitAllData();
};

export const getLoginIsLoggedIn = (props) => {
    const { loginInfo } = props;
    return loginInfo.toJS().isLoggedIn;
};

export const setLoginIsLoggedIn = (props, value) => {
    const { loginActions } = props;
    loginActions.loginSetIsLoggedIn(value);
};

export const getLoginUserName = (props) => {
    const { loginInfo } = props;
    return loginInfo.toJS().username;
};

export const setLoginUserName = (props, value) => {
    const { loginActions } = props;
    loginActions.loginSetUsername(value);
};

export const getLoginPassword = (props) => {
    const { loginInfo } = props;
    return loginInfo.toJS().password;
};

export const setLoginPassword = (props, value) => {
    const { loginActions } = props;
    loginActions.loginSetPassword(value);
};

export const getLoginAuth = (props) => {
    const { loginInfo } = props;
    return loginInfo.toJS().auth;
};

export const setLoginAuth = (props, value) => {
    const { loginActions } = props;
    loginActions.loginSetAuth(value);
};

export const getErrCode = (props) => {
    const { loginInfo } = props;
    return loginInfo.toJS().errCode;
};

export const setErrCode = (props, value) => {
    const { loginActions } = props;
    loginActions.loginSetErrCode(value);
};

export const startLoginAuth = (props, url) => {
    const { loginActions } = props;
    return loginActions.loginCheckAuth(url);
};

export const startLogout = (props, url) => {
    const { loginActions } = props;
    return loginActions.loginSetLogOff(url);
};

export const changePassword = (props, state) => {
    const { loginActions, loginInfo } = props;
    const { userId } = loginInfo.toJS();
    const { oldPw, newPw } = state;
    const requestData = {
        newPassword: md5(newPw),
        oldPassword: md5(oldPw)
    }

    return loginActions.changeUserPassword(`${Define.REST_USERS_PATCH_CHANGE_PASSWORD}/${userId}/password`, requestData);
};

