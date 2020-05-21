
export const getDBDwHistoryList = (props) => {
    const { dwHistoryAction } = props;
    return dwHistoryAction.loadDwHistoryList(`/dwHistory/getHistoryList`);

};
export const getDwHistoryList = (props) => {
    const { dwHistoryInfo } = props;
    return dwHistoryInfo.toJS().dw_list;
};
export const getDwHistoryTotalCnt = (props) => {
    const { totalCnt } = props.dwHistoryInfo;
    return totalCnt;
};


export const getDwHistoryErrorCode = (props) => {
    const { dwHistoryInfo } = props;
    return dwHistoryInfo.toJS().result;
};
