import services from "../services";
import * as Define from "../define";

export const vftpCompatInitAll = (props) => {
    const {CompatActions, commandActions, viewListActions} = props;
    viewListActions.viewCheckAllToolList(false);
    CompatActions.vftpCompatInitAll();
    commandActions.commandInit();
    commandActions.commandLoadList("/rss/api/vftp/command?type=vftp_compat");
}
export const vftpCompatSetRequestMachine = (props, machine) => {
    const { CompatActions } = props;
    CompatActions.vftpCompatSetRequestMachine(date);
};
export const vftpCompatSetRequestStartDate = (props, date) => {
    const { CompatActions } = props;
    CompatActions.vftpCompatSetRequestStartDate(date);
};

export const vftpCompatSetRequestEndDate = (props, date) => {
    const { CompatActions } = props;
    CompatActions.vftpCompatSetRequestMachine(date);
};

export const vftpCompatSetRequestCommand = (props,command) => {
    const { CompatActions } = props;
    CompatActions.vftpCompatSetRequestCommand(command);
};

export const startVftpCompatDownload = async (props) => {
    const {CompatActions, toolInfoList} = props;
    const {startDate, endDate, command} = props;

    const toolInfoListJS = toolInfoList.toJS();

    console.log("toolInfoList", toolInfoList);
    console.log("toolInfoListJS", toolInfoListJS);
    console.log("startDate", startDate);
    console.log("endDate", endDate);

    const newToolInfoList = toolInfoListJS.filter(item => item.checked === true);
    const fabNames = newToolInfoList.map(item => item.structId);
    const machineNames = newToolInfoList.map(item => item.targetname);
    const newRequestList = {
        fabNames: fabNames,
        machineNames: machineNames,
        command: command,
    };
    console.log("fabNames", fabNames);
    console.log("machineNames", machineNames);
    console.log("command", command);
    console.log("newRequestList", newRequestList);

    CompatActions.vftpCompatInitResponseList();
    try {
        const response = await services.axiosAPI.requestPost(Define.REST_VFTP_COMPAT_POST_DOWNLOAD, newRequestList)
        const {downloadId} = response.data;
        console.log("[requestDownload]donwloadId", downloadId);
        return downloadId;
    } catch (error) {
        console.error(error);
        return "";
    }
};

export const vftpConvertDBCommand = (cmdList) => {
    return cmdList.map(item => {
        if (item.cmd_type == "vftp_compat") {
            item.cmd_name = item.cmd_name.includes("%s-%s-")
              ? item.cmd_name.replace("%s-%s-", "")
              : item.cmd_name.replace("%s-%s", "");
        } else {
            item.cmd_name = item.cmd_name.replace("-%s-%s", "");
        }
        return item;
    });
}
