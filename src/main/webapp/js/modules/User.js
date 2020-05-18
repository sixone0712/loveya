import { createAction, handleActions } from 'redux-actions';
import { Map, List, fromJS, Record } from 'immutable';
import { pender , applyPenders } from 'redux-pender';
import services from '../services';


const USER_CREATE = "user/USER_CREATE";
const USER_DELETE = "user/USER_DELETE";
const USER_GET_LIST = "user/USER_GET_LIST";
const USER_GET_DOWNLOAD_HISTORY = "user/USER_GET_DOWNLOAD_HISTORY";
const USER_INIT_ALL_LIST = "user/USER_INIT_ALL_LIST";
const USER_INIT_SERVER_ERROR = "user/USER_INIT_SERVER_ERROR";

export const createUser = createAction(USER_CREATE, services.axiosAPI.get);
export const deleteUser = createAction(USER_DELETE, services.axiosAPI.get);
export const loadUserList = createAction(USER_GET_LIST, services.axiosAPI.get);

const initialState = Map({
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
                validity: false
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
                return  state.setIn(["UserInfo","result"], action.payload.data);
            }
        },
    ),
    ...pender(
        {
            type: USER_DELETE,
            onSuccess: (state, action) => {
                return  state.setIn(["UserInfo","result"], action.payload.data);
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
                const {data, result} = action.payload.data;

                if (result !== 0) {
                    console.warn("[USER_GET_LIST] error ", result);
                    return state.setIn(["UserList", "result"], result);
                }

                const cUserList = data.map(list => {
                    return {
                        id: list.id,
                        name: list.username,
                        auth: list.permissions,
                        created: list.created,
                        modified: list.modified,
                        validity: list.validity,
                        last_access: list.lastAccess,
                    }
                });

                return state
                    .setIn(["UserList", "list"], fromJS(cUserList))
                    .setIn(["UserList", "totalCnt"], cUserList.length)
                    .setIn(["UserList", "result"], result);
            }
        }
    )
}, initialState);

