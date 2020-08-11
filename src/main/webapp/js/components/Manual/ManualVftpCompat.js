
import React, {useEffect, useState} from "react";
import {connect, useDispatch} from "react-redux";
import {bindActionCreators} from "redux";
import * as API from "../../api";
import * as CmdActions from "../../modules/Command";
import * as CompatActions from "../../modules/vftpCompat";
import * as viewListActions from "../../modules/viewList";
import { Container, Row, Col, Breadcrumb, BreadcrumbItem } from "reactstrap";
import ScrollToTop from "react-scroll-up";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import { faAngleDoubleUp } from "@fortawesome/free-solid-svg-icons";
import Machinelist from "../Manual/Machine/MachineList";
import Commandlist from "../Manual/VFTP/commandlist";
import Datesetting from "../Manual/VFTP/datesetting";
import Commandline from "../Manual/VFTP/commandline";
import Footer from "../Common/Footer";
import moment from "moment";
import * as Define from "../../define";
import {modalType} from "./Search/FormList";

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

const downloadFunc = async (props) => {
    const {command, startDate, endDate, toolInfoListCheckCnt} = props;
    console.log("================Modal================");
    console.log("downloadFunc");
    let requestID = 0;
    //if ----- Machine is no selected
    if (toolInfoListCheckCnt <= 0) {
        console.log("checked machine: ", toolInfoListCheckCnt);
    } else if (startDate.isAfter(endDate)) {
        console.log("====startDate.isAfter(endDate)====");
        console.log("startDate: ", startDate);
        console.log("endDate: ", endDate);
    } else {
        //await openModal(modalType.PROCESS);
        requestID = API.startVftpCompatDownload(props);
        if (requestID !== "") {

        } else {
            //await CompatActions.vftpCompatCheckDlStatus({toolList, logInfoList, startDate, endDate});
            //API.startVftpCompatDownload(props).then(r => );
        }
    }
}

const ManualVftpCompat = (props) => {
    const [command, setCommand] = useState(props.command);
    const [fromDate, setFromDate] = useState(props.startDate);
    const [toDate, setToDate] = useState(props.endDate);


    useEffect(()=>{
        console.log("====ManualVftpCompat initialized=====");
        API.initializeCmd(props);
        API.vftpCompatInitAll(props);
        console.log("=====================================");
        },[]);

    useEffect(()=>{
        console.log("==== ManualVftpCompat Command Update ====");
        setCommand(convertCommand("",fromDate,toDate));
        API.vftpCompatSetRequestCommand(props,command);
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
                    <Col><Commandlist /></Col>
                    <Col className="datesetting">
                        <Datesetting from={fromDate} handleChangeFromDate={setFromDate}
                                     to={toDate} handleChangeToDate={setToDate} />
                    </Col>
                </Row>
                <Commandline type ="compat/optional" string={command} func={()=>downloadFunc(props)}/>
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
        CommandInfo: state.cmd.get('CommandInfo'),
        CommandList: state.cmd.get('CommandList'),
        equipmentList: state.viewList.get('equipmentList'),
        toolInfoList: state.viewList.get('toolInfoList'),
        toolInfoListCheckCnt: state.viewList.get('toolInfoListCheckCnt'),
        startDate:state.vftpCompat.get('startDate'),
        endDate:state.vftpCompat.get('endDate'),
        command:state.vftpCompat.getIn(['requestList', "command"]),
    }),
    (dispatch) => ({
        CmdActions: bindActionCreators(CmdActions, dispatch),
        CompatActions: bindActionCreators(CompatActions, dispatch),
        viewListActions: bindActionCreators(viewListActions, dispatch),
    })
)(ManualVftpCompat);