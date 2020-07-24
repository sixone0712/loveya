import React, {Component} from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTerminal} from "@fortawesome/free-solid-svg-icons";
import ReactTransitionGroup from "react-addons-css-transition-group";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as autoPlanActions from "../../modules/autoPlan";
import {Col, FormGroup, Label} from "reactstrap";
import moment from "moment";
import * as Define from "../../define";

class RSSautoplanchecksetting extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isModalOpen: false
        };
    }

    openModal = () => {
        this.setState({ isModalOpen: true });
    }

    closeModal = () => {
        this.setState({ isModalOpen: false });
    }

    render() {
        const { autoPlan, toolInfoListCheckCnt, logInfoListCheckCnt, type } = this.props;
        const { planId, collectType, interval, intervalUnit, from, to, collectStart, description } = autoPlan.toJS();

        return (
            <>
                <CommandList isOpen={this.state.isModalOpen} closer={this.closeModal} />
                <div className="form-section checksetting">
                    <Col className="pd-0">
                        <div className="form-header-section">
                            <div className="form-title-section">
                                Check Settings
                                <p>Check your plan settings.</p>
                            </div>
                        </div>
                        <div className="dis-flex align-center">
                            <FormGroup className={"auto-plan-checklist-form-group" + (type === "FTP" ? "" : " vftp")}>
                                <FormGroup>
                                    <Label>Plan ID</Label>
                                    <div className="setting-info">{planId}</div>
                                </FormGroup>
                                <FormGroup>
                                    <Label>Description</Label>
                                    <div className="setting-info">{description}</div>
                                </FormGroup>
                                <FormGroup>
                                    <Label>Period</Label>
                                    <div className="setting-info">
                                        {moment(from).format("YYYY-MM-DD HH:mm")} ~ {moment(to).format("YYYY-MM-DD HH:mm")}
                                    </div>
                                </FormGroup>
                                <FormGroup>
                                    <Label>Start</Label>
                                    <div className="setting-info">{moment(collectStart).format("YYYY-MM-DD HH:mm")}</div>
                                </FormGroup>
                                <FormGroup>
                                    <Label>Mode</Label>
                                    <div className="setting-info">
                                        { collectType === Define.AUTO_MODE_CONTINUOUS
                                            ? "Continous"
                                            : `Cycle / ${Number(interval)} ${intervalUnit}`
                                        }
                                    </div>
                                </FormGroup>
                                <FormGroup>
                                    <Label>Machine</Label>
                                    <div className="setting-info">{toolInfoListCheckCnt} Machines</div>
                                </FormGroup>
                                { type === "FTP" ? (
                                    <FormGroup>
                                        <Label>Target</Label>
                                        <div className="setting-info">{logInfoListCheckCnt} Targets</div>
                                    </FormGroup>
                                ) : (
                                    <>
                                        <FormGroup>
                                            <Label>Command</Label>
                                            <div className="setting-info">2 Commands</div>
                                        </FormGroup>
                                        <FormGroup>
                                            <Label>Command to execute</Label>
                                            <div
                                                className="setting-info execute-command-list"
                                                onClick={this.openModal}
                                            >
                                                Click for details.
                                            </div>
                                        </FormGroup>
                                    </>
                                )}
                            </FormGroup>
                        </div>
                    </Col>
                </div>
            </>
        );
    }
}

const CommandList = ({ isOpen, closer }) => {
    return (
        <>
            {isOpen ? (
                <ReactTransitionGroup
                    transitionName={"Custom-modal-anim"}
                    transitionEnterTimeout={200}
                    transitionLeaveTimeout={200}
                >
                    <div className="Custom-modal-overlay" onClick={closer} />
                    <div className="Custom-modal auto-plan-alert-modal execute-command-list">
                        <p className="title">List of commands to execute</p>
                        <div className="content-with-title">
                            <ul>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                                <li>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    TEST_1234
                                </li>
                            </ul>
                        </div>
                        <div className="button-wrap">
                            <button className="auto-plan alert-type" onClick={closer}>
                                Close
                            </button>
                        </div>
                    </div>
                </ReactTransitionGroup>
            ) : (
                <ReactTransitionGroup
                    transitionName={"Custom-modal-anim"}
                    transitionEnterTimeout={200}
                    transitionLeaveTimeout={200}
                />
            )}
        </>
    );
};

export default connect(
    (state) => ({
        autoPlan: state.autoPlan.get('autoPlan'),
        toolInfoList: state.viewList.get('toolInfoList'),
        logInfoList: state.viewList.get('logInfoList'),
        toolInfoListCheckCnt: state.viewList.get('toolInfoListCheckCnt'),
        logInfoListCheckCnt: state.viewList.get('logInfoListCheckCnt'),
    }),
    (dispatch) => ({
        autoPlanActions: bindActionCreators(autoPlanActions, dispatch),
    })
)(RSSautoplanchecksetting);
