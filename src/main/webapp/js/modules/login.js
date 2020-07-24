import {createAction, handleActions} from 'redux-actions';
import {Map} from 'immutable';
import {pender} from 'redux-pender';
import services from '../services';
import * as Define from "../define";

const LOGIN_INIT_ALL_DATA = "login/LOGIN_INIT_ALL_DATA";
const LOGIN_SET_ISLOGGEDIN = "login/LOGIN_SET_ISLOGGEDIN";
const LOGIN_SET_USERNAME = "login/LOGIN_SET_USERNAME";
const LOGIN_SET_PASSWORD = "login/LOGIN_SET_PASSWORD";
const LOGIN_SET_AUTH = "login/LOGIN_SET_AUTH";
const LOGIN_SET_ERROR_CODE = "login/LOGIN_SET_ERROR_CODE";
const LOGIN_CHECK_AUTH = "login/LOGIN_CHECK_AUTH";
const LOGIN_SET_LOGOFF = "login/LOGIN_SET_LOGOFF";
const CHANGE_USER_PASSWORD = "login/CHANGE_USER_PASSWORD";

export const loginInitAllData = createAction(LOGIN_INIT_ALL_DATA);
export const loginSetIsLoggedIn = createAction(LOGIN_SET_ISLOGGEDIN);
export const loginSetUsername = createAction(LOGIN_SET_USERNAME);
export const loginSetPassword = createAction(LOGIN_SET_PASSWORD);
export const loginSetAuth = createAction(LOGIN_SET_AUTH);
export const loginSetErrCode = createAction(LOGIN_SET_ERROR_CODE);
export const loginCheckAuth = createAction(LOGIN_CHECK_AUTH, services.axiosAPI.requestGet);
export const loginSetLogOff = createAction(LOGIN_SET_LOGOFF,services.axiosAPI.requestGet);
export const changeUserPassword = createAction(CHANGE_USER_PASSWORD,services.axiosAPI.requestPatch);

const initialState = Map({
    loginInfo : Map({
        errCode: 0,
        isLoggedIn: false,
        username: "",
        password: "",
        userId: "",
        auth: ""
    })
});

export default handleActions({

    [LOGIN_INIT_ALL_DATA]: (state, action) => {
        return initialState;
    },

    [LOGIN_SET_ISLOGGEDIN]: (state, action) => {
        const setValue = action.payload;
        return state.setIn(["loginInfo", "isLoggedIn"], setValue);
    },

    [LOGIN_SET_USERNAME]: (state, action) => {
        const setValue = action.payload;
        return state.setIn(["loginInfo", "username"], setValue);
    },

    [LOGIN_SET_PASSWORD]: (state, action) => {
        const setValue = action.payload;
        return state.setIn(["loginInfo", "password"], setValue);
    },

    [LOGIN_SET_AUTH]: (state, action) => {
        const setValue = action.payload;
        return state.setIn(["loginInfo", "auth"], setValue);
    },

    [LOGIN_SET_ERROR_CODE]: (state, action) => {
        const setValue = action.payload;
        return state.setIn(["loginInfo", "errCode"], setValue);
    },
    ...pender(
        {
            type: LOGIN_CHECK_AUTH,
            onSuccess: (state, action) => {
                console.log("action.payload", action.payload);
                const { data}  = action.payload;
                return state.setIn(["loginInfo", "isLoggedIn"], true)
                            .setIn(["loginInfo", "username"], data.userName)
                            .setIn(["loginInfo", "userId"], data.userId)
                            .setIn(["loginInfo", "auth"], data.permission);
            },
            onFailure: (state, action) => {
                const { status, data }  = action.payload.response;
                const { error } = data;
                let errorCode = 0;
                if (error.reason === Define.REASON_INVALID_PARAMETER) errorCode = Define.LOGIN_FAIL_NO_USERNAME_PASSWORD;
                else if (error.reason === Define.REASON_INVALID_PASSWORD) errorCode = Define.LOGIN_FAIL_INCORRECT_PASSWORD;
                else if (error.reason === Define.REASON_NOT_FOUND) errorCode = Define.LOGIN_FAIL_NO_REGISTER_USER;
                else errorCode = Define.COMMON_FAIL_SERVER_ERROR

                return state.setIn(["loginInfo", "isLoggedIn"], false)
                  .setIn(["loginInfo", "errCode"], errorCode);
            }
        },
    ),
    ...pender(
        {
            type: LOGIN_SET_LOGOFF,
            onSuccess: (state, action) => {
                return state.setIn(["loginInfo", "isLoggedIn"], false);
            }
        }
    ),
    ...pender(
        {
            type: CHANGE_USER_PASSWORD,
            onSuccess: (state, action) => {
                return state.setIn(["loginInfo", "errCode"], Define.RSS_SUCCESS);
            },
            onFailure: (state, action) => {
                const {status, data: {error: {reason}}} = action.payload.response;
                let result = Define.COMMON_FAIL_SERVER_ERROR;
                    if (reason !== undefined || reason !== null) {
                        if (status === Define.BAD_REQUEST) {
                            result = reason === Define.REASON_INVALID_PASSWORD
                              ? Define.CHANGE_PW_FAIL_INCORRECT_CURRENT_PASSWORD
                              : Define.CHANGE_PW_FAIL_EMPTY_PASSWORD
                        } else if (status === Define.NOT_FOUND) {
                            result = Define.DB_UPDATE_ERROR_NO_SUCH_USER
                        } else {
                            result = Define.COMMON_FAIL_SERVER_ERROR
                        }
                    }
                    return state.setIn(["loginInfo", "errCode"], result);
            }
        }
    )
}, initialState);

