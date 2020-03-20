import { createAction, handleActions } from 'redux-actions';
import { Map, List, fromJS } from 'immutable';

const SEARCH_SET_LIST= 'searchList/SEARCH_SET_LIST';
const SEARCH_SET_START_DATE= 'searchList/SEARCH_SET_START_DATE';
const SEARCH_SET_END_DATE= 'searchList/SEARCH_SET_END_DATE';

export const searchSetList = createAction(SEARCH_SET_LIST); 	// toolList
export const searchSetStartDate = createAction(SEARCH_SET_START_DATE); 	// toolList
export const searchSetEndDate= createAction(SEARCH_SET_END_DATE); 	// toolList

const initialState = Map({

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

    responseLists: List([
		Map({
            machine: "",
            fileCat: "",
			name: "",
			timestamp: "",
			size: 0,
			type: ""
		})
    ]),
    
    startDate: "",
    endDate : '',
});

//2020-08-20 07:25
export default handleActions({

    [SEARCH_SET_START_DATE]: (state, action) => {

        const startTime = action.payload;
        const moment = require("moment");
        const convDate = moment(startTime).format("YYMMDDHHMMSS");

        return state.set("startDate", fromJS(convDate));
    },

    [SEARCH_SET_END_DATE]: (state, action) => {

        const endDate = action.payload;
        const moment = require("moment");
        const convDate = moment(endDate).format("YYMMDDHHMMSS");

        return state.set("startDate", fromJS(convDate));
    },


    [SEARCH_SET_LIST]: (state, action) => {

        const { requestLists } = state;
        const { toolList, logInfoList, startDate, endDate } = action.payload;

        console.log("toolList", toolList.toJS());
        console.log("logInfoList", logInfoList.toJS());

        const newToolList = toolList.filter(list => list.get("checked") === true).toJS();
        const newLogInfoList = logInfoList.filter(list => list.get("checked") === true).toJS();
        const formDate = startDate;
        const toDate = endDate;

        console.log("newToolList", newToolList);
        console.log("newLogInfoList", newLogInfoList);

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

        return state.set("requestLists", fromJS(newSearchList));


        /*
        const newSearchList = List([]);
        const newRequestLists = newToolList.map(mList => 
            newfileTypeList.reduce((preValue, curValue) => {
                return preValue.push(
                    Map({
                        toolInfoList: Map({
                                name: mList.get("name"),						
                                type: mList.get("type"),						
                                structId: mList.get("structId"),
                        }),
                        logInfoList: Map({
                            logType: curValue.get("logType"),
                            dataName: curValue.get("dataName"),
                            searchType: curValue.get("searchType"),			
                        })
                    })
                )
            }, newSearchList)
            
        );

        console.log("newSearchList", newSearchList.toJS());
        console.log("newRequestLists", newRequestLists.toJS());
        */

        /*
        const newRequestLists = newToolList.map(mList =>
            newfileTypeList.map(fList => 
                Map({
                    toolInfoList: Map({
                            name: mList.get("name"),						
                            type: mList.get("type"),						
                            structId: mList.get("structId"),
                    }),
                    logInfoList: Map({
                        logType: fList.get("logType"),
                        dataName: fList.get("dataName"),
                        searchType: fList.get("searchType"),			
                    })
                })
            )
        );
        */

        //console.log("newRequestLists", newRequestLists.toJS());

        return state;
    },
	
}, initialState)
