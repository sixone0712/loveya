export const getLogInfoList = (props) => {
    const { logInfoList } = props;
    return logInfoList.toJS();
};

export const checkLogInfoList = (props, idx) => {
    const { viewListActions } = this.props;
    return viewListActions.viewCheckLogtypeList(idx);
};

const checkAllLogInfoList = (props, isAllChecked) => {
    console.log("checkFileCatList");
    console.log("check", check);
    const { viewListActions } = this.props;
    if(isAllChecked === true) {
        viewListActions.viewCheckAllLogtypeList(true);
    } else {
        viewListActions.viewCheckAllLogtypeList(false);
    }
};