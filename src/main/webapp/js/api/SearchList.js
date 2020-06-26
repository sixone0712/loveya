import services from "../services";
import * as Define from "../define";
import axios from "axios";

export const getRequestList = (props) => {
    const { requestList } = props;
    return requestList.toJS();
};

export const setStartDate = (props, date) => {
    const { searchListActions } = props;
    searchListActions.searchSetRequestStartDate(date);
};

export const setEndDate = (props, date) => {
    const { searchListActions } = props;
    searchListActions.searchSetRequestEndDate(date);
};

export const setSearchList = async (props) => {
    const { searchListActions } = props;
    const toolList = props.toolInfoList;
    const logInfoList = props.logInfoList;
    const startDate = props.startDate;
    const endDate = props.endDate;
    let error = Define.RSS_SUCCESS;

    //if(props.toolInfoListCheckCnt <= 0 && props.logInfoListCheckCnt <= 0) {
    if(props.toolInfoListCheckCnt <= 0 || props.logInfoListCheckCnt <= 0) {
        error = Define.SEARCH_FAIL_NO_MACHINE_AND_CATEGORY
    //} else if(props.toolInfoListCheckCnt <= 0) {
        //error = Define.SEARCH_FAIL_NO_MACHINE
    //} else if(props.logInfoListCheckCnt <= 0) {
        //error = Define.SEARCH_FAIL_NO_CATEGORY
    } else if(startDate.isAfter(endDate)) {
        error = Define.SEARCH_FAIL_DATE
    } else {
        await searchListActions.searchSetRequestList({toolList, logInfoList, startDate, endDate});
    }

    //startSearchList(props);
    return error;
};

export const startSearchList = (props) => {
    const { searchListActions } = props;
    const { requestList } = props;
    searchListActions.searchInitResponseList();
    searchListActions.searchLoadResponseList(Define.REST_API_URL + "/soap/createFileList", requestList.toJS());
};

export const getResponseList = (props) => {
    const { responseList } = props;
    return responseList.toJS();
};

export const getResponseListCnt = (props) => {
    const { responseListCnt } = props;
    return responseListCnt;
};

export const checkResponseList = (props, idx) => {
    const { searchListActions } = props;
    searchListActions.searchCheckResponseList(idx);
};

export const checkAllResponseList = (props, isAllChecked) => {
    const { searchListActions } = props;
    if(isAllChecked === true) {
        searchListActions.searchCheckALLResponseList(true);
    } else {
        searchListActions.searchCheckALLResponseList(false);
    }
};

export const requestDownload = async (props) => {
    const { responseList } = props;
    const responseListJS = responseList.toJS();
    //console.log("responseListJS", responseListJS);

    const downloadList = responseListJS.reduce((acc, cur, idx) => {
        if (cur.checked) acc.push({
            structId: cur.structId,
            machine: cur.targetName,
            category: cur.logId,
            categoryName: cur.logName,
            file: cur.fileName,
            filesize: String(cur.fileSize),
            date: cur.fileDate,
            isFile: cur.file,
        });
        return acc;
    }, []);

    const jsonList = new Object();
    jsonList.list = downloadList;

    //console.log("downloadList", downloadList);
    //console.log("jsonList", jsonList);

    const result = await services.axiosAPI.post(Define.REST_API_URL + "/dl/request", jsonList)
        .then((data) => {/*console.log("data", data);*/ return  data.data})
        .catch((error) => {
            console.log("[startDownload]error", error);
            return Define.GENRE_SET_FAIL_SEVER_ERROR;
        });

    console.log("result", result);

    return result;
};


export const convertDateFormat = (date) => {
    if(date == "" || date == null) return "0000/00/00 00:00:00";

    const year = date.substr(0,4);
    const month = date.substr(4,2);
    const day = date.substr(6,2);
    const hour = date.substr(8,2);
    const min = date.substr(10,2);
    const sec = date.substr(12,2);

    return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
};

export const setRowsPerPage = (props, page) => {
    const { searchListActions } = props;
    searchListActions.searchSetResponsePerPage(page);
};

export const bytesToSize = (bytes) => {
    var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    if (bytes == 0) return 'n/a';
    var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
    if (i == 0) return bytes + ' ' + sizes[i];
    return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];
};


export const setDownload = (props) => {
    const { responseList } = props;
    const responseListJS = responseList.toJS();
    //console.log("responseListJS", responseListJS);

    const downloadList = responseListJS.reduce((acc, cur, idx) => {
        if (cur.checked) acc.push({
            structId: cur.structId,
            machine: cur.targetName,
            category: cur.logId,
            file: cur.fileName,
            filesize: String(cur.fileSize),
            date: cur.fileDate,
        });
        return acc;
    }, []);

    const jsonList = new Object();
    jsonList.list = downloadList;

    return jsonList;
};

export const setWatchDlStatus = (requestId, modalFunc) => {
    const timeoutVal = setTimeout(async (requestId, modalFunc) => {
        const downloadStatus = modalFunc.getDownloadStatus();
        let { func } = downloadStatus;
        if(func === null) return;

        const res = await services.axiosAPI.get(Define.REST_API_URL + "/dl/status?dlId=" + requestId)
            .then(res => res)
            .catch(res => {let newRes = { data: { status: "timeOut"}};  return newRes});

        if(res.data.status === "done" || res.data.status ==="error" || res.data.status === "timeOut" || res.data === null) {
            clearTimeout(func);
            func = null;
            modalFunc.closeProcessModal();
            if(res.data.status === "done") {
                modalFunc.openCompleteModal();
            } else {
                // 1. axios timeout
                // 2. Respond error from /dl/status
                // 3. Respond null from /dl/status
                modalFunc.setErrorMsg(Define.FILE_FAIL_SERVER_ERROR)
                modalFunc.openErrorModal();
            }
        }

        modalFunc.setSearchListActions({
            func: func,
            dlId: res.data.dlId,
            status: res.data.status,
            totalFiles: res.data.totalFiles,
            downloadFiles: res.data.downloadFiles
        });

        setWatchDlStatus(requestId, modalFunc);
    }, 500, requestId, modalFunc);

    return timeoutVal;
};

export const setWatchSearchStatus = (props) => {
    const timeoutVal = setTimeout( (props) => {
        const { closeProcessModal, getIntervalFunc, setIntervalFunc, getResStatus, openErrorModal, onSetErrorState } = props;
        const intervalFunc = getIntervalFunc();
        const resStatus = getResStatus();

        // when setTimeout is null
        if(intervalFunc == null) return;
        //console.log("intervalFunc", intervalFunc);

        console.log("resStatus", resStatus);
        if(resStatus === "success" || resStatus === "error") {
            clearTimeout(intervalFunc);
            setIntervalFunc(null);
            closeProcessModal();
            // when response status is error, open error popup
            if(resStatus === "error") {
                onSetErrorState(Define.SEARCH_FAIL_SERVER_ERROR);
                openErrorModal();
            }
        }
        setWatchSearchStatus(props);
    }, 200, props);

    return timeoutVal;
};
