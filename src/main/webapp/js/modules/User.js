import {createAction, handleActions} from 'redux-actions';
import {fromJS, List, Map} from 'immutable';
import {pender} from 'redux-pender';
import services from '../services';
import * as Define from "../define";

const USER_CREATE = "user/USER_CREATE";
const USER_DELETE = "user/USER_DELETE";
const USER_MODIFY_AUTH = "user/USER_MODIFY_AUTH";
const USER_GET_LIST = "user/USER_GET_LIST";
const USER_INIT_ALL_LIST = "user/USER_INIT_ALL_LIST";
const USER_INIT_SERVER_ERROR = "user/USER_INIT_SERVER_ERROR";

export const createUser = createAction(USER_CREATE, services.axiosAPI.requestPost);
export const deleteUser = createAction(USER_DELETE, services.axiosAPI.requestDelete);
export const loadUserList = createAction(USER_GET_LIST, services.axiosAPI.requestGet);
export const changeUserPermission = createAction(USER_MODIFY_AUTH, services.axiosAPI.requestPatch);

export const initialState = Map({
    UserInfo : Map({
        name: "",
        pwd:"",
        auth:"",
        result:"",
    }),
    UserList: Map({
        isServerErr: false,
        totalCnt: -1,
        result: 1,
        list: List([
            Map({
                id: 0,
                name: "",
                pwd:"",
                auth:"",
                created: "",
                last_access: "",
                modified: "",
            })
        ]),
    }),
});

export default handleActions({
    [USER_INIT_ALL_LIST]: (state, action) => {
        return initialState;
    },
    [USER_INIT_SERVER_ERROR] : (state, action) => {
        return state.setIn(["UserInfo","isServerErr"], false);
    },

    ...pender(
        {
            type: USER_CREATE,
            onSuccess: (state, action) => {
                return  state.setIn(["UserInfo","result"], Define.RSS_SUCCESS);
            },
            onFailure: (state, action) => {
              const { status, data : { error : { reason } } } = action.payload.response;
              let result = Define.USER_SET_FAIL_NO_REASON;
              if(reason !== undefined || reason !== null) {
                if (status === Define.BAD_REQUEST) {
                    result = Define.LOGIN_FAIL_NO_USERNAME_PASSWORD;
                } else if(status === Define.CONFLICT) {
                  result = Define.USER_SET_FAIL_SAME_NAME
                } else {
                  result = Define.USER_SET_FAIL_NO_REASON
                }
              }
              return  state.setIn(["UserInfo","result"], result);
            }
        },
    ),
    ...pender(
        {
            type: USER_DELETE,
            onSuccess: (state, action) => {
                return  state.setIn(["UserInfo","result"], Define.RSS_SUCCESS);
            },
            onFailure: (state, action) => {
              const { status, data : { error : { reason } } } = action.payload.response;
              let result = Define.USER_SET_FAIL_NO_REASON;
              if(reason !== undefined || reason !== null) {
                if(status === Define.NOT_FOUND) {
                  result = Define.DB_UPDATE_ERROR_NO_SUCH_USER
                } else {
                  result = Define.USER_SET_FAIL_NO_REASON
                }
              }
              return  state.setIn(["UserInfo","result"], result);
            }
        }
    ),
    ...pender(
        {
            type: USER_MODIFY_AUTH,
            onSuccess: (state, action) => {
              const { status, data: { permission } } = action.payload;
              return state.setIn(["UserInfo", "result"], Define.RSS_SUCCESS)
                          .setIn(["UserInfo", "auth"], permission);
            },
            onFailure: (state, action) => {
              const { status, data : { error : { reason } } } = action.payload.response;
              let result = Define.USER_SET_FAIL_NO_REASON;
              if(reason !== undefined || reason !== null) {
                if(status === Define.BAD_REQUEST) {
                  result = Define.LOGIN_FAIL_NO_USERNAME_PASSWORD;
                } else if(status === Define.NOT_FOUND) {
                  result = Define.DB_UPDATE_ERROR_NO_SUCH_USER
                } else {
                  result = Define.USER_SET_FAIL_NO_REASON
                }
              }
              return  state.setIn(["UserInfo","result"], result);
            }
        }
    ),
    ...pender(
        {
            type: USER_GET_LIST,
            onPending: (state, action) => {
                return state.setIn(["UserList","isServerErr"], false)
            },
            onFailure: (state, action) => {
                return state.setIn(["UserList","isServerErr"], true)
            },
            onSuccess: (state, action) => {
                const { data: { lists } } = action.payload;
                const cUserList = lists.map(list => {
                    return {
                        id: list.userId,
                        name: list.userName,
                        auth: list.permission,
                        created: list.created,
                        modified: list.modified,
                        last_access: list.lastAccess,
                    }
                });

                return state
                    .setIn(["UserList", "list"], fromJS(cUserList))
                    .setIn(["UserList", "totalCnt"], cUserList.length);
            }
        }
    )
}, initialState);

