import React, { Component } from "react";
import { Col, FormGroup, Input, Label, CustomInput } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCalendarPlus } from "@fortawesome/free-regular-svg-icons";
import { DatetimePicker } from "rc-datetime-picker";
import ReactTransitionGroup from "react-addons-css-transition-group";
import moment from "moment";
import { Select } from "antd";

const { Option } = Select;

const MODE_CONTINUE = "Continue";
const MODE_CYCLE = "cycle";
const DATE_PERIOD_FROM = 1;
const DATE_PERIOD_TO = 2;
const DATE_START_FROM = 3;

class RSSautoformlist extends Component {
  constructor() {
    super();
    this.state = {
      modeValue: MODE_CONTINUE,
      period_from: moment()
          .hour(0)
          .minute(0),
      period_to: moment()
          .hour(23)
          .minute(59),
      start_from: moment()
          .hour(0)
          .minute(0),
      currentModal: 0,
      modalOpen: false
    };
  }

  handleDateChange = (idx, moment) => {
    switch (idx) {
      case DATE_PERIOD_FROM:
        this.setState({
          period_from: moment
        });
        break;

      case DATE_PERIOD_TO:
        this.setState({
          period_to: moment
        });
        break;

      case DATE_START_FROM:
        this.setState({
          start_from: moment
        });
        break;

      default:
        break;
    }
  };

  getDateValue = idx => {
    switch (idx) {
      case DATE_PERIOD_FROM:
        return this.state.period_from;

      case DATE_PERIOD_TO:
        return this.state.period_to;

      case DATE_START_FROM:
        return this.state.start_from;

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
    this.setState({
      modeValue: mode
    });
  };

  render() {
    const {
      modeValue,
      period_from,
      period_to,
      start_from,
      currentModal,
      modalOpen
    } = this.state;

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
                        value={period_from.format("YYYY-MM-DD HH:mm")}
                        onClick={() =>
                            this.openModal(DATE_PERIOD_FROM, period_from)
                        }
                        readOnly
                    />
                    <span className="split-character">~</span>
                    <Input
                        type="text"
                        bsSize="sm"
                        value={period_to.format("YYYY-MM-DD HH:mm")}
                        onClick={() => this.openModal(DATE_PERIOD_TO, period_to)}
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
                      value={start_from.format("YYYY-MM-DD HH:mm")}
                      onClick={() => this.openModal(DATE_START_FROM, start_from)}
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
                        checked={modeValue === MODE_CONTINUE ? true : false}
                        onChange={() => this.handleModeChange(MODE_CONTINUE)}
                    />
                    <CustomInput
                        type="radio"
                        id="mode_cycle"
                        name="collection_mode"
                        label="Cycle"
                        className="mode-cycle"
                        checked={modeValue === MODE_CYCLE ? true : false}
                        onChange={() => this.handleModeChange(MODE_CYCLE)}
                    />
                    <div
                        className={
                          "sub-option " +
                          (modeValue === MODE_CONTINUE ? "hidden" : "show")
                        }
                    >
                      <Input type="text" bsSize="sm" />
                      <Select defaultValue="1">
                        <Option value="1">Minute</Option>
                        <Option value="2">Hour</Option>
                        <Option value="3">Day</Option>
                      </Select>
                    </div>
                  </FormGroup>
                </FormGroup>
                <FormGroup>
                  <Label for="plan_desc" className="input-label">
                    Description
                  </Label>
                  <Input type="text" id="plan_desc" bsSize="sm" />
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
  constructor(props) {
    super(props);
    const { moment } = this.props;
    this.state = {
      moment
    };
  }

  handleChange = moment => {
    const { idx, changer } = this.props;

    this.setState({
      moment: moment
    });

    changer(idx, moment);
  };

  render() {
    const { moment } = this.state;

    return <DatetimePicker moment={moment} onChange={this.handleChange} />;
  }
}

export default RSSautoformlist;