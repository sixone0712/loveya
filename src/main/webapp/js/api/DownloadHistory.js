import services from "../services";
import * as Define from '../define';

export const addDlHistory = (type,filename, status) => {
    const history = new Object();
    history.type = type;
    history.status = status;
    history.filename = filename;
    services.axiosAPI.post(Define.REST_API_URL+"/dlHistory/addDlHistory", history).then(r => (r === true)?console.log("history db update success"): console.log("history db update fail"));
    return 0;
};

export const addAutoDlHistory = (props, res) => {
//    services.axiosAPI.post(Define.REST_API_URL+"/dlHistory/addDlHistory", jsonList).then(r => (r === true)?console.log("history db update success"): console.log("history db update fail"));
    return 0;
};
export const loadDlHistoryList = (props) => {
    const { dlHistoryAction } = props;
    return dlHistoryAction.loadDlHistoryList(`${Define.REST_API_URL}/dlHistory/getHistoryList`);

};
export const getDlHistoryList = (props) => {
    const { dlHistoryInfo } = props;
    return dlHistoryInfo.toJS().dl_list;
};
export const getDlHistoryTotalCnt = (props) => {
    const { totalCnt } = props.dlHistoryInfo;
    return totalCnt;
};
export const getDlHistoryErrorCode = (props) => {
    const { dlHistoryInfo } = props;
    return dlHistoryInfo.toJS().result;
};
