import { createAction, handleActions } from 'redux-actions';
import { Map, List, fromJS, Record } from 'immutable';
import { pender , applyPenders } from 'redux-pender';
import services from '../services';


const GET_DW_HISTORY = "dwHis/GET_DW_HISTORY";
const ADD_DW_HISTORY = "dwHis/ADD_DW_HISTORY";
const DELETE_DW_HISTORY = "dwHis/DELETE_DW_HISTORY";
const DW_HIS_INIT_SERVER_ERROR = "user/USER_INIT_SERVER_ERROR";

export const loadDwHistoryList = createAction(GET_DW_HISTORY, services.axiosAPI.get);
export const addDwHistory = createAction(ADD_DW_HISTORY, services.axiosAPI.get);
export const deleteDwHistory = createAction(DELETE_DW_HISTORY, services.axiosAPI.get);
const initialState = Map({
    dwHistoryInfo :
        Map({
        result:"",
        totalCnt:"",
        isServerErr:"",
        dw_list: List([
            Map({
                dw_id:"",
                dw_user: "",
                dw_date:"",
                dw_type:"",
                dw_filelist:"",
            })
        ]),
    }),
});

export default handleActions({
    [DW_HIS_INIT_SERVER_ERROR] : (state, action) => {
        return state.setIn(["dwHistoryInfo","isServerErr"], false);
    },

    ...pender(
        {
            type: GET_DW_HISTORY,
            onPending: (state, action) => {
                return state.setIn(["dwHistoryInfo","isServerErr"], false)
            },
            onFailure: (state, action) => {
                return state.setIn(["dwHistoryInfo","isServerErr"], true)
            },
            onSuccess: (state, action) => {
                const {data, result} = action.payload.data;

                if (result !== 0) {
                    console.warn("[GET_DW_HISTORY] error ", result);
                    return state.setIn(["dwHistoryInfo", "result"], result);
                }
                console.log("[GET_DW_HISTORY]  ", result);

                const cDwList = data.map(list => {
                    return {
                        dw_id: list.dw_id,
                        dw_user: list.dw_user,
                        dw_date: list.dw_date,
                        dw_type: list.dw_type,
                        dw_filelist: list.dw_filelist
                    }
                });

                return state
                    .setIn(["dwHistoryInfo", "dw_list"], fromJS(cDwList))
                    .setIn(["dwHistoryInfo", "totalCnt"], cDwList.length)
                    .setIn(["dwHistoryInfo", "result"], result);
            }
        }
    )
}, initialState);

