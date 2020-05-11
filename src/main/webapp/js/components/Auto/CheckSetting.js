import React, { Component } from "react";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as autoPlanActions from "../../modules/autoPlan";
import { Col, FormGroup, Label } from "reactstrap";
import moment from "moment";
import * as DEFINE from "../../define";

class RSSautoplanchecksetting extends Component {
    render() {
        const { autoPlan, toolInfoListCheckCnt, logInfoListCheckCnt } = this.props;
        const { planId, collectType, interval, intervalUnit, from, to, collectStart, description } = autoPlan.toJS();

        return (
            <div className="form-section checksetting">
                <Col className="pd-0">
                    <div className="form-header-section">
                        <div className="form-title-section">
                            Check Settings
                            <p>Check your plan settings.</p>
                        </div>
                    </div>
                    <div className="dis-flex align-center">
                        <FormGroup className="auto-plan-checklist-form-group">
                            <FormGroup>
                                <Label>Plan ID</Label>
                                <div className="setting-info">{planId}</div>
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
                                    { collectType == DEFINE.AUTO_MODE_CONTINUOUS
                                        ? "Continous"
                                        : `Cycle / ${interval} ${intervalUnit}`
                                    }
                                </div>
                            </FormGroup>
                            <FormGroup>
                                <Label>Machine</Label>
                                <div className="setting-info">{toolInfoListCheckCnt} Machines</div>
                            </FormGroup>
                            <FormGroup>
                                <Label>Target</Label>
                                <div className="setting-info">{logInfoListCheckCnt} Targets</div>
                            </FormGroup>
                            <FormGroup>
                                <Label>Description</Label>
                                <div className="setting-info">{description}</div>
                            </FormGroup>
                        </FormGroup>
                    </div>
                </Col>
            </div>
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
    }),
    (dispatch) => ({
        autoPlanActions: bindActionCreators(autoPlanActions, dispatch),
    })
)(RSSautoplanchecksetting);
