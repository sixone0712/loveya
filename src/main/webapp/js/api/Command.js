import * as Define from "../define";

export const getCmdErrCode = (props) => {
    const { CommandInfo } = props;
    return CommandInfo.toJS().cmdErrCode;
};

export const getCmdList = (props) => {
    const { CommandList } = props;
    return CommandList.toJS().list;
};

export const getCmdListCnt = (props) => {
    const { totalCnt } = props.CommandList;
    return totalCnt;
};

export const getCmdName = (props, id) => {
    const  {list}  = props.CommandList.toJS();
    if(id >0)
    {
        const find = list.find(item => item.id == id);
        return find.cmd_name;
    }
    return '';
};

export const addCommand = (props, name, type) => {
    const { CmdActions } = props;
    if(name.length <= 0) {
        return Define.COMMAND_FAIL_EMPTY_NAME;
    }
    return CmdActions.addCommand(`${Define.REST_API_URL}/cmd/add?cmd_name=${name}&cmd_type=${type}`);
};

export const deleteCommand = (props, id) => {
    const { CmdActions } = props;
    return CmdActions.deleteCommand(`${Define.REST_API_URL}/cmd/delete?id=${id}`);
};

export const updateCommand = (props, url) => {
    const { CmdActions } = props;
    return CmdActions.updateCommand(url);
};

export const getDBCommandList = (props, type) => {
    const { CmdActions } = props;
    return CmdActions.getCommandList(`${Define.REST_API_URL}/cmd/getList?cmd_type=${type}`);
};

export const setCmdStartDate = (props, date) => {
    const { CmdActions } = props;
    CmdActions.setCmdStartDate(date);
};

export const setCmdEndDate = (props, date) => {
    const { CmdActions } = props;
    CmdActions.setCmdEndDate(date);
};
export const initializeCmd = (props) =>{
    const { CmdActions } = props;
    CmdActions.initializeCmd();
}