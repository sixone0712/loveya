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
    }

    render() {
        const { autoPlan, toolInfoListCheckCnt, logInfoListCheckCnt, type, command } = this.props;
        const { planId, collectType, interval, intervalUnit, from, to, collectStart, description } = autoPlan.toJS();
        const { checkedCnt } = command.toJS();
        return (
            <>
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
                                { type === Define.PLAN_TYPE_FTP ? (
                                    <FormGroup>
                                        <Label>Target</Label>
                                        <div className="setting-info">{logInfoListCheckCnt} Targets</div>
                                    </FormGroup>
                                ) : (
                                    <>
                                        <FormGroup>
                                            <Label>Command</Label>
                                            <div className="setting-info">{checkedCnt} Commands</div>
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

export default connect(
    (state) => ({
        autoPlan: state.autoPlan.get('autoPlan'),
        toolInfoList: state.viewList.get('toolInfoList'),
        logInfoList: state.viewList.get('logInfoList'),
        toolInfoListCheckCnt: state.viewList.get('toolInfoListCheckCnt'),
        logInfoListCheckCnt: state.viewList.get('logInfoListCheckCnt'),
        command: state.command.get('command')
    }),
    (dispatch) => ({
        autoPlanActions: bindActionCreators(autoPlanActions, dispatch)
    })
)(RSSautoplanchecksetting);
