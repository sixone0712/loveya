import { createAction, handleActions } from 'redux-actions';
import { Map, List, fromJS } from 'immutable';
import { pender } from 'redux-pender';
import services from "../services";
import moment from "moment";

const SEARCH_SET_LIST= 'searchList/SEARCH_SET_LIST';
const SEARCH_SET_START_DATE= 'searchList/SEARCH_SET_START_DATE';
const SEARCH_SET_END_DATE= 'searchList/SEARCH_SET_END_DATE';
const SEARCH_LOAD_LIST= 'searchList/SEARCH_LOAD_LIST';
const SEARCH_CHECK_LIST = 'searchList/SEARCH_CHECK_LIST';
const SEARCH_CHECK_ALL_LIST = 'searchList/SEARCH_CHECK_ALL_LIST';
const SEARCH_SET_DOWNLOAD_LIST = 'searchList/SEARCH_SET_DOWNLOAD_LIST';


export const searchSetList = createAction(SEARCH_SET_LIST); 	// toolList
export const searchSetStartDate = createAction(SEARCH_SET_START_DATE); 	// toolList
export const searchSetEndDate = createAction(SEARCH_SET_END_DATE); 	// toolList
export const searchLoadList = createAction(SEARCH_LOAD_LIST, services.axiosAPI.post);
export const searchCheckList = createAction(SEARCH_CHECK_LIST); 	// toolList
export const searchCheckALLList = createAction(SEARCH_CHECK_ALL_LIST); 	// toolList
export const searchSetDownlaodList = createAction(SEARCH_SET_DOWNLOAD_LIST); 	// toolList

const initialState = Map({
    requestListCnt: 0,
    requestList: List([
        Map({
            structId: "",
            targetName: "",
            targetType: "",
            logType: "",
            logCode: "",
            logName: "",
            startDate: "",
            endDate: "",
            keyword: "",
            dir: ""
        })
    ]),

    downloadCnt: 0,
    responseListCnt: 0,
    responseList: List([
		Map({
            keyIndex: 0,
            fileId: 0,
            fileStatus: "",
            logId: "",
            fileName: "",
            fileSize: 0,
            fileDate: "",
            filePath: "",
            file: false,
            structId: "",
            targetName: "",
            logName: "",
            sizeKB: 0,
            checked: false
		})
    ]),
    
    startDate: moment()
        .hour(0)
        .minute(0),
    endDate : moment()
        .hour(23)
        .minute(59),
});

//2020-08-20 07:25
export default handleActions({
    ...pender(
        {
            type: SEARCH_LOAD_LIST, // type 이 주어지면, 이 type 에 접미사를 붙인 액션핸들러들이 담긴 객체를 생성합니다.
            // 요청중 / 실패 했을 때 추가적으로 해야 할 작업이 있다면 이렇게 onPending 과 onFailure 를 추가해주면됩니다.
            // onPending: (state, action) => state,
            // onFailure: (state, action) => state

            onSuccess: (state, action) => { // 성공했을때 해야 할 작업이 따로 없으면 이 함수 또한 생략해도 됩니다.
                console.log("handleActions[SEARCH_LOAD_LIST]");
                const lists = action.payload.data;
                const newLists = lists.map((list, idx) => {
                    return {
                        keyIndex: idx,
                        fileId: list.fileId,
                        fileStatus: list.fileStatus,
                        logId: list.logId,
                        fileName: list.fileName,
                        fileSize: list.fileSize,
                        fileDate: list.fileDate,
                        filePath: list.filePath,
                        file: list.file,
                        structId: list.structId,
                        targetName: list.targetName,
                        logName: list.logName,
                        sizeKB: list.sizeKB,
                        checked: false
                    }
                });

                const newListSize = newLists.length;
                return state.set('responseList', fromJS(newLists)).set('requestListCnt', newListSize)
                            .set('responseListCnt', newListSize);
            },
            // 함수가 생략됐을때 기본 값으론 (state, action) => state 가 설정됩니다 (state 를 그대로 반환한다는 것이죠)
        }),

    [SEARCH_SET_START_DATE]: (state, action) => {

        const startTime = action.payload;
        //const moment = require("moment");
        //const convDate = moment(startTime).format("YYMMDDHHMMSS");

        return state.set("startDate", startTime);
    },

    [SEARCH_SET_END_DATE]: (state, action) => {

        const endDate = action.payload;
        //const moment = require("moment");
        //const convDate = moment(endDate).format("YYMMDDHHMMSS");

        return state.set("startDate", endDate);
    },


    [SEARCH_SET_LIST]: (state, action) => {
        console.log("SEARCH_SET_LIST");

        const { requestList } = state;
        const { toolList, logInfoList, startDate, endDate } = action.payload;

        console.log("startDate", startDate);
        console.log("toolList", toolList.toJS());
        console.log("logInfoList", logInfoList.toJS());

        const newToolList = toolList.filter(list => list.get("checked") === true).toJS();
        const newLogInfoList = logInfoList.filter(list => list.get("checked") === true).toJS();
        const formDate = moment(startDate).format("YYYYMMDDHHmmss");
        const toDate = moment(endDate).format("YYYYMMDDHHmmss");

        console.log("newToolList", newToolList);
        console.log("newLogInfoList", newLogInfoList);
        console.log("formDate", formDate);
        console.log("toDate", toDate);

        const newSearchList = new Array();
        for (let tList of newToolList) {
            for(let fList of newLogInfoList) {
                console.log("tList", tList);
                console.log("tList.structId", tList.structId);
                console.log("tList.targetName", tList.targetname);
                console.log("tList.targetType", tList.targettype);
                console.log("fList.logType", fList.logType);
                console.log("fList.logCode", fList.logCode);
                console.log("fList.logName", fList.logName);

                newSearchList.push(
                    {
                        structId: tList.structId,
                        targetName: tList.targetname,
                        targetType: tList.targettype,
                        logType: fList.logType,
                        logCode: fList.logCode,
                        logName: fList.logName,
                        startDate: formDate,
                        endDate: toDate,
                        keyword: "",
                        dir: "",
                    }
                );
            }
        }
        console.log("newSearchList", newSearchList);

        return state.set("requestList", fromJS(newSearchList));
    },

    [SEARCH_CHECK_LIST]: (state, action) => {
        console.log("handleActions[SEARCH_CHECK_LIST]");
        const responseList = state.get("responseList");
        let downloadCnt = state.get("downloadCnt");
        const index = action.payload;

        console.log("responseList", responseList);
        console.log("index", index);

        const check =  responseList.getIn([index, 'checked']);
        if(check){
            downloadCnt--;
        } else {
            downloadCnt++;
        }

        return state.set("responseList", responseList.update(index, list => list.set("checked", !list.get("checked")))).set('downloadCnt', downloadCnt);;
    },

    [SEARCH_CHECK_ALL_LIST] : (state, action) => {
        const responseList = state.get("responseList");
        const check = action.payload;
        let downloadCnt = 0;

        const newResponseList = responseList.map(list => list.set("checked", check));
        if(check){
            downloadCnt = newResponseList.size;
        }

        return state.set("responseList", newResponseList).set('downloadCnt', downloadCnt);
    },

    [SEARCH_SET_DOWNLOAD_LIST]: (state, action) => {

        const { responseList } = state;
        const checkedResList = responseList.filter(list => list.get("checked") === true).toJS();


        const toolList = checkedResList.filter( (item, idx, checkedResList) => {
            return checkedResList.indexOf( item.targetName ) === idx ;
        });

        const logList = checkedResList.filter( (item, idx, checkedResList) => {
            return checkedResList.indexOf( item.logCode ) === idx ;
        });

        /*
        수정해야함.
        const newSearchList = new Array();
        for (let tList of newToolList) {
            for(let fList of newLogInfoList) {
                newSearchList.push(
                    {
                        structId: tList.structId,
                        targetName: tList.targetName,
                        targetType: tList.targetType,
                        logType: fList.logType,
                        logCode: fList.code,
                        logName: fList.logName,
                        startDate: formDate,
                        endDate: toDate,
                        keyword: "",
                        dir: "",
                    }
                );
            }
        }
        console.log("newSearchList", newSearchList);
         */

        return state.set("requestList", fromJS(newSearchList));
    },
	
}, initialState)
