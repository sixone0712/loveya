
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

    if (sDate) {
        cmdString += (moment(sDate).format(formatDate) + '-');
    }
    if (eDate) {
        cmdString += (moment(eDate).format(formatDate));
    }
    if (cmd) {
        cmdString += ('-' + "DE_TEST");
    }

    console.log("eDate" + eDate);
    console.log("sDate" + sDate);
    console.log("cmd: " + cmdString);
    return  cmdString + ".log";
}

const downloadFunc = (props) => {
    const machines= props.toolInfoListCheckCnt;
    const fromDate = props.startDate;
    const toDate = props.endDate;
    let error = Define.RSS_SUCCESS;
    console.log("================downloadFunc================");
    let requestID = 0;
    //if ----- Machine is no selected
    if (machines <= 0) {
        error = Define.SEARCH_FAIL_NO_MACHINE_AND_CATEGORY;
    } else if (fromDate.isAfter(toDate)) {
        error = Define.SEARCH_FAIL_DATE;
    } else {
        requestID = API.startVftpCompatDownload(props);
        if (requestID !== "") {
            console.log("requestId", requestID);
            CompatActions.vftpCompatSetDlStatus({dlId: requestID});
        } else {
            //await CompatActions.vftpCompatCheckDlStatus({toolList, logInfoList, startDate, endDate});
            //API.startVftpCompatDownload(props).then(r => );
        }
    }
    return error;
}

const statusCheckFunc = (props) => {
}

const ManualVftpCompat = (props) => {
    const [command, setCommand] = useState(props.command);
    const [fromDate, setFromDate] = useState(props.startDate);
    const [toDate, setToDate] = useState(props.endDate);
    const modalMsglist ={
        cancel: "Are you sure want to cancel the download?",
        process: "downloading.....",
        confirm: "Do you want to execute the command?",
        complete: "Download Complete!",
        ready:"",
    }
    useEffect(()=>{
        console.log("====ManualVftpCompat initialized=====");
        API.vftpCompatInitAll(props);
        console.log("=====================================");
        },[]);

    useEffect(()=>{
        console.log("==== ManualVftpCompat Command Update ====");
        setCommand(convertCommand("",fromDate,toDate));
        API.vftpCompatSetRequestCommand(props,command);
        API.vftpCompatSetRequestEndDate(props,toDate);
        API.vftpCompatSetRequestStartDate(props,fromDate);
        console.log("=====================================");
    },[fromDate,toDate]);

    return (
        <>
            <Container className="rss-container vftp manual" fluid={true}>
                <Breadcrumb className="topic-path">
                    <BreadcrumbItem>Manual Download</BreadcrumbItem>
                    <BreadcrumbItem active>VFTP (COMPAT)</BreadcrumbItem>
                </Breadcrumb>
                <Row>
                    <Col className="machinelist"><Machinelist/></Col>
                    <Col><Commandlist cmdType={"vftp_compat"}/></Col>
                    <Col className="datesetting">
                        <Datesetting from={fromDate} handleChangeFromDate={setFromDate}
                                     to={toDate} handleChangeToDate={setToDate} />
                    </Col>
                </Row>
                <Commandline type ="compat/optional" string={command} func={() => downloadFunc(props)} modalMsglist={modalMsglist}/>
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
        dbCommand: state.command.get('command'),
    }),
    (dispatch) => ({
        commandActions: bindActionCreators(commandActions, dispatch),
        CompatActions: bindActionCreators(CompatActions, dispatch),
        viewListActions: bindActionCreators(viewListActions, dispatch),
    })
)(ManualVftpCompat);