export const getLogInfoList = (props) => {
    const { logInfoList } = props;
    return logInfoList.toJS();
};

export const checkLogInfoList = (props, idx) => {
    const { viewListActions } = props;
    viewListActions.viewCheckLogTypeList(idx);
};

export const checkAllLogInfoList = (props, isAllChecked) => {
    const { viewListActions } = props;
    if(isAllChecked === true) {
        viewListActions.viewCheckAllLogTypeList(true);
    } else {
        viewListActions.viewCheckAllLogTypeList(false);
    }
};