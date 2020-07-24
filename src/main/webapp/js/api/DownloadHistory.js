import services from "../services";
import * as Define from '../define';

export const addDlHistory = async (type, filename, status) => {
    const history = {
        type: type,
        status: status,
        filename: filename
    }

    try {
        await services.axiosAPI.requestPost(Define.REST_HISTORIES_POST_DOWNLOAD_ADD, history)
        console.log("history db update success")
    } catch(e) {
        console.error("history db update fail")
        console.error(e)
    }
};

export const addAutoDlHistory = (props, res) => {
//    services.axiosAPI.requestPost(Define.REST_API_URL+"/dlHistory/addDlHistory", jsonList).then(r => (r === true)?console.log("history db update success"): console.log("history db update fail"));
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
