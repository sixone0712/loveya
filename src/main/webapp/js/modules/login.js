import { createAction, handleActions } from 'redux-actions';
import { Map, List, fromJS, Record } from 'immutable';
import { pender , applyPenders } from 'redux-pender';
import services from '../services';

const LOGIN_INIT_ALL_DATA = "login/LOGIN_INIT_ALL_DATA";
const LOGIN_SET_ISLOGGEDIN = "login/LOGIN_SET_ISLOGGEDIN";
const LOGIN_SET_USERNAME = "login/LOGIN_SET_USERNAME";
const LOGIN_SET_PASSWORD = "login/LOGIN_SET_PASSWORD";
const LOGIN_SET_AUTH = "login/LOGIN_SET_AUTH";

export const loginInitAllData = createAction(LOGIN_INIT_ALL_DATA);
export const loginSetIsLoggedIn = createAction(LOGIN_SET_ISLOGGEDIN);
export const loginSetUsername = createAction(LOGIN_SET_USERNAME);
export const loginSetPassword = createAction(LOGIN_SET_PASSWORD);
export const loginSetAuth = createAction(LOGIN_SET_AUTH);

const initialState = Map({
    loginInfo : Map({
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

}, initialState);

