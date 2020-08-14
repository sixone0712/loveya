import {createAction, handleActions} from 'redux-actions';
import {fromJS, List, Map} from 'immutable';
import {pender} from 'redux-pender';
import moment from "moment";
import * as  API from "../api";

const VFTP_SSS_INIT_ALL = 'vftpSss/VFTP_SSS_INIT_ALL';
const VFTP_SSS_SET_REQUEST_MACHINE= 'vftpSss/VFTP_SSS_SET_REQUEST_MACHINE';
const VFTP_SSS_SET_REQUEST_COMMAND= 'vftpSss/VFTP_SSS_SET_REQUEST_COMMAND';
const VFTP_SSS_SET_REQEUST_START_DATE= 'vftpSss/VFTP_SSS_SET_REQEUST_START_DATE';
const VFTP_SSS_SET_REQUEST_END_DATE= 'vftpSss/VFTP_SSS_SET_REQUEST_END_DATE';
const VFTP_SSS_SET_RESPONSE_LIST= 'vftpSss/VFTP_SSS_SET_RESPONSE_LIST';
const VFTP_SSS_CHECK_RESPONSE_LIST = 'vftpSss/VFTP_SSS_CHECK_RESPONSE_LIST';
const VFTP_SSS_CHECK_ALL_RESPONSE_LIST = 'vftpSss/VFTP_SSS_CHECK_ALL_RESPONSE_LIST';
const VFTP_SSS_INIT_RESPONSE_LIST = 'vftpSss/VFTP_SSS_INIT_RESPONSE_LIST';
const VFTP_SSS_SET_IS_NEW_RESPONSE_LIST = 'vftpSss/VFTP_SSS_SET_IS_NEW_RESPONSE_LIST';

export const vftpSssInitAll = createAction(VFTP_SSS_INIT_ALL); //initialize....
export const vftpSssSetRequestMachine = createAction(VFTP_SSS_SET_REQUEST_MACHINE); 	// machine
export const vftpSssSetRequestCommand = createAction(VFTP_SSS_SET_REQUEST_COMMAND); 	// command
export const vftpSssSetRequestStartDate = createAction(VFTP_SSS_SET_REQEUST_START_DATE); 	// startDate
export const vftpSssSetRequestEndDate = createAction(VFTP_SSS_SET_REQUEST_END_DATE); 	// endDate
export const vftpSssSetResponseList = createAction(VFTP_SSS_SET_RESPONSE_LIST);
export const vftpSssCheckResponseList = createAction(VFTP_SSS_CHECK_RESPONSE_LIST);
export const vftpSssCheckAllResponseList = createAction(VFTP_SSS_CHECK_ALL_RESPONSE_LIST);
export const vftpSssInitResponseList = createAction(VFTP_SSS_INIT_RESPONSE_LIST);
export const vftpSssSetIsNewResponseList = createAction(VFTP_SSS_SET_IS_NEW_RESPONSE_LIST);

const initialState = Map({
    requestCompletedDate: "",
    requestListCnt: 0,
    requestList: Map({
        fabNames: List[{}],
        machineNames: List[{}],
        command: "",
    }),
    isNewResponseList: false,
    responseList: List[{}],
    responseListCnt: 0,
    downloadAll: false,
    downloadCnt: 0,
    downloadStatus: Map({
        func: null,
        dlId: "",
        status: "init",
        totalFiles: 0,
        downloadFiles: 0,
        downloadUrl: ""
    }),
    startDate: moment().startOf('day'),
    endDate: moment().endOf('day')
});


export default handleActions({
    [VFTP_SSS_INIT_ALL]: (state, action) => {
        return initialState;
    },

    [VFTP_SSS_SET_REQUEST_MACHINE]: (state, action) => {
        const { fabNames, machineNames } = action.payload;
        console.log("VFTP_SSS_SET_REQUEST_MACHINE");

        return state
            .setIn(["requestList","fabNames"], fabNames)
            .setIn(["requestList", "machineNames"], machineNames);
    },

    [VFTP_SSS_SET_REQUEST_COMMAND]: (state, action) => {
        const command = action.payload;
        console.log("VFTP_SSS_SET_REQUEST_COMMAND");
        return state.setIn(["requestList","command"], command);
    },

    [VFTP_SSS_SET_REQEUST_START_DATE]: (state, action) => {
        const startDate = action.payload;
        console.log("VFTP_SSS_SET_REQEUST_START_DATE");
        return state.set("startDate", startDate);
    },

    [VFTP_SSS_SET_REQUEST_END_DATE]: (state, action) => {
        const endDate = action.payload;
        console.log("SEARCH_SET_REQUEST_END_DATE");
        return state.set("endDate", endDate);
    },
    [VFTP_SSS_SET_RESPONSE_LIST]: (state, action) => {
        const lists = action.payload.data.lists;
        const newList = lists.map((item, idx) => {
            item.index = idx;
            item.checked = true;
            return item;
        })

        return state.set("responseList", fromJS(newList))
                    .set('responseListCnt', newList.length)
                    .set('downloadCnt', newList.length)
                    .set('downloadAll', true)
                    .set('isNewResponseList', true);
    },
    [VFTP_SSS_CHECK_RESPONSE_LIST]: (state, action) => {
        const responseList = state.get("responseList");
        let downloadCnt = state.get("downloadCnt");
        const index = action.payload;

        //console.log("responseList", responseList);
        //console.log("index", index);

        const check =  responseList.getIn([index, 'checked']);
        if(check){
            downloadCnt--;
        } else {
            downloadCnt++;
        }

        return state.set("responseList", responseList.update(index, list => list.set("checked", !list.get("checked")))).set('downloadCnt', downloadCnt);;
    },

    [VFTP_SSS_CHECK_ALL_RESPONSE_LIST] : (state, action) => {
        const responseList = state.get("responseList");
        const check = action.payload;
        let downloadCnt = 0;

        const newResponseList = responseList.map(list => list.set("checked", check));
        if(check){
            downloadCnt = newResponseList.size;
        }

        return state.set("responseList", newResponseList)
                    .set('downloadCnt', downloadCnt)
                    .set('downloadAll', check);
    },

    [VFTP_SSS_INIT_RESPONSE_LIST] : (state, action) => {
        return state.set("responseList", List([]))
                    .set("requestListCnt", 0)
                    .set("responseListCnt", 0)
                    .set('downloadCnt', 0)
                    .set('isNewResponseList', false);
    },

    [VFTP_SSS_SET_IS_NEW_RESPONSE_LIST] : (state, action) => {
        const check = action.payload;
        return state.set("isNewResponseList", check);
    },
}, initialState)
