export const getToolInfoList = (props) => {
    const { toolInfoList } = props;
    return toolInfoList.toJS();
};

export const checkToolInfoList = (props, keyIndex) => {
    const { viewListActions } = props;
    return viewListActions.viewCheckToolList(keyIndex);
};

export const checkAllToolInfoList  = (props, isAllCheck) => {
    const { viewListActions } = props;

    if(isAllCheck === true) {
        viewListActions.viewCheckAllToolList(true);
    } else {
        viewListActions.viewCheckAllToolList(false);
    }
};