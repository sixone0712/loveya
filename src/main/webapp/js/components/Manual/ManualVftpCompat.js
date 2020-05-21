import React, {Component} from "react";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as CmdActions from "../../modules/Command";
import CommandInfo from "../../modules/Command"
import {
    Container,
    Row,
    Col,
    CardBody,
    FormGroup,
    Button,
    Card,
    Collapse,
    Input,
    ButtonToggle,
} from "reactstrap";
import Footer from "../Common/Footer";
import ScrollToTop from "react-scroll-up";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faAngleDoubleUp,
    faPencilAlt,
    faPlus,
    faMinus,
    faTrashAlt,
    faExclamationCircle
} from "@fortawesome/free-solid-svg-icons";
import DatePicker from "./Search/DatePicker";
import ReactTransitionGroup from "react-addons-css-transition-group";
import ModalTwoButton from "../Common/ModalTwoButton";
import Filelist from "./File/FileList";
import * as API from "../../api";
import cmd from "../../modules/Command";
import AlertModal from "../Common/AlertModal";
import ConfirmModal from "../Common/ConfirmModal";
import RadioGroup from "antd/es/radio/group";
import Radio from "antd/es/radio";
import FormControlLabel from '@material-ui/core/FormControlLabel';
import DateForm from "../Form/DateForm";
import moment from "moment";


const scrollStyle = {
    backgroundColor: "#343a40",
    width: "40px",
    height: "40px",
    textAlign: "center",
    borderRadius: "3px",
    zIndex: "101"
};

const CommandAddModal = ({ isOpen, left,right , chgfunc, errorMsg}) => {
    const buttonMsg = {
        leftMsg:'No',
        rightMsg:'Yes',
    };

    return (

        <React.Fragment>
            {
                isOpen ? (
                    <ReactTransitionGroup
                        transitionName={'Modal-anim'}
                        transitionEnterTimeout={200}
                        transitionLeaveTimeout={200} >
                        <div className="Custom-modal-overlay" onClick={close} />
                        <div className="Custom-modal">
                            <p className="title">Add Command</p>
                            <div className="content-with-title">
                                <FormGroup>
                                    <Input type="text" name= "cmdEditName" placeholder="Please enter new Command" className="catlist-modal-input my-2"
                                    onChange={(e) => {const value = e.target.value; chgfunc(value)}}/>
                                    {errorMsg.length > 0 &&
                                    <span className="text-red-700 uppercase font-bold text-xxs">{errorMsg}</span>}
                                </FormGroup>
                            </div>
                            <ModalTwoButton data={buttonMsg} actionRightFunc={right} actionLeftFunc={left}/>
                        </div>
                    </ReactTransitionGroup>
                ):(
                    <ReactTransitionGroup transitionName={'Modal-anim'} transitionEnterTimeout={200} transitionLeaveTimeout={200} />
                )
            }
        </React.Fragment>
    )
}

function convertCommand (cmd, sDate,eDate) {
    let cmdString = '';
    const formatDate = 'YYYYMMDD_HHmmss';

    if (sDate) {
        cmdString += ( moment(sDate).format(formatDate)+ '-');
    }
    if (eDate) {
        cmdString += ( moment(eDate).format(formatDate));
    }
    if (cmd) {
        cmdString +=( '-' + cmd);
    }
    return "#get " + cmdString+ ".log";
}
const CommandArea = ({ id, startDate, endDate}) => {
    const cmdString = convertCommand(id,startDate,endDate);
    return (
            <Card className="ribbon-wrapper formlist-card">
                <CardBody className="custom-scrollbar manual-cmd-body">
                    <div className="ribbon ribbon-clip ribbon-gray_blue">Command</div>
                      <div style={{display: "flex", marginLeft: "auto", justifyContent:"flex-end", fontSize:"1.5em", alignItems:"center"}}>
                          {cmdString}
                          <Button
                        outline size="l"
                        className="filelist-card mx-4">
                        Download
                        </Button>
                      </div>
                </CardBody>
            </Card>
    );
}
class ManualVftpCompat extends Component {
    constructor() {
        super();
        this.state= {
            cmdName:"",
            cmdEditName:"",
            editShow: false,
            isModalOpen : false,
            isMode:"",
            errMsg:"",
            selected:'0',
        }
}

    openModal =(sMode) => {
        this.setState(() => ({isModalOpen: true, isMode:sMode}));
    }
    closeModal = () => {
        this.setState(() => ({isModalOpen: false,isMode:"",errMsg:""}));
    }
    ChangeHandle = (cmd) => {
        this.setState(() => ({...this.state,cmdEditName : cmd}));
    };

    handleCmdToggle = () => {
        if (this.state.editShow) {
            this.setState({
                editShow: !this.state.editShow,
            });

        } else {
            this.setState({
                editShow: !this.state.editShow
            });
        }
    };
    AddCommand = async () => {
        console.log(this.state.cmdEditName);
        await API.addCommand(this.props, this.state.cmdEditName, "1");
        const errCode= API.getCmdErrCode(this.props);
        console.log("errCode", errCode);
        if(errCode !== 0 )
        {
            let msg = API.getErrorMsg(errCode);

            if (msg.length > 0) {
                this.setState({
                    ...this.state,
                    errMsg: msg,
                })
            }
        }
        else
        {
            this.closeModal(); //add commmand modal Close
            await API.getDBCommandList(this.props,1);
        }
    };
    EditCommand = (e) => {
        console.log("[VFT .....] EditCommand");
        console.log(this.state.cmdEditName);

    };
    DeleteCommand = async (e) => {
        console.log("[VFT .....] DeleteCommand");
        console.log(this.state.selected);
        await API.deleteCommand(this.props, this.state.selected);
        const errCode= API.getCmdErrCode(this.props);
        console.log("errCode", errCode);
        if(errCode !== 0 )
        {
            let msg = API.getErrorMsg(errCode) ;
            if (msg.length > 0) {
                this.setState({
                    ...this.state,
                    errMsg: msg,
                    isModalOpen: true,
                    isMode: "ErrorModal",
                })
            }
        }
        else
        {
            this.handleRadio('0');
            this.closeModal(); //delete commmand modal Close
            await API.getDBCommandList(this.props,1);
        }

    };
    handleRadio = (value) => {
        this.setState(() => (
            {...this.state,
                selected : value,
                cmdName:(value > 0)?API.getCmdName(this.props, value):''
            })
        );
    }

    onStartDateChanage = startDate => {
        API.setCmdStartDate(this.props, startDate);
        console.log("startDate : ",startDate);
    };

    onEndDateChanage = endDate => {
        API.setCmdEndDate(this.props, endDate);
        console.log("endDate : ",endDate);
    };

    async componentDidMount() {
        console.log("componentDidMount");
        API.getDBCommandList(this.props,1);
    }

    render() {
        const {editShow} = this.state;
        const cmdlist  = API.getCmdList(this.props);
        const {startDate, endDate} = this.props;
        return (
            <>
            <Container className="rss-container" fluid={true}>
            <Row>
            <Col sm={8}>
                <Card className="ribbon-wrapper catlist-card">
                    <CardBody className="custom-scrollbar manual-card-body">
                        <div className="ribbon ribbon-clip ribbon-secondary">
                            Manual Download / [COMPAT/OPTIONAL] Command
                        </div>

                        <div className="card-btn-area">
                            <div style={{display: "flex", marginLeft: "auto", justifyContent:"flex-end"}}>
                            {
                                (editShow) &&
                                    <div>
                                    <Button outline size="sm" color="info" className="catlist-btn"
                                            onClick={() => this.openModal("AddCommand")}>  <FontAwesomeIcon icon={faPlus} /> </Button>
                                    <Button outline size="sm" color="info" className="catlist-btn"
                                            onClick={() => this.openModal("DeleteCommand")}>  <FontAwesomeIcon icon={faMinus} />  </Button>
                                    </div>
                            }
                            <ButtonToggle outline size="sm" color="info" className={"catlist-btn cmdlist-btn-toggle" + (editShow ? " active" : "")}
                                onClick={this.handleCmdToggle}
                            >
                               <FontAwesomeIcon icon={faPencilAlt} />
                            </ButtonToggle>
                            </div>
                        </div>
                        <div className = "card-body">
                            <RadioGroup className="dis-flex mode-section code"
                                       value ={this.state.selected}
                                       onChange={(e) => this.handleRadio(e.target.value)}
                                       onDoubleClick={(e)=> {this.openModal("EditCommand");} }
                            >
                                <div className="relative w-full">
                                <FormControlLabel
                                    className="px-2 py-2 border-b-1 placeholder-gray-400 text-gray-700 bg-white rounded text-xs shadow focus:shadow-outline w-full"
                                    type="radio"
                                    id="mode_continue"
                                    value ='0'
                                    name={"no select command"}
                                    label={"no select command"}
                                    control={<Radio />} />
                                </div>
                                {cmdlist.map((cat, key) => {
                                    return (
                                        <div className="relative w-full1">
                                        <FormControlLabel
                                            className="px-2 py-2 border-b-1 placeholder-gray-400 text-gray-700 bg-white rounded text-xs shadow focus:shadow-outline w-full"
                                            type="radio"
                                            key = {key}
                                            id={cat.id}
                                            value={cat.id}
                                            name={cat.cmd_name}
                                            label={cat.cmd_name}
                                            control={<Radio />}/>
                                        </div>
                                        );
                                    }
                                )}
                            </RadioGroup>
                        </div>
                    </CardBody>
                </Card>

                </Col>
            <Col sm={4}>
                <Card className="ribbon-wrapper formlist-card">
                    <CardBody className="custom-scrollbar manual-card-body">
                        <div className="ribbon ribbon-clip ribbon-success">Date</div>
                        <Col>
                            <FormGroup className="formlist-form-group">
                                <DateForm eDateChanageFunc={this.onEndDateChanage} sDateChanageFunc={this.onStartDateChanage}
                                          startDate={startDate} endDate={endDate}/>
                            </FormGroup>
                        </Col>
                    </CardBody>
                </Card>
            </Col>
            </Row>
            <CommandArea id={this.state.cmdName} startDate={startDate} endDate={endDate}/>
            </Container>
            <Footer/>
            <ScrollToTop showUnder={160} style={scrollStyle}>
            <span className="scroll-up-icon"><FontAwesomeIcon icon={faAngleDoubleUp} size="lg"/></span>
            </ScrollToTop>
                {
                    this.state.isMode ==='EditCommand'
                        ?<CommandAddModal isOpen={this.state.isModalOpen } left={this.AddCommand} right={this.closeModal} chgfunc={this.ChangeHandle} errorMsg={this.state.errMsg}/>
                        : this.state.isMode ==='AddCommand'
                        ? <CommandAddModal isOpen={this.state.isModalOpen } left={this.AddCommand} right={this.closeModal} chgfunc={this.ChangeHandle} errorMsg={this.state.errMsg}/>
                        : this.state.isMode ==='DeleteCommand'
                        ? <ConfirmModal isOpen={this.state.isModalOpen } icon={faTrashAlt} message={"Do you want to delete the selected command?"} style={"auto-plan"} actionBg={this.DeleteCommand} actionLeft={this.DeleteCommand} actionRight={this.closeModal} />
                        : this.state.isMode ==='ErrorModal'
                        ? <AlertModal isOpen={this.state.isModalOpen } icon={faExclamationCircle} message={this.state.errMsg} closer={this.closeModal} />
                        :  <ReactTransitionGroup transitionName={'Modal-anim'} transitionEnterTimeout={200} transitionLeaveTimeout={200} />

                }
            </>
    );
    }
}

export default connect(
    (state) => ({
        CommandInfo: state.cmd.get('CommandInfo'),
        CommandList: state.cmd.get('CommandList'),
        startDate:state.cmd.get('startDate'),
        endDate:state.cmd.get('endDate'),

    }),
    (dispatch) => ({
        CmdActions: bindActionCreators(CmdActions, dispatch),
    })
)(ManualVftpCompat);