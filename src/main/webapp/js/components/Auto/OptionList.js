import React, { Component } from "react";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as autoPlanActions from "../../modules/autoPlan";
import * as API from '../../api'

import { Col, FormGroup, Input, Label, CustomInput, UncontrolledPopover, PopoverHeader, PopoverBody } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faExclamation } from "@fortawesome/free-solid-svg-icons";
import { DatetimePicker } from "rc-datetime-picker";
import ReactTransitionGroup from "react-addons-css-transition-group";
import moment from "moment";
import { Select } from "antd";
import * as Define from "../../define";

const { Option } = Select;

class RSSautoformlist extends Component {
  constructor() {
    super();
    this.state = {
      currentModal: 0,
      modalOpen: false,
      cycleIntervalMax: 2
    };
  }

  handleDateChange = (idx, moment) => {
    const { autoPlanActions } = this.props;

    switch (idx) {
      case Define.AUTO_DATE_PERIOD_FROM:
        autoPlanActions.autoPlanSetFrom(moment);
        break;

      case Define.AUTO_DATE_PERIOD_TO:
        autoPlanActions.autoPlanSetTo(moment);
        break;

      case Define.AUTO_DATE_COLLECT_START:
        autoPlanActions.autoPlanSetCollectStart(moment);
        break;

      default:
        break;
    }
  };

  getDateValue = idx => {
    const { autoPlan } = this.props;
    switch (idx) {
      case Define.AUTO_DATE_PERIOD_FROM:
        return autoPlan.get("from");

      case Define.AUTO_DATE_PERIOD_TO:
        return autoPlan.get("to");

      case Define.AUTO_DATE_COLLECT_START:
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

    switch(value) {
      case Define.AUTO_UNIT_MINUTE:
      case Define.AUTO_UNIT_HOUR:
        this.setState({
          cycleIntervalMax: 2
        });
        break;

      case Define.AUTO_UNIT_DAY:
        this.setState({
          cycleIntervalMax: 3
        });
        break;

      default:
        console.log("[OptionList.js] cycle interval unit error!!!");
        break;
    }
  }

  handleDiscriptionChange = e => {
    const { autoPlanActions } = this.props;
    autoPlanActions.autoPlanSetDescription(e.target.value);
  }

  render() {
    const { currentModal, modalOpen, cycleIntervalMax } = this.state;

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
                      maxLength="32"
                      onChange={this.handlePlanIdChange}
                  />
                  <UncontrolledPopover
                      placement="top-end"
                      target="plan_id"
                      className="auto-plan"
                      trigger="hover"
                      delay={{ show: 300, hide: 0 }}
                  >
                    <PopoverHeader>Plan ID</PopoverHeader>
                    <PopoverBody>
                      <p>
                        <FontAwesomeIcon icon={faExclamation} />{" "}
                        There are no restrictions on the types of characters that can be entered.
                      </p>
                      <p>
                        <FontAwesomeIcon icon={faExclamation} />{" "}
                        Special characters cannot be entered at the beginning or end.
                      </p>
                      <p>
                        <FontAwesomeIcon icon={faExclamation} />{" "}
                        Allowed to be at least 3 characters long and up to 32 characters long.
                      </p>
                    </PopoverBody>
                  </UncontrolledPopover>
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
                            this.openModal(Define.AUTO_DATE_PERIOD_FROM)
                        }
                        readOnly
                    />
                    <span className="split-character">~</span>
                    <Input
                        type="text"
                        bsSize="sm"
                        value={to.format("YYYY-MM-DD HH:mm")}
                        onClick={() => this.openModal(Define.AUTO_DATE_PERIOD_TO)}
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
                      onClick={() => this.openModal(Define.AUTO_DATE_COLLECT_START)}
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
                        checked={collectType === Define.AUTO_MODE_CONTINUOUS}
                        onChange={() => this.handleModeChange(Define.AUTO_MODE_CONTINUOUS)}
                    />
                    <CustomInput
                        type="radio"
                        id="mode_cycle"
                        name="collection_mode"
                        label="Cycle"
                        className="mode-cycle"
                        checked={collectType === Define.AUTO_MODE_CYCLE}
                        onChange={() => this.handleModeChange(Define.AUTO_MODE_CYCLE)}
                    />
                    <div
                        className={
                          "sub-option " +
                          (collectType === Define.AUTO_MODE_CONTINUOUS ? "hidden" : "show")
                        }
                    >
                      <Input
                          type="text"
                          bsSize="sm"
                          id="plan_cycle_interval"
                          value={interval}
                          maxLength={cycleIntervalMax}
                          onChange={this.handleIntervalChange}
                      />
                      <UncontrolledPopover
                          placement="top"
                          target="plan_cycle_interval"
                          className="auto-plan"
                          trigger="hover"
                          delay={{ show: 300, hide: 0 }}
                      >
                        <PopoverHeader>Cycle Interval</PopoverHeader>
                        <PopoverBody>
                          <p>
                            <FontAwesomeIcon icon={faExclamation} />{" "}
                            Minute: You can enter from 1 to 59.
                          </p>
                          <p>
                            <FontAwesomeIcon icon={faExclamation} />{" "}
                            Hour: You can enter from 1 to 23.
                          </p>
                          <p>
                            <FontAwesomeIcon icon={faExclamation} />{" "}
                            Day: You can enter from 1 to 365.
                          </p>
                        </PopoverBody>
                      </UncontrolledPopover>
                      <Select
                          defaultValue= {intervalUnit}
                          onChange={this.handleIntervalUnitChange}
                      >
                        <Option value={Define.AUTO_UNIT_MINUTE}>Minute</Option>
                        <Option value={Define.AUTO_UNIT_HOUR}>Hour</Option>
                        <Option value={Define.AUTO_UNIT_DAY}>Day</Option>
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
                      className="half-width"
                      maxLength="40"
                      value={description}
                      onChange={this.handleDiscriptionChange}
                  />
                  <UncontrolledPopover
                      placement="top-end"
                      target="plan_desc"
                      className="auto-plan"
                      trigger="hover"
                      delay={{ show: 300, hide: 0 }}
                  >
                    <PopoverHeader>Description</PopoverHeader>
                    <PopoverBody>
                      <p>
                        <FontAwesomeIcon icon={faExclamation} />{" "}
                        You can register a collection plan without entering the description.
                      </p>
                      <p>
                        <FontAwesomeIcon icon={faExclamation} />{" "}
                        There are no restrictions on the types of characters that can be entered.
                      </p>
                      <p>
                        <FontAwesomeIcon icon={faExclamation} />{" "}
                        Special characters cannot be entered at the beginning or end.
                      </p>
                      <p>
                        <FontAwesomeIcon icon={faExclamation} />{" "}
                        Allowed to be at least 3 characters long and up to 40 characters long.
                      </p>
                    </PopoverBody>
                  </UncontrolledPopover>
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