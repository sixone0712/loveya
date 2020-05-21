import React, { Component } from "react";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as autoPlanActions from "../../modules/autoPlan";
import * as API from '../../api'

import { Col, FormGroup, Input, Label, CustomInput } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCalendarPlus } from "@fortawesome/free-regular-svg-icons";
import { DatetimePicker } from "rc-datetime-picker";
import ReactTransitionGroup from "react-addons-css-transition-group";
import moment from "moment";
import { Select } from "antd";
import * as DEFINE from "../../define";

const { Option } = Select;

class RSSautoformlist extends Component {
  constructor() {
    super();
    this.state = {
      currentModal: 0,
      modalOpen: false
    };
  }

  handleDateChange = (idx, moment) => {
    const { autoPlanActions } = this.props;

    switch (idx) {
      case DEFINE.AUTO_DATE_PERIOD_FROM:
        autoPlanActions.autoPlanSetFrom(moment);
        break;

      case DEFINE.AUTO_DATE_PERIOD_TO:
        autoPlanActions.autoPlanSetTo(moment);
        break;

      case DEFINE.AUTO_DATE_COLLECT_START:
        autoPlanActions.autoPlanSetCollectStart(moment);
        break;

      default:
        break;
    }
  };

  getDateValue = idx => {
    const { autoPlan } = this.props;
    switch (idx) {
      case DEFINE.AUTO_DATE_PERIOD_FROM:
        return autoPlan.get("from");

      case DEFINE.AUTO_DATE_PERIOD_TO:
        return autoPlan.get("to");

      case DEFINE.AUTO_DATE_COLLECT_START:
        return autoPlan.get("collectStart");

      default:
        return;
    }
  };

  openModal = idx => {
    this.setState({
      currentModal: idx,
      modalOpen: true
    });
  };

  closeModal = () => {
    this.setState({
      modalOpen: false
    });
  };

  handleModeChange = mode => {
    const { autoPlanActions } = this.props;
    autoPlanActions.autoPlanSetCollectType(mode);
  };

  handlePlanIdChange = e => {
    const { autoPlanActions } = this.props;
    autoPlanActions.autoPlanSetPlanId(e.target.value);
  }

  handleIntervalChange = e => {
    const { autoPlanActions } = this.props;
    autoPlanActions.autoPlanSetInterval(e.target.value);
  }

  handleIntervalUnitChange = value => {
    const { autoPlanActions } = this.props;
    autoPlanActions.autoPlanSetIntervalUnit(value);
  }

  handleDiscriptionChange = e => {
    const { autoPlanActions } = this.props;
    autoPlanActions.autoPlanSetDescription(e.target.value);
  }

  render() {
    const {
      currentModal,
      modalOpen
    } = this.state;

    const { autoPlan } = this.props;
    const { planId, collectType, interval, intervalUnit, from, to, collectStart, description } = autoPlan.toJS();

    return (
        <div className="form-section optionlist">
          <Col className="pd-0">
            <div className="form-header-section">
              <div className="form-title-section">
                Detail Options
                <p>Set detail plan options.</p>
              </div>
            </div>
            <div className="dis-flex align-center">
              <FormGroup className="auto-plan-optionlist-form-group">
                <FormGroup>
                  <Label for="plan_id" className="input-label">
                    Plan ID
                  </Label>
                  <Input
                      type="text"
                      id="plan_id"
                      bsSize="sm"
                      className="half-width"
                      value={planId}
                      onChange={this.handlePlanIdChange}
                  />
                </FormGroup>
                <FormGroup>
                  <Label for="plan_period" className="input-label">
                    Period
                  </Label>
                  <FormGroup className="period-section">
                    <Input
                        type="text"
                        bsSize="sm"
                        value={from.format("YYYY-MM-DD HH:mm")}
                        onClick={() =>
                            this.openModal(DEFINE.AUTO_DATE_PERIOD_FROM)
                        }
                        readOnly
                    />
                    <span className="split-character">~</span>
                    <Input
                        type="text"
                        bsSize="sm"
                        value={to.format("YYYY-MM-DD HH:mm")}
                        onClick={() => this.openModal(DEFINE.AUTO_DATE_PERIOD_TO)}
                        readOnly
                    />
                  </FormGroup>
                </FormGroup>
                <FormGroup className="start-section">
                  <Label for="plan_start" className="input-label">
                    Start
                  </Label>
                  <Input
                      type="text"
                      bsSize="sm"
                      value={collectStart.format("YYYY-MM-DD HH:mm")}
                      onClick={() => this.openModal(DEFINE.AUTO_DATE_COLLECT_START)}
                      readOnly
                      className="half-width"
                  />
                </FormGroup>
                <FormGroup>
                  <Label for="plan_mode" className="input-label">
                    Mode
                  </Label>
                  <FormGroup className="dis-flex mode-section">
                    <CustomInput
                        type="radio"
                        id="mode_continue"
                        name="collection_mode"
                        label="Continuous"
                        checked={collectType === DEFINE.AUTO_MODE_CONTINUOUS}
                        onChange={() => this.handleModeChange(DEFINE.AUTO_MODE_CONTINUOUS)}
                    />
                    <CustomInput
                        type="radio"
                        id="mode_cycle"
                        name="collection_mode"
                        label="Cycle"
                        className="mode-cycle"
                        checked={collectType === DEFINE.AUTO_MODE_CYCLE}
                        onChange={() => this.handleModeChange(DEFINE.AUTO_MODE_CYCLE)}
                    />
                    <div
                        className={
                          "sub-option " +
                          (collectType === DEFINE.AUTO_MODE_CONTINUOUS ? "hidden" : "show")
                        }
                    >
                      <Input
                          type="text"
                          bsSize="sm"
                          value={interval}
                          onChange={this.handleIntervalChange}
                      />
                      <Select
                          defaultValue= {intervalUnit}
                          onChange={this.handleIntervalUnitChange}
                      >
                        <Option value={DEFINE.AUTO_UNIT_MINUTE}>Minute</Option>
                        <Option value={DEFINE.AUTO_UNIT_HOUR}>Hour</Option>
                        <Option value={DEFINE.AUTO_UNIT_DAY}>Day</Option>
                      </Select>
                    </div>
                  </FormGroup>
                </FormGroup>
                <FormGroup>
                  <Label for="plan_desc" className="input-label">
                    Description
                  </Label>
                  <Input
                      type="text"
                      id="plan_desc"
                      bsSize="sm"
                      value={description}
                      onChange={this.handleDiscriptionChange}
                  />
                </FormGroup>
              </FormGroup>
            </div>
          </Col>
          {modalOpen ? (
              <ReactTransitionGroup
                  transitionName={"Custom-modal-anim"}
                  transitionEnterTimeout={200}
                  transitionLeaveTimeout={200}
              >
                <div className="Custom-modal-overlay" onClick={this.closeModal} />
                <div className="Custom-modal auto-plan-calendar-modal">
                  <p className="title">Date Setting</p>
                  <div className="auto-plan-calendar-modal content-with-title">
                    <FormGroup>
                      <CreateDatetimePicker
                          moment={this.getDateValue(currentModal)}
                          idx={currentModal}
                          changer={this.handleDateChange}
                      />
                    </FormGroup>
                  </div>
                  <div className="button-wrap">
                    <button
                        className="auto-plan alert-type"
                        onClick={this.closeModal}
                    >
                      OK
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
        </div>
    );
  }
}

class CreateDatetimePicker extends Component {

  handleChange = moment => {
    const { idx, changer } = this.props;
    changer(idx, moment);
  };

  render() {
    const { moment } = this.props;

    return <DatetimePicker moment={moment} onChange={this.handleChange} />;
  }
}

export default connect(
    (state) => ({
      autoPlan: state.autoPlan.get('autoPlan'),
    }),
    (dispatch) => ({
      autoPlanActions: bindActionCreators(autoPlanActions, dispatch),
    })
)(RSSautoformlist);