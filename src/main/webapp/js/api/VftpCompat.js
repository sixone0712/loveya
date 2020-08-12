import services from "../services";
import * as Define from "../define";

import axios from "axios";
import * as CompatActions from "../modules/vftpCompat";
import * as viewListActions from "../modules/viewList";
import * as commandActions from "../modules/command";

//vftp
export const getRequestList = (props) => {
    const { requestList } = props;
    return requestList.toJS();
};

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



export const requestDownload = async (props) => {
    const { responseList } = props;
    const responseListJS = responseList.toJS();

    const downloadList = responseListJS.reduce((acc, cur, idx) => {
        if (cur.checked) acc.push({
            fabName: cur.structId,
            machineName: cur.targetName,
            categoryName: cur.logName,
            fileName: cur.fileName,
            fileSize: cur.fileSize,
            fileDate: cur.fileDate,
            file: cur.file,
        });
        return acc;
    }, []);

    //console.log("downloadList", downloadList);

    const requestList = {
        lists: downloadList
    }
    //console.log("requestList", requestList);


};


export const convertDateFormat = (date) => {
    if(date == "" || date == null) return "0000/00/00 00:00:00";

    const year = date.substr(0,4);
    const month = date.substr(4,2);
    const day = date.substr(6,2);
    const hour = date.substr(8,2);
    const min = date.substr(10,2);
    const sec = date.substr(12,2);

    return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
};

export const setRowsPerPage = (props, page) => {
    const { searchListActions } = props;
    searchListActions.searchSetResponsePerPage(page);
};

export const bytesToSize = (bytes) => {
    var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    if (bytes == 0) return 'n/a';
    var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
    if (i == 0) return bytes + ' ' + sizes[i];
    return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];
};


export const setDownload = (props) => {
    const { responseList } = props;
    const responseListJS = responseList.toJS();
    //console.log("responseListJS", responseListJS);

    const downloadList = responseListJS.reduce((acc, cur, idx) => {
        if (cur.checked) acc.push({
            structId: cur.structId,
            machine: cur.targetName,
            category: cur.logId,
            file: cur.fileName,
            filesize: String(cur.fileSize),
            date: cur.fileDate,
        });
        return acc;
    }, []);

    const jsonList = new Object();
    jsonList.list = downloadList;

    return jsonList;
};

export const setWatchDlStatus = (requestId, modalFunc) => {
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
            item.cmd_name = item.cmd_name.includes("-%s-%s-")
              ? item.cmd_name.replace("-%s-%s-", "")
              : item.cmd_name.replace("-%s-%s", "")
        }
        return item;
    });
}
