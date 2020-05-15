import { createAction, handleActions } from 'redux-actions';
import { Map, List, fromJS, Record } from 'immutable';
import { pender , applyPenders } from 'redux-pender';
import services from '../services';
import * as API from "../api";
import moment from "moment";

const COMMAND_ADD = "cmd/COMMAND_ADD";
const COMMAND_DELETE = "cmd/COMMAND_DELETE";
const COMMAND_UPDATE = "cmd/COMMAND_UPDATE";
const COMMAND_GET_LIST = "cmd/COMMAND_GET_LIST";
const COMMAND_INIT_ALL_LIST = "cmd/COMMAND_INIT_ALL_LIST";
const COMMAND_INIT_SERVER_ERROR = "cmd/COMMAND_INIT_SERVER_ERROR";
const COMMAND_SET_START_DATE= 'cmd/COMMAND_SET_START_DATE';
const COMMAND_SET_END_DATE= 'cmd/COMMAND_SET_END_DATE';

export const addCommand = createAction(COMMAND_ADD, services.axiosAPI.get);
export const deleteCommand = createAction(COMMAND_DELETE, services.axiosAPI.get);
export const updateCommand = createAction(COMMAND_UPDATE, services.axiosAPI.get);
export const getCommandList = createAction(COMMAND_GET_LIST, services.axiosAPI.get);
export const setCmdStartDate = createAction(COMMAND_SET_START_DATE);
export const setCmdEndDate = createAction(COMMAND_SET_END_DATE);

const initialState = Map({
    CommandInfo : Map({
        cmdErrCode: "",
        cmd_name:"",
        cmd_type:"",
        result:"",
    }),
    CommandList: Map({
        isServerErr: false,
        totalCnt: -1,
        needUpdate : false,
        update: "",
        result: 1,
        list: List([
            Map({
                id: 0,
                cmd_name: "",
                cmd_type: "",
                created: "",
                modified: "",
                validity: false
            })
        ]),
    }),
    startDate: moment().utc().startOf('day'),
    endDate : moment().utc().endOf('day')

});

export default handleActions({
    [COMMAND_INIT_ALL_LIST]: (state, action) => {
        return initialState;
    },
    [COMMAND_INIT_SERVER_ERROR] : (state, action) => {
        return state.setIn(["CommandInfo","isServerErr"], false);
    },
    
    [COMMAND_SET_START_DATE]: (state, action) => {

        const startTime = action.payload;
        console.log("COMMAND_SET_START_DATE");
        console.log("action.payload", action.payload);

        return state.set("startDate", startTime);
    },

    [COMMAND_SET_END_DATE]: (state, action) => {

        const endDate = action.payload;

        console.log("COMMAND_SET_END_DATE");
        console.log("action.payload", action.payload);

        return state.set("endDate", endDate);
    },
    ...pender(
        {
            type: COMMAND_ADD,
            onSuccess: (state, action) => {
                return  state.setIn(["CommandInfo","cmdErrCode"], action.payload.data);
            }
        },
    ),
    ...pender(
        {
            type: COMMAND_DELETE,
            onSuccess: (state, action) => {
                return  state.setIn(["CommandInfo","cmdErrCode"], action.payload.data);
            }
        }
    ),
    ...pender(
        {
            type: COMMAND_GET_LIST,
            onPending: (state, action) => {
                return state.setIn(["CommandList","isServerErr"], false)
            },
            onFailure: (state, action) => {
                return state.setIn(["CommandList","isServerErr"], true)
            },
            onSuccess: (state, action) => {
                const {data, result} = action.payload.data;

                if (result !== 0) {
                    console.warn("[COMMAND_GET_LIST] error ", result);
                    return state.setIn(["CommandList", "result"], result);
                }

                const newCmdList = data.map(list => {
                    return {
                        id: list.id,
                        cmd_name: list.cmd_name,
                        cmd_type: list.cmd_type,
                        created: list.created,
                        modified: list.modified,
                        validity: list.validity
                    }
                });

                return state
                    .setIn(["CommandList", "list"], fromJS(newCmdList))
                    .setIn(["CommandList", "totalCnt"], newCmdList.length)
                    .setIn(["CommandList", "result"], result)
                    .setIn(["CommandList", "needUpdate"], false);
            }
        }
    )
}, initialState);

