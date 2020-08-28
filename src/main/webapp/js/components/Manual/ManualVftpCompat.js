
import React, {useEffect, useState} from "react";
import {connect, useDispatch} from "react-redux";
import {bindActionCreators} from "redux";
import * as API from "../../api";
import * as commandActions from "../../modules/command";
import * as CompatActions from "../../modules/vftpCompat";
import * as viewListActions from "../../modules/viewList";
import { Container, Row, Col, Breadcrumb, BreadcrumbItem } from "reactstrap";
import ScrollToTop from "react-scroll-up";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleDoubleUp, faExclamationCircle} from "@fortawesome/free-solid-svg-icons";
import Machinelist from "../Manual/Machine/MachineList";
import Commandlist from "../Manual/VFTP/commandlist";
import Datesetting from "../Manual/VFTP/datesetting";
import Commandline from "../Manual/VFTP/commandline";
import Footer from "../Common/Footer";
import moment from "moment";
import * as Define from "../../define";
import services from "../../services";

const scrollStyle = {
    backgroundColor: "#343a40",
    width: "40px",
    height: "40px",
    textAlign: "center",
    borderRadius: "3px",
    zIndex: "101"
};

function convertCommand (cmd, sDate,eDate) {
    let cmdString = '';
    const formatDate = 'YYYYMMDD_HHmmss';
    cmdString += (moment(sDate).format(formatDate) + '-'+moment(eDate).format(formatDate))
    cmdString += (cmd !== '') ? ('-' + cmd) :"";
    console.log("cmd: " + cmdString);
    return  cmdString + ".log";
}

const checkFunc = async (props) => {
    console.log("================checkFunc================");
    const {CompatActions} = props;
    const machines = props.toolInfoListCheckCnt;
    const fromDate = props.startDate;
    const toDate = props.endDate;
    const ret = {
        error: Define.RSS_SUCCESS,
        totalFiles: 0,
        downloadFiles: 0,
    };

    //if ----- Machine is no selected
    if (machines <= 0) {
        ret.error = Define.SEARCH_FAIL_NO_MACHINE;
    } else if (fromDate.isAfter(toDate)) {
        ret.error = Define.SEARCH_FAIL_DATE;
    } else {
        try {
            const res = await API.startVftpCompatDownload(props);
            if (res !== "") {
                CompatActions.vftpCompatSetDlStatus({
                    func: null,
                    dlId: res.downloadId,
                    status: res.status,
                    totalFiles: res.totalFiles,
                    downloadFiles: 0,
                    downloadUrl: ""
                })
                ret.totalFiles = res.totalFiles;
                ret.downloadFiles = 0;
            } else {
                console.log("startVftpCompatDownload requestID: 0 ");
            }
        } catch (error) {
            console.error(error);
            ret.error = Define.RSS_FAIL;
        }
    }
    return ret;
}

const statusCheckFunc = async (props) => {
    const {dlId} = props.downloadStatus;
    const {CompatActions} = props;
    const ret = {
        error:Define.RSS_SUCCESS,
        msg:"",
        status:"",
        url:"",
        totalFiles:0,
        downloadedFiles:0,
    }
    let res;
    if(dlId !== 0)
    {
        try {
            res = await services.axiosAPI.requestGet(`${Define.REST_VFTP_COMPAT_POST_DOWNLOAD}/${dlId}`);
            const {status, downloadId, totalFiles, downloadedFiles, downloadUrl} = res.data;
            console.log("[statusCheckFunc] status", status);
            console.log("[statusCheckFunc] totalFiles", totalFiles);
            console.log("[statusCheckFunc] downloadedFiles", downloadedFiles);
            ret.status = status;
            if (status === "error") {
                // when download status is error, go to network error page
                window.appHistory.replace(Define.PAGE_NEWORK_ERROR);
            } else if(status === "done"){
                ret.error=Define.RSS_SUCCESS;
                ret.url = downloadUrl;
            } else {
                ret.downloadedFiles = downloadedFiles;
            }
            CompatActions.vftpCompatSetDlStatus({
                func: null,
                dlId: downloadId,
                status: status,
                downloadFiles: downloadedFiles,
                downloadUrl: downloadUrl
            })
        } catch (error) {
            console.error(error);
        }
    }

    return ret;
}

const completeFunc = async (props) => {
    const {downloadUrl} = props.downloadStatus;
    const {CompatActions} = props;

    let res = 0;
    res = await services.axiosAPI.downloadFile(downloadUrl);
    console.log("res: ", res);
    if(res.result === Define.RSS_SUCCESS){
        await API.addDlHistory(Define.RSS_TYPE_VFTP_MANUAL_COMPAT ,res.fileName, "Download Completed")
    }
    else{
        await API.addDlHistory(Define.RSS_TYPE_VFTP_MANUAL_COMPAT ,res.fileName, "Download Fail");
    }
    CompatActions.vftpCompatSetDlStatus({
        func: null,
        dlId: "",
        status: "init",
        totalFiles: 0,
        downloadFiles: 0,
        downloadUrl: ""
    })
    return res;
}
const cancelFunc = async (props) => {
    const {downloadStatus, CompatActions} = props;
    let res = 0;
    console.log("============cancelFunc=============");
    if(downloadStatus.status == "done")
    {
        await API.addDlHistory(Define.RSS_TYPE_VFTP_MANUAL_COMPAT ,"unknown", "User Cancel")
        CompatActions.vftpCompatSetDlStatus({
            func: null,
            dlId: "",
            status: "init",
            totalFiles: 0,
            downloadFiles: 0,
            downloadUrl: ""
        })
    }
    else if(downloadStatus.dlId !== 0)
    {
        try {
            const res = await services.axiosAPI.requestDelete(Define.REST_VFTP_COMPAT_POST_DOWNLOAD + "/" + downloadStatus.dlId);
            console.log("res", res)
        } catch (error) {
            console.error(error);
        }
    }
    else
    {
        console.log("downloadStatus", downloadStatus)
    }
    return res;
}

const ManualVftpCompat = (props) => {
    const [command, setCommand] = useState(props.command);
    const [fromDate, setFromDate] = useState(props.startDate);
    const [toDate, setToDate] = useState(props.endDate);
    const dbCommand = API.vftpConvertDBCommand(props.dbCommand.get("lists").toJS());
    const modalMsgList ={
        cancel: "Are you sure want to cancel the download?",
        process: "downloading.....",
        confirm: "Do you want to execute the command?",
        complete: "Download Complete!",
        ready:"",
    }

    useEffect(()=>{
        console.log("==== ManualVftpCompat Command Update ====");
        const selectCmd = dbCommand.find(item => item.checked && item.cmd_type === "vftp_compat");
        setCommand(convertCommand(selectCmd === undefined ? "" : selectCmd.cmd_name, fromDate, toDate));
        API.vftpCompatSetRequestCommand(props, command);
        API.vftpCompatSetRequestEndDate(props, toDate);
        API.vftpCompatSetRequestStartDate(props, fromDate);
        console.log("=====================================");
    },[fromDate, toDate, dbCommand]);

    return (
        <>
            <Container className="rss-container vftp manual" fluid={true}>
                <Breadcrumb className="topic-path">
                    <BreadcrumbItem>Manual Download</BreadcrumbItem>
                    <BreadcrumbItem active>VFTP (COMPAT)</BreadcrumbItem>
                </Breadcrumb>
                <Row>
                    <Col><Machinelist/></Col>
                    <Col><Commandlist cmdType={"vftp_compat"}/></Col>
                    <Col>
                        <Datesetting from={fromDate} FromDateChangehandler={setFromDate}
                                     to={toDate} ToDateChangehandler={setToDate} />
                    </Col>
                </Row>
                <Commandline type ="compat/optional" string={command} modalMsglist={modalMsgList}
                             confirmfunc={() => checkFunc(props)}
                             processfunc={() => statusCheckFunc(props)}
                             completeFunc={()=> completeFunc(props)}
                             cancelFunc={()=> cancelFunc(props)}
                />
            </Container>
            <Footer/>
            <ScrollToTop showUnder={160} style={scrollStyle}>
                <span className="scroll-up-icon"><FontAwesomeIcon icon={faAngleDoubleUp} size="lg"/></span>
            </ScrollToTop>
        </>
    );
};


export default connect(
    (state) => ({
        equipmentList: state.viewList.get('equipmentList'),
        toolInfoList: state.viewList.get('toolInfoList'),
        toolInfoListCheckCnt: state.viewList.get('toolInfoListCheckCnt'),
        startDate:state.vftpCompat.get('startDate'),
        endDate:state.vftpCompat.get('endDate'),
        command:state.vftpCompat.getIn(['requestList', "command"]),
        downloadStatus: state.vftpCompat.get('downloadStatus'),
        dbCommand: state.command.get('command'),
    }),
    (dispatch) => ({
        commandActions: bindActionCreators(commandActions, dispatch),
        CompatActions: bindActionCreators(CompatActions, dispatch),
        viewListActions: bindActionCreators(viewListActions, dispatch),
    })
)(ManualVftpCompat);