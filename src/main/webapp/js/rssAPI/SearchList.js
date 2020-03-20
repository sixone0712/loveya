export const getRequestList = (props) => {
    const { requestList } = props;
    return requestList.toJS();
};

export const setStartDate = (props, date) => {
    const { searchListActions } = props;
    searchListActions.searchSetStartDate(date);
};

export const setEndDate = (props, date) => {
    const { searchListActions } = props;
    searchListActions.searchSetEndDate(date);
};

export const setSearchList = (props) => {
    const { searchListActions } = props;
    const toolList = props.toolInfoList;
    const logInfoList = props.logInfoList;
    const startDate = props.startDate;
    const endDate = props.endDate;
    searchListActions.searchSetEndDate({ toolList, logInfoList, startDate, endDate });
};

