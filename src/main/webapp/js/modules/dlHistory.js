import {createAction, handleActions} from 'redux-actions';
import {fromJS, List, Map} from 'immutable';
import {pender} from 'redux-pender';
import services from '../services';
import * as Define from "../define";

const GET_DL_HISTORY = "dlHistory/GET_DL_HISTORY";
const ADD_DL_HISTORY = "dlHistory/ADD_DL_HISTORY";
const DELETE_DL_HISTORY = "dlHistory/DELETE_DL_HISTORY";
const DL_HIS_INIT_SERVER_ERROR = "dlHistory/DL_HIS_INIT_SERVER_ERROR";

export const loadDlHistoryList = createAction(GET_DL_HISTORY, services.axiosAPI.requestGet);
export const addDlHistory = createAction(ADD_DL_HISTORY, services.axiosAPI.requestGet);
export const deleteDlHistory = createAction(DELETE_DL_HISTORY, services.axiosAPI.requestGet);

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
              const { data: { lists } } = action.payload;

              if(lists === null || lists === undefined) {
                return state;
              }
                const newList = lists.map((list,idx) => {
                  return {
                    dl_Idx: idx+1,
                    dl_id: list.historyId,
                    dl_user: list.userName,
                    dl_date: list.date,
                    dl_type: list.type,
                    dl_filename: list.fileName,
                    dl_status: list.status
                  }
                });

                return state
                  .setIn(["dlHistoryInfo", "dl_list"], fromJS(newList))
                  .setIn(["dlHistoryInfo", "totalCnt"], newList.length)
                  .setIn(["dlHistoryInfo", "result"], Define.RSS_SUCCESS);
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
                return  state.setIn(["dlHistoryInfo","result"], Define.RSS_SUCCESS);
            }
        }
    )
}, initialState);

