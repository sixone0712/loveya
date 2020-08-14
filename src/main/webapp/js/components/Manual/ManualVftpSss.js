import React, {useEffect} from "react";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as commandActions from "../../modules/command";
import * as sssActions from "../../modules/vftpSss";
import * as viewListActions from "../../modules/viewList";
import {Breadcrumb, BreadcrumbItem, Col, Container, Row} from "reactstrap";
import ScrollToTop from "react-scroll-up";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleDoubleUp} from "@fortawesome/free-solid-svg-icons";
import Machinelist from "../Manual/Machine/MachineList";
import Datesetting from "../Manual/VFTP/datesetting";
import Commandlist from "../Manual/VFTP/commandlist";
import Commandline from "../Manual/VFTP/commandline";
import Filelist from "./VFTP/filelist";
import Footer from "../Common/Footer";
import * as API from "../../api";
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

const modalMsglist ={
    cancel: "Are you sure want to cancel the search?",
    process: "Searching.....",
    confirm: "Do you want to execute the command?",
    complete: "Search was canceled.",
    ready:"",
}

function convertCommand (cmd, sDate, eDate) {
    const splitCmd = cmd.split('-');
    const command = splitCmd[0];
    const option = splitCmd.length > 1 ? `-${splitCmd[1]}` : "";
    const formatDate = 'YYYYMMDD_HHmmss';
    return `${command}-${moment(sDate).format(formatDate)}-${moment(eDate).format(formatDate)}${option}`;
}

const ManualVftpSss = ({
        dbCommandList,
        requestCommand,
        fromDate,
        toDate,
        toolInfoList,
        commandActions,
        sssActions,
        viewListActions }) =>
{
    const commandList = API.vftpConvertDBCommand(dbCommandList.toJS());

    useEffect(()=>{
        const init = async () => {
            await viewListActions.viewCheckAllToolList(false);
            await sssActions.vftpSssInitAll();
            await commandActions.commandInit();
            await commandActions.commandLoadList("/rss/api/vftp/command?type=vftp_sss");
        }
        init().then(r => r).catch(e => console.log(e));
    },[]);

    useEffect(()=>{
        const setCommand = async () => {
            const selectCmd = commandList.find(item => item.checked && item.cmd_type === "vftp_sss");
            const convCmd = convertCommand(selectCmd === undefined ? "" : selectCmd.cmd_name, fromDate, toDate);
            await sssActions.vftpSssSetRequestCommand(convCmd);
        }
        setCommand().then(r => r).catch(e => console.log(e));
    },[fromDate, toDate, commandList]);

    const checkRequest = () => {
        const newToolInfoList = toolInfoList.filter(item => item.get("checked") === true).toJS();
        const machineNames = newToolInfoList.map(item => item.targetname);
        const checkCmd = commandList.filter(item => item.checked === true);
        if (machineNames.length === 0) return Define.SEARCH_FAIL_NO_MACHINE;
        if (checkCmd.length === 0) return Define.SEARCH_FAIL_NO_COMMAND;
        if (fromDate.isAfter(toDate)) return Define.SEARCH_FAIL_DATE;
        return Define.RSS_SUCCESS;
    }

    const searchRequest = async () => {
        const newToolInfoList = toolInfoList.filter(item => item.get("checked") === true).toJS();
        const fabNames = newToolInfoList.map(item => item.structId);
        const machineNames = newToolInfoList.map(item => item.targetname);

        const reqData = {
            "command": requestCommand,
            "fabNames": fabNames,
            "machineNames": machineNames
        }

        try {
            await sssActions.vftpSssInitResponseList()
            const res = await services.axiosAPI.requestPost("/rss/api/vftp/sss", reqData);
            sssActions.vftpSssSetResponseList(res);
            return Define.RSS_SUCCESS
        } catch (e) {
            console.error(e)
            return Define.SEARCH_FAIL_SERVER_ERROR;
        }
    }

    const searchCancel = () => {
        services.axiosAPI.postCancel();
    }

    return (
        <>
            <Container className="rss-container vftp manual" fluid={true}>
                <Breadcrumb className="topic-path">
                    <BreadcrumbItem>Manual Download</BreadcrumbItem>
                    <BreadcrumbItem active>VFTP (SSS)</BreadcrumbItem>
                </Breadcrumb>
                <Row>
                    <Col className="machinelist"><Machinelist /></Col>
                    <Col><Commandlist cmdType={"vftp_sss"} /></Col>
                    <Col className="datesetting">
                      <Datesetting
                        from={fromDate}
                        FromDateChangehandler={sssActions.vftpSssSetRequestStartDate}
                        to={toDate}
                        ToDateChangehandler={sssActions.vftpSssSetRequestEndDate}
                      />
                    </Col>
                </Row>
                <Commandline
                    type ="sss/optional"
                    string={requestCommand}
                    modalMsglist={modalMsglist}
                    confirmfunc={checkRequest}
                    processfunc={searchRequest}
                    completeFunc={()=> {}}
                    cancelFunc={searchCancel}
                />
                <Filelist />
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
        dbCommandList: state.command.getIn(['command', "lists"]),
        requestCommand: state.vftpSss.getIn(['requestList', "command"]),
        fromDate: state.vftpSss.get('startDate'),
        toDate: state.vftpSss.get('endDate'),
        toolInfoList: state.viewList.get('toolInfoList'),
    }),
    (dispatch) => ({
        commandActions: bindActionCreators(commandActions, dispatch),
        sssActions: bindActionCreators(sssActions, dispatch),
        viewListActions: bindActionCreators(viewListActions, dispatch),
    })
)(ManualVftpSss);