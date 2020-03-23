import services from "../services";
import * as Define from "../define";

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

export const setSearchList = (props) => {
    const { searchListActions } = props;
    const toolList = props.toolInfoList;
    const logInfoList = props.logInfoList;
    const startDate = props.startDate;
    const endDate = props.endDate;
    searchListActions.searchSetRequestList({ toolList, logInfoList, startDate, endDate });
    //startSearchList(props);
};

export const startSearchList = (props) => {
    const { searchListActions } = props;
    const { requestList } = props;
    searchListActions.searchInitResponseList();
    searchListActions.searchLoadResponseList("api/createFileList", requestList.toJS());
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

export const startDownload = async (props) => {
    const { responseList } = props;
    const responseListJS = responseList.toJS();
    console.log("responseListJS", responseListJS);


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

    console.log("downloadList", downloadList);
    console.log("jsonList", jsonList);

    const result = await services.axiosAPI.postDownload("dl/request", jsonList)
        .then((data) => data.data)
        .catch((error) => {
            console.log("[startDownload]error", error);
            return Define.GENRE_SET_FAIL_SEVER_ERROR;
        });

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