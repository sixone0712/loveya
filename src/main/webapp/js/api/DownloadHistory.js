import services from "../services";
import * as Define from '../define';

export const addDlHistory = (type, filename, status) => {
    const history = {
        type: type,
        status: status,
        filename: filename
    }
    services.axiosAPI.post(Define.REST_HISTORIES_POST_DOWNLOAD_ADD, history)
      .then(r => console.log("history db update success"))
      .catch(e =>console.log("history db update fail"));
};

export const addAutoDlHistory = (props, res) => {
//    services.axiosAPI.post(Define.REST_API_URL+"/dlHistory/addDlHistory", jsonList).then(r => (r === true)?console.log("history db update success"): console.log("history db update fail"));
    return 0;
};
export const loadDlHistoryList = (props) => {
    const { dlHistoryAction } = props;
    return dlHistoryAction.loadDlHistoryList(Define.REST_HISTORIES_GET_DOWNLOAD_LIST);

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
