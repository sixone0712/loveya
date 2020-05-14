import React, { Component } from "react";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../modules/viewList";
import services from '../../services';
import * as DEFINE from "../../define"
import {
  Col,
  Card,
  CardHeader,
  CardBody,
  CardFooter,
  Button,
  Carousel,
  CarouselItem
} from "reactstrap";
import Machine from "./MachineList";
import Target from "./TargetList";
import Option from "./OptionList";
import Check from "./CheckSetting";
import moment from "moment";
import * as API from "../../api";
import * as autoPlanActions from "../../modules/autoPlan";
import { faExclamationCircle } from "@fortawesome/free-solid-svg-icons";
import AlertModal from "../Common/AlertModal";

const STEP_MACHINE = 1;
const STEP_TARGET = 2;
const STEP_OPTION = 3;
const STEP_CHECK = 4;
const STEP_MAX = 5;

const MACHINE_ALERT_MESSAGE = "You must select at least one or more machines.";
const TARGET_ALERT_MESSAGE = "You must select at least one or more targets.";
const PLAN_ID_ALERT_MESSAGE = "Plan ID must be at least 1 character long."
const FROM_TO_ALERT_MESSAGE = "Please set the From(Period) time before the To(Period) time.";
const CYCLE_ALERT_MESSAGE = "Cycle mode must have a minimum of 1 interval.";

class RSSautoplanwizard extends Component {
  constructor(props) {
    super(props);
    const { isNew, editId } = this.props;
    this.state = {
      isNew,
      editId,
      currentStep: STEP_MACHINE,
      completeStep: [],
      machineList: [],
      targetList: [],
      planNumber: "",
      periodFrom: "",
      periodTo: "",
      startDate: "",
      planMode: "",
      cycleOption: { count: "", unit: "" },
      planDescription: "",
      isAlertOpen: false,
      alertMessage: null
    };
  }

  calculateTime = (collectType, interval, intervalUnit) => {
    const intervalInt = Number(interval);
    let millisec = 0;

    if(collectType === DEFINE.AUTO_MODE_CYCLE) {
      switch (intervalUnit) {
        case DEFINE.AUTO_UNIT_MINUTE:
          millisec = intervalInt * 60 * 1000;
          break;
        case DEFINE.AUTO_UNIT_HOUR:
          millisec = intervalInt * 60 * 60 * 1000;
          break;
        case DEFINE.AUTO_UNIT_DAY:
          millisec = intervalInt * 60 * 60 * 24 * 1000;
          break;
      }
    }
    return String(millisec);
  }

  makeRequestAutoPlanData = () => {
    const { autoPlan, toolInfoList, logInfoList } = this.props;
    const { planId, collectType, interval, intervalUnit, from, to, collectStart, description } = autoPlan.toJS();
    const convInterval = this.calculateTime(collectType, interval, intervalUnit);
    const toolInfoListJS = toolInfoList.toJS();
    const logInfoListJS = logInfoList.toJS();
    const newToolInfoList = toolInfoListJS.filter(item => item.checked === true);
    const newLogInfoList = logInfoListJS.filter(item => item.checked === true);

    const tools = newToolInfoList.map(item => item.targetname);
    const logTypes = newLogInfoList.map(item => item.logCode);
    const logNames = newLogInfoList.map(item => item.logName);

    const reqData = {
      planId: planId,
      tools: tools,
      logTypes: logTypes,
      logNames: logNames,
      collectStart: moment(collectStart).format("YYYY-MM-DD HH:mm:ss"),
      from: moment(from).format("YYYY-MM-DD HH:mm:ss"),
      to: moment(to).format("YYYY-MM-DD HH:mm:ss"),
      collectType: collectType,
      interval: convInterval,
      description: description,
    };
    return reqData;
  }

  handleRequestAutoPlanAdd = async () => {
    const reqData = this.makeRequestAutoPlanData();
    console.log("reqData", reqData);
    const res = await services.axiosAPI.postByJson("/plan/add", reqData);
    console.log(res);

    console.log("this.props.history", this.props.history);
    this.props.history.push(DEFINE.PAGE_REFRESH_AUTO_STATUS);
    // 에러 처리 추가 필요
  }

  handleRequestAutoPlanEdit = async (editId) => {
    const reqData = this.makeRequestAutoPlanData();
    console.log("reqData", reqData);
    console.log("editID", editId);
    const res = await services.axiosAPI.postByJson("/plan/modify?id=" + editId, reqData);
    console.log(res);
    console.log("this.props.history", this.props.history);
    this.props.history.push(DEFINE.PAGE_REFRESH_AUTO_STATUS);
    // 에러 처리 추가 필요
  }


  handleNext = () => {
    const { currentStep } = this.state;
    const { autoPlan, toolInfoListCheckCnt, logInfoListCheckCnt } = this.props;
    const message = invalidCheck(currentStep, toolInfoListCheckCnt, logInfoListCheckCnt, autoPlan);

    if(message === null) {
      if(this.state.isNew && currentStep === STEP_CHECK) {
        this.handleRequestAutoPlanAdd();
      } else if(!this.state.isNew && currentStep === STEP_CHECK) {
        this.handleRequestAutoPlanEdit(this.state.editId);
      }

      this.setState(prevState => ({
        completeStep: [...prevState.completeStep, currentStep],
        currentStep: currentStep + 1
      }));
    } else {
      this.modalOpen(message);
    }
  };

  handlePrev = () => {
    const currentStep =
        this.state.currentStep <= STEP_MACHINE ? STEP_MACHINE : this.state.currentStep - 1;

    this.setState(prevState => ({
      completeStep: prevState.completeStep.filter(item => item !== currentStep),
      currentStep: currentStep
    }));
  };

  getClassName = step => {
    const { currentStep, completeStep } = this.state;

    for (const item of completeStep) {
      if (item === step) {
        return "complete";
      }
    }

    return step === currentStep ? "active" : null;
  };

  completeCheck = step => {
    const { completeStep } = this.state;

    for (const item of completeStep) {
      if (item === step) {
        return "√";
      }
    }

    return step;
  };

  drawPrevButton = () => {
    const { currentStep } = this.state;

    if (currentStep !== STEP_MACHINE) {
      return (
          <Button className="footer-btn" onClick={this.handlePrev}>
            Previous
          </Button>
      );
    }

    return null;
  };

  drawNextButton = () => {
    const { currentStep, isNew } = this.state;
    let buttonName = "";

    if (currentStep < STEP_CHECK) {
      buttonName = "Next";
    } else if (currentStep === STEP_CHECK) {
      if (isNew) {
        buttonName = "Add Plan";
      } else {
        buttonName = "Edit Plan";
      }
    } else {
      return null;
    }

    return (
        <Button className="footer-btn pos-right" onClick={this.handleNext}>
          {buttonName}
        </Button>
    );
  };

  modalOpen = (message) => {
    this.setState({
      isAlertOpen: true,
      alertMessage: message
    });
  }

  modalClose = () => {
    this.setState({
      isAlertOpen: false,
      alertMessage: null
    });
  }
  render() {
    console.log("render");
    console.log("this.state.editID", this.state.editID);
    const { currentStep, isNew, isAlertOpen, alertMessage } = this.state;
    const { logTypeSuccess,
            toolInfoSuccess,
            logTypeFailure,
            toolInfoFailure } = this.props;
    const renderAlertModal = AlertModal(isAlertOpen, faExclamationCircle, alertMessage, "auto-plan", this.modalClose);

    return (
        <>
          {toolInfoSuccess && logTypeSuccess &&
        <Card className="auto-plan-box">
          <CardHeader className="auto-plan-card-header">
            Plan Settings
            <p>
              Set the <span>following items.</span>
            </p>
          </CardHeader>
          <CardBody className="auto-plan-card-body">
            <Col sm={{ size: 3 }} className="step-indicator pdl-0 bd-right">
              <ul>
                <li>
                  <div className={this.getClassName(STEP_MACHINE)}>
                    <div className="step-number">
                      {this.completeCheck(STEP_MACHINE)}
                    </div>
                    <div className="step-label">Machine</div>
                  </div>
                </li>
                <li>
                  <div className={this.getClassName(STEP_TARGET)}>
                    <div className="step-number">
                      {this.completeCheck(STEP_TARGET)}
                    </div>
                    <div className="step-label">Target</div>
                  </div>
                </li>
                <li>
                  <div className={this.getClassName(STEP_OPTION)}>
                    <div className="step-number">
                      {this.completeCheck(STEP_OPTION)}
                    </div>
                    <div className="step-label">Detail Options</div>
                  </div>
                </li>
                <li>
                  <div className={this.getClassName(STEP_CHECK)}>
                    <div className="step-number">
                      {this.completeCheck(STEP_CHECK)}
                    </div>
                    <div className="step-label">Check Settings</div>
                  </div>
                </li>
              </ul>
            </Col>
            <Col sm={{ size: 9 }} className="pdr-0 pdl-5">
              <Carousel
                  activeIndex={currentStep - 1}
                  next={this.handleNext}
                  previous={this.handlePrev}
                  interval={false}
              >
                <CarouselItem key={STEP_MACHINE}>
                  <Machine isNew={isNew} />
                </CarouselItem>
                <CarouselItem key={STEP_TARGET}>
                  <Target isNew={isNew} />
                </CarouselItem>
                <CarouselItem key={STEP_OPTION}>
                  <Option isNew={isNew} />
                </CarouselItem>
                <CarouselItem key={STEP_CHECK}>
                  <Check isNew={isNew} />
                </CarouselItem>
              </Carousel>
            </Col>
          </CardBody>
          <CardFooter className="auto-plan-card-footer">
            {this.drawPrevButton()}
            {this.drawNextButton()}
          </CardFooter>
        </Card>
        }
        { logTypeFailure && toolInfoFailure &&
          <div className="network-connection-error">Network Connection Error</div>
        }
        {renderAlertModal}
      </>
    );
  }
}

function invalidCheck(step, toolCnt, targetCnt, optionList) {
  switch(step) {
    case STEP_MACHINE:
      if (toolCnt === 0) {
        return MACHINE_ALERT_MESSAGE;
      } else {
        return null;
      }

    case STEP_TARGET:
      if (targetCnt === 0) {
        return TARGET_ALERT_MESSAGE;
      } else {
        return null;
      }

    case STEP_OPTION:
      const { planId, collectType, interval, from, to } = optionList.toJS();

      if (planId.toString().length < 1) {
        return PLAN_ID_ALERT_MESSAGE;
      } else if (from.isAfter(to)) {
        return FROM_TO_ALERT_MESSAGE;
      } else if (collectType === DEFINE.AUTO_MODE_CYCLE) {
        if (interval < 1) {
          return CYCLE_ALERT_MESSAGE;
        } else {
          return null;
        }
      } else {
        return null;
      }

    case STEP_CHECK:
    default:
      return null;
  }
}

export default connect(
    (state) => ({
      equipmentList: state.viewList.get('equipmentList'),
      toolInfoList: state.viewList.get('toolInfoList'),
      toolInfoListCheckCnt: state.viewList.get('toolInfoListCheckCnt'),
      logInfoListCheckCnt: state.viewList.get('logInfoListCheckCnt'),
      logInfoList: state.viewList.get('logInfoList'),
      autoPlan: state.autoPlan.get('autoPlan'),
      logTypeSuccess: state.pender.success['viewList/VIEW_LOAD_TOOLINFO_LIST'],
      toolInfoSuccess: state.pender.success['viewList/VIEW_LOAD_LOGTYPE_LIST'],
      logTypeFailure: state.pender.failure['viewList/VIEW_LOAD_TOOLINFO_LIST'],
      toolInfoFailure: state.pender.failure['viewList/VIEW_LOAD_LOGTYPE_LIST'],
    }),
    (dispatch) => ({
      viewListActions: bindActionCreators(viewListActions, dispatch),
      autoPlanActions: bindActionCreators(autoPlanActions, dispatch),
    })
)(RSSautoplanwizard);
