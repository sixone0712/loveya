import {createAction, handleActions} from 'redux-actions';
import {fromJS, List, Map} from 'immutable';
import {pender} from 'redux-pender';
import services from "../services";
import moment from "moment";
import * as  API from "../api";

const SEARCH_SET_INIT_ALL_LIST = 'searchList/SEARCH_SET_INIT_ALL_LIST';
const SEARCH_SET_REQUEST_LIST= 'searchList/SEARCH_SET_REQUEST_LIST';
const SEARCH_SET_REQEUST_START_DATE= 'searchList/SEARCH_SET_REQEUST_START_DATE';
const SEARCH_SET_REQUEST_END_DATE= 'searchList/SEARCH_SET_REQUEST_END_DATE';
const SEARCH_INIT_RESPONSE_LIST = 'searchList/SEARCH_INIT_RESPONSE_LIST';
const SEARCH_LOAD_RESPONSE_LIST= 'searchList/SEARCH_LOAD_RESPONSE_LIST';
const SEARCH_CHECK_RESPONSE_LIST = 'searchList/SEARCH_CHECK_RESPONSE_LIST';
const SEARCH_CHECK_ALL_RESPONSE_LIST = 'searchList/SEARCH_CHECK_ALL_RESPONSE_LIST';
const SEARCH_SET_RESPONSE_PERPAGE= 'searchList/SEARCH_SET_RESPONSE_PERPAGE';
const SEARCH_SET_DL_STATUS= 'searchList/SEARCH_SET_DL_STATUS';

export const searchSetInitAllList = createAction(SEARCH_SET_INIT_ALL_LIST);
export const searchSetRequestList = createAction(SEARCH_SET_REQUEST_LIST); 	// toolList
export const searchSetRequestStartDate = createAction(SEARCH_SET_REQEUST_START_DATE); 	// toolList
export const searchSetRequestEndDate = createAction(SEARCH_SET_REQUEST_END_DATE); 	// toolList
export const searchInitResponseList = createAction(SEARCH_INIT_RESPONSE_LIST); 	// toolList
export const searchLoadResponseList = createAction(SEARCH_LOAD_RESPONSE_LIST, services.axiosAPI.requestPost);
export const searchCheckResponseList = createAction(SEARCH_CHECK_RESPONSE_LIST); 	// toolList
export const searchCheckALLResponseList = createAction(SEARCH_CHECK_ALL_RESPONSE_LIST); 	// toolList
export const searchSetResponsePerPage = createAction(SEARCH_SET_RESPONSE_PERPAGE); 	// toolList
export const searchSetDlStatus = createAction(SEARCH_SET_DL_STATUS);
/*
export const searchSetDlId = createAction(SEARCH_SET_DL_ID);
export const searchSetDlId = createAction(SEARCH_SET_DL_STATUS);
export const searchSetDlId = createAction(SEARCH_SET_DL_TOTAL_FILES);
export const searchSetDlId = createAction(SEARCH_SET_DL_DOWNLOAD_FILES);
*/


const initialState = Map({
    requestCompletedDate: "",
    requestListCnt: 0,
    requestList: Map({
        fabNames: List[{}],
        machineNames: List[{}],
        //categoryTypes: List[{}],     //Not currently in use
        categoryCodes: List[{}],
        categoryNames: List[{}],
        startDate: "",
        endDate: "",
        //keyword: "",      //Not currently in use
        //dir: "",      //Not currently in use
    }),
    downloadCnt: 0,
    responsePerPage: 10,
    responseListCnt: 0,
    responseList: List([
		Map({
            keyIndex: 0,
            //fileId: 0,        //Not currently in use
            //fileStatus: "",   //Not currently in use
            logId: "",
            fileName: "",
            fileSize: 0,
            fileDate: "",
            filePath: "",
            //file: false,      //Not currently in use
            structId: "",
            targetName: "",
            logName: "",
            sizeKB: 0,
            checked: false
		})
    ]),

    downloadStatus: Map({
        func: null,
        dlId: "",
        status: "init",
        totalFiles: 0,
        downloadFiles: 0,
        downloadUrl: ""
    }),

    //startDate: moment().set({'hour' : 0, 'minute': 0, 'second': 1}),
    startDate: moment().startOf('day'),
        // .hour(1)
        // .minute(0),
    endDate : moment().endOf('day')
        // .hour(23)
        // .minute(59),
});

//2020-08-20 07:25
export default handleActions({
    ...pender(
        {
            type: SEARCH_LOAD_RESPONSE_LIST,
            // onPending: (state, action) => state,
            // onFailure: (state, action) => state,
            onSuccess: (state, action) => {
                console.log("handleActions[SEARCH_LOAD_RESPONSE_LIST]");
                const {lists} = action.payload.data;
                //console.log("handleActions[SEARCH_LOAD_RESPONSE_LIST]lists", lists);
                const newLists = lists.map((list, idx) => {
                    return {
                        keyIndex: idx,
                        structId: list.fabName,
                        targetName: list.machineName,
                        logName: list.categoryName,
                        logId: list.categoryCode,
                        //fileId: list.fileId,  //Not currently in use
                        fileName: list.fileName,
                        fileSize: list.fileSize,
                        fileDate: list.fileDate,
                        filePath: list.filePath,
                        //fileStatus: list.fileStatus,  //Not currently in use
                        //file: list.file,  //Not currently in use
                        sizeKB: API.bytesToSize(list.fileSize),
                        checked: true
                    }
                });

                //console.log("handleActions[SEARCH_LOAD_RESPONSE_LIST]newLists", newLists);

                const newListSize = newLists.length;
                return state.set('responseList', fromJS(newLists)).set('requestListCnt', newListSize)
                            .set('responseListCnt', newListSize)
                            .set('downloadCnt', newListSize)
                            .set('requestCompletedDate', new Date());
            },
        }),

    [SEARCH_SET_INIT_ALL_LIST]: (state, action) => {
        return initialState;
    },

    [SEARCH_SET_REQEUST_START_DATE]: (state, action) => {

        const startTime = action.payload;
        //const moment = require("moment");
        //const convDate = moment(startTime).format("YYMMDDHHMMSS");
        console.log("SEARCH_SET_REQEUST_START_DATE");
        //console.log("action.payload", action.payload);

        return state.set("startDate", startTime);
    },

    [SEARCH_SET_REQUEST_END_DATE]: (state, action) => {

        const endDate = action.payload;
        //const moment = require("moment");
        //const convDate = moment(endDate).format("YYMMDDHHMMSS");

        console.log("SEARCH_SET_REQUEST_END_DATE");
        //console.log("action.payload", action.payload);

        return state.set("endDate", endDate);
    },


    [SEARCH_SET_REQUEST_LIST]: (state, action) => {
        console.log("SEARCH_SET_REQUEST_LIST");

        const { requestList } = state;
        const { toolList, logInfoList, startDate, endDate } = action.payload;

        //console.log("startDate", startDate);
        //console.log("toolList", toolList.toJS());
        //console.log("logInfoList", logInfoList.toJS());

        const newToolList = toolList.filter(list => list.get("checked") === true).toJS();
        const newLogInfoList = logInfoList.filter(list => list.get("checked") === true).toJS();
        const formDate = moment(startDate).format("YYYYMMDDHHmmss");
        const toDate = moment(endDate).format("YYYYMMDDHHmmss");

        //console.log("newToolList", newToolList);
        //console.log("newLogInfoList", newLogInfoList);
        //console.log("formDate", formDate);
        //console.log("toDate", toDate);

        const fabNames = newToolList.map(list => list.structId);
        const machineNames = newToolList.map(list => list.targetname);
        const categoryCodes = newLogInfoList.map(list => list.logCode);
        const categoryNames = newLogInfoList.map(list => list.logName);

        //console.log("handleActions[SEARCH_SET_REQUEST_LIST]fabNames", fabNames);
        //console.log("handleActions[SEARCH_SET_REQUEST_LIST]machineNames", machineNames);
        //console.log("handleActions[SEARCH_SET_REQUEST_LIST]categoryCodes", categoryCodes);
        //console.log("handleActions[SEARCH_SET_REQUEST_LIST]categoryNames", categoryNames);

        return state.setIn(['requestList', 'fabNames'], fromJS(fabNames))
                    .setIn(['requestList', 'machineNames'], fromJS(machineNames))
                    .setIn(['requestList', 'categoryCodes'], fromJS(categoryCodes))
                    .setIn(['requestList', 'categoryNames'], fromJS(categoryNames))
                    .setIn(['requestList', 'startDate'], formDate)
                    .setIn(['requestList', 'endDate'], toDate);
    },

    [SEARCH_CHECK_RESPONSE_LIST]: (state, action) => {
        console.log("handleActions[SEARCH_CHECK_RESPONSE_LIST]");
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

    [SEARCH_CHECK_ALL_RESPONSE_LIST] : (state, action) => {
        const responseList = state.get("responseList");
        const check = action.payload;
        let downloadCnt = 0;

        const newResponseList = responseList.map(list => list.set("checked", check));
        if(check){
            downloadCnt = newResponseList.size;
        }

        return state.set("responseList", newResponseList).set('downloadCnt', downloadCnt);
    },

    [SEARCH_INIT_RESPONSE_LIST] : (state, action) => {
        return state.set("responseList", List([]))
                    .set("requestListCnt", 0)
                    .set("responseListCnt", 0)
                    .set('downloadCnt', 0);
    },

    [SEARCH_INIT_RESPONSE_LIST] : (state, action) => {
        return state.set("responsePerPage", action.payload);
    },

    [SEARCH_SET_DL_STATUS] : (state, action) => {

        const { func, dlId, status, totalFiles, downloadFiles, downloadUrl } = action.payload;
        const downloadStatus = state.get("downloadStatus").toJS();

        //console.log("func", func);
        if(func !== undefined ) {
            downloadStatus.func = func;
        }

        //console.log("dlId", dlId);
        if(dlId !== undefined) {
            downloadStatus.dlId = dlId;
        }
        //console.log("status", status);
        if(status !== undefined) {
            if(status ==="done" || status === "error") {
                clearInterval(downloadStatus.func);
                downloadStatus.func = null;
            }
            downloadStatus.status = status;
        }
        //console.log("totalFiles", totalFiles);
        if(totalFiles !== undefined) {
            downloadStatus.totalFiles = totalFiles;
        }
        //console.log("downloadFiles", downloadFiles);
        if(downloadFiles !== undefined) {
            downloadStatus.downloadFiles = downloadFiles;
        }
        if(downloadUrl !== undefined) {
            downloadStatus.downloadUrl = downloadUrl;
        }

        //console.log("downloadStatus", downloadStatus);
        return state.set("downloadStatus", fromJS(downloadStatus));
    }

}, initialState)
