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

export const setVftpComaptWatchDlStatus = (requestId, modalFunc) => {
    const timeoutVal = setTimeout(async (requestId, modalFunc) => {
        const downloadStatus = modalFunc.getDownloadStatus();
        let { func } = downloadStatus;
        if(func === null) return;

        let res;
        try {
            res = await services.axiosAPI.requestGet(`${Define.REST_VFTP_COMPAT_POST_DOWNLOAD}/${requestId}`);
            const { status } = res.data;
            if(status === "done" || res.data.status ==="error") {
                clearTimeout(func);
                func = null;
                modalFunc.closeProcessModal()
                if(status === "done") {
                    modalFunc.openCompleteModal();
                } else {
                    // 1. axios timeout
                    // 2. Respond error from /dl/status
                    // 3. Respond null from /dl/status
                    modalFunc.setErrorMsg(Define.FILE_FAIL_SERVER_ERROR)
                    modalFunc.openErrorModal();
                }
            }
        } catch (error) {
            console.error(error);
            modalFunc.setErrorMsg(Define.FILE_FAIL_SERVER_ERROR)
            modalFunc.openErrorModal();
        }

        modalFunc.setSearchListActions({
            func: func,
            dlId: res.data.donwloadId,
            status: res.data.status,
            totalFiles: res.data.totalFiles,
            downloadFiles: res.data.downloadedFiles,
            downloadUrl: res.data.downloadUrl
        });

        setWatchDlStatus(requestId, modalFunc);
    }, 500, requestId, modalFunc);

    return timeoutVal;
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
