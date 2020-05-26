import { createAction, handleActions } from 'redux-actions';
import { Map, List, fromJS, Record } from 'immutable';
import { pender , applyPenders } from 'redux-pender';
import services from '../services';
import * as API from "../api";

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
export const loginCheckAuth = createAction(LOGIN_CHECK_AUTH, services.axiosAPI.get);
export const loginSetLogOff = createAction(LOGIN_SET_LOGOFF,services.axiosAPI.get);
export const changeUserPassword = createAction(CHANGE_USER_PASSWORD,services.axiosAPI.get);

const initialState = Map({
    loginInfo : Map({
        errCode: "",
        isLoggedIn: false,
        username: "",
        password: "",
        auth: 0
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
                if (action.payload.data === 0) {
                    return state.setIn(["loginInfo", "isLoggedIn"], true);
                } else {
                    return state
                        .setIn(["loginInfo", "isLoggedIn"], false)
                        .setIn(["loginInfo", "errCode"], action.payload.data);
                }
            }
        }
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
                const setValue = action.payload;
                return state.setIn(["loginInfo", "errCode"], action.payload.data);
            }
        }
    )
}, initialState);

