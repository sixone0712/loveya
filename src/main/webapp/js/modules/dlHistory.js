import { createAction, handleActions } from 'redux-actions';
import { Map, List, fromJS, Record } from 'immutable';
import { pender , applyPenders } from 'redux-pender';
import services from '../services';


const GET_DL_HISTORY = "dlHistory/GET_DL_HISTORY";
const ADD_DL_HISTORY = "dlHistory/ADD_DL_HISTORY";
const DELETE_DL_HISTORY = "dlHistory/DELETE_DL_HISTORY";
const DL_HIS_INIT_SERVER_ERROR = "dlHistory/DL_HIS_INIT_SERVER_ERROR";

export const loadDlHistoryList = createAction(GET_DL_HISTORY, services.axiosAPI.get);
export const addDlHistory = createAction(ADD_DL_HISTORY, services.axiosAPI.get);
export const deleteDlHistory = createAction(DELETE_DL_HISTORY, services.axiosAPI.get);

const initialState = Map({
    dlHistoryInfo :
        Map({
        result:"",
        totalCnt:"",
        isServerErr:"",
        dl_list: List([
            Map({
                dl_id:"",
                dl_user: "",
                dl_date:"",
                dl_type:"",
                dl_filename:"",
                dl_status:"",
            })
        ]),
    }),
});

export default handleActions({
    [DL_HIS_INIT_SERVER_ERROR] : (state, action) => {
        return state.setIn(["dlHistoryInfo","isServerErr"], false);
    },

    ...pender(
        {
            type: GET_DL_HISTORY,
            onPending: (state, action) => {
                return state.setIn(["dlHistoryInfo","isServerErr"], false)
            },
            onFailure: (state, action) => {
                return state.setIn(["dlHistoryInfo","isServerErr"], true)
            },
            onSuccess: (state, action) => {
                const {data, result} = action.payload.data;

                if (result !== 0) {
                    console.warn("[GET_DL_HISTORY] error ", result);
                    return state.setIn(["dlHistoryInfo", "result"], result)
                                .setIn(["dlHistoryInfo", "dl_list"], List([]));
                }
                console.log("[GET_DL_HISTORY]  ", result);
                console.log("[GET_DL_HISTORY]  ", data.length);
                if(data.length === 0) {
                    return state.setIn(["dlHistoryInfo", "dl_list"], List([]));
                }

                const cDwList = data.map(list => {
                    return {
                        dl_id: list.id,
                        dl_user: list.dl_user,
                        dl_date: list.dl_date,
                        dl_type: list.dl_type,
                        dl_filename: list.dl_filename,
                        dl_status: list.dl_status
                    }
                });

                return state
                    .setIn(["dlHistoryInfo", "dl_list"], fromJS(cDwList))
                    .setIn(["dlHistoryInfo", "totalCnt"], cDwList.length)
                    .setIn(["dlHistoryInfo", "result"], result);
            }
        }
    ),
    ...pender(
        {
            type: ADD_DL_HISTORY,
            onPending: (state, action) => {
                return state.setIn(["dlHistoryInfo","isServerErr"], false)
            },
            onFailure: (state, action) => {
                return state.setIn(["dlHistoryInfo","isServerErr"], true)
            },
            onSuccess: (state, action) => {
                return  state.setIn(["dlHistoryInfo","result"], action.payload.data);
            }
        }
    )
}, initialState);

