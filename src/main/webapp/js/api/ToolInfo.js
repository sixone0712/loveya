export const getToolInfoList = (props) => {
    const { toolInfoList } = props;
    return toolInfoList.toJS();
};

export const checkToolInfoList = (props, idx) => {
    const { viewListActions } = props;

    console.log("checkToolInfoList");
    console.log("props", props);
    console.log("viewListActions", viewListActions);
    console.log("idx", idx);
    viewListActions.viewCheckToolList(idx);
};

export const checkAllToolInfoList  = (props, isAllCheck) => {
    const { viewListActions } = props;

    if(isAllCheck === true) {
        viewListActions.viewCheckAllToolList(true);
    } else {
        viewListActions.viewCheckAllToolList(false);
    }
};