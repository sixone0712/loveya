import {createAction, handleActions} from 'redux-actions';
import {fromJS, List, Map} from 'immutable';
import {pender} from 'redux-pender';
import moment from "moment";
import * as  API from "../api";

const VFTP_COMPAT_INIT_ALL = 'vftpCompat/VFTP_COMPAT_INIT_ALL';
const VFTP_COMPAT_SET_REQUEST_MACHINE= 'vftpCompat/VFTP_COMPAT_SET_REQUEST_MACHINE';
const VFTP_COMPAT_SET_REQUEST_COMMAND= 'vftpCompat/VFTP_COMPAT_SET_REQUEST_COMMAND';
const VFTP_COMPAT_SET_REQEUST_START_DATE= 'vftpCompat/VFTP_COMPAT_SET_REQEUST_START_DATE';
const VFTP_COMPAT_SET_REQUEST_END_DATE= 'vftpCompat/VFTP_COMPAT_SET_REQUEST_END_DATE';
const VFTP_COMPAT_SET_DOWNLOAD_STATUS = 'vftpCompat/VFTP_COMPAT_SET_DOWNLOAD_STATUS';
const VFTP_COMPAT_CHECK_DOWNLOAD_STATUS = 'vftpCompat/VFTP_COMPAT_CHECK_DOWNLOAD_STATUS';
const VFTP_COMPAT_INIT_RESPONSE_LIST = 'vftpCompat/VFTP_COMPAT_INIT_RESPONSE_LIST';

export const vftpCompatInitAll = createAction(VFTP_COMPAT_INIT_ALL); //initialize....
export const vftpCompatSetRequestMachine = createAction(VFTP_COMPAT_SET_REQUEST_MACHINE); 	// machine
export const vftpCompatSetRequestCommand = createAction(VFTP_COMPAT_SET_REQUEST_COMMAND); 	// command
export const vftpCompatSetRequestStartDate = createAction(VFTP_COMPAT_SET_REQEUST_START_DATE); 	// startDate
export const vftpCompatSetRequestEndDate = createAction(VFTP_COMPAT_SET_REQUEST_END_DATE); 	// endDate
export const vftpCompatCheckDlStatus = createAction(VFTP_COMPAT_CHECK_DOWNLOAD_STATUS);
export const vftpCompatSetDlStatus = createAction(VFTP_COMPAT_SET_DOWNLOAD_STATUS);
export const vftpCompatInitResponseList = createAction(VFTP_COMPAT_INIT_RESPONSE_LIST);


const initialState = Map({
    requestCompletedDate: "",
    requestListCnt: 0,
    requestList: Map({
        fabNames: List[{}],
        machineNames: List[{}],
        command: "",
    }),
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

    [VFTP_COMPAT_INIT_ALL]: (state, action) => {
        return initialState;
    },

    [VFTP_COMPAT_SET_REQUEST_MACHINE]: (state, action) => {
        const { fabNames, machineNames } = action.payload;
        console.log("VFTP_COMPAT_SET_REQUEST_MACHINE");

        return state
            .setIn(["requestList","fabNames"], fabNames)
            .setIn(["requestList", "machineNames"], machineNames);
    },

    [VFTP_COMPAT_SET_REQUEST_COMMAND]: (state, action) => {
        const command = action.payload;
        console.log("VFTP_COMPAT_SET_REQUEST_COMMAND");
        return state.setIn(["requestList","command"], command);
    },

    [VFTP_COMPAT_SET_REQEUST_START_DATE]: (state, action) => {
        const startDate = action.payload;
        console.log("VFTP_COMPAT_SET_REQEUST_START_DATE");
        return state.set("startDate", startDate);
    },

    [VFTP_COMPAT_SET_REQUEST_END_DATE]: (state, action) => {
        const endDate = action.payload;
        console.log("SEARCH_SET_REQUEST_END_DATE");
        return state.set("endDate", endDate);
    },
    [VFTP_COMPAT_INIT_RESPONSE_LIST] : (state, action) => {
        return state.set("responseList", List([]))
            .set("requestListCnt", 0)
b            .set("downloadStatus", initialState.get("downloadStatus"))
    },

    [VFTP_COMPAT_SET_DOWNLOAD_STATUS] : (state, action) => {
        const { func, dlId, status, totalFiles, downloadFiles, downloadUrl } = action.payload;
        const downloadStatus = state.get("downloadStatus");
        console.log("downloadStatus", downloadStatus);

        //console.log("dlId", dlId);
        if(dlId !== undefined) {
            downloadStatus.dlId = dlId;
        }
        if(status !== undefined) {
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
        return state.set("downloadStatus", downloadStatus);
    },

    [VFTP_COMPAT_CHECK_DOWNLOAD_STATUS] : (state, action) => {

        const { func, dlId, status, totalFiles, downloadFiles, downloadUrl } = action.payload;
        const downloadStatus = state.get("downloadStatus");

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
        return state.set("downloadStatus", downloadStatus);
    }

}, initialState)
