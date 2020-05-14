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
import { faExclamationCircle, faCheckCircle } from "@fortawesome/free-solid-svg-icons";
import AlertModal from "../Common/AlertModal";
import ConfirmModal from "../Common/ConfirmModal";

const wizardStep = {
  MACHINE: 1,
  TARGET: 2,
  OPTION: 3,
  CHECK: 4
};

const modalMessage = {
  MACHINE_ALERT_MESSAGE: "You must select at least one or more machines.",
  TARGET_ALERT_MESSAGE: "You must select at least one or more targets.",
  PLAN_ID_ALERT_MESSAGE: "Plan ID must be at least 1 character long.",
  FROM_TO_ALERT_MESSAGE: "Please set the from(Period) time before the To(Period) time.",
  CYCLE_ALERT_MESSAGE: "Cycle mode must have a minimum of 1 interval.",
  PLAN_ADD_MESSAGE: "Are you sure you want to create a collection plan with this setting?",
  PLAN_EDIT_MESSAGE: "Are you sure you want to change the collection plan with this setting?"
};

const modalType = {
  ALERT: 1,
  CONFIRM: 2
};

class RSSautoplanwizard extends Component {
  constructor(props) {
    super(props);
    const { isNew, editId } = this.props;
    this.state = {
      isNew,
      editId,
      currentStep: wizardStep.MACHINE,
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
      isConfirmOpen: false,
      modalMessage: null
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
    const { currentStep, isNew } = this.state;
    const { autoPlan, toolInfoListCheckCnt, logInfoListCheckCnt } = this.props;
    const message = invalidCheck(currentStep, toolInfoListCheckCnt, logInfoListCheckCnt, autoPlan);

    if(message === null) {
      if(currentStep === wizardStep.CHECK) {
        if (isNew) {
          this.modalOpen(modalType.CONFIRM, modalMessage.PLAN_ADD_MESSAGE);
        } else {
          this.modalOpen(modalType.CONFIRM, modalMessage.PLAN_EDIT_MESSAGE);
        }
      } else {
        this.setState(prevState => ({
          completeStep: [...prevState.completeStep, currentStep],
          currentStep: currentStep + 1
        }));
      }
    } else {
      this.modalOpen(modalType.ALERT, message);
    }
  };

  handlePrev = () => {
    const currentStep =
        this.state.currentStep <= wizardStep.MACHINE ? wizardStep.MACHINE : this.state.currentStep - 1;

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

    if (currentStep !== wizardStep.MACHINE) {
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

    if (currentStep < wizardStep.CHECK) {
      buttonName = "Next";
    } else if (currentStep === wizardStep.CHECK) {
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

  modalOpen = (type, message) => {
    switch(type) {
      case modalType.ALERT:
        this.setState({
          isAlertOpen: true,
          modalMessage: message
        });
        break;

      case modalType.CONFIRM:
        this.setState({
          isConfirmOpen: true,
          modalMessage: message
        });
        break;

      default:
        break;
    }
  }

  modalClose = () => {
    this.setState({
      isAlertOpen: false,
      isConfirmOpen: false,
      modalMessage: ""
    });
  }

  render() {
    console.log("render");
    console.log("this.state.editID", this.state.editID);
    const { currentStep, isNew, editId, isAlertOpen, isConfirmOpen, modalMessage } = this.state;
    const { logTypeSuccess,
            toolInfoSuccess,
            logTypeFailure,
            toolInfoFailure } = this.props;
    const renderAlertModal = AlertModal(isAlertOpen, faExclamationCircle, modalMessage, "auto-plan", this.modalClose);
    let renderConfirmModal;

    if (isNew) {
      renderConfirmModal = ConfirmModal(isConfirmOpen,
                                        faCheckCircle,
                                        modalMessage,
                                        "auto-plan",
                                        this.modalClose,
                                        () => this.handleRequestAutoPlanAdd(),
                                        this.modalClose);
    } else {
      renderConfirmModal = ConfirmModal(isConfirmOpen,
                                        faCheckCircle,
                                        modalMessage,
                                        "auto-plan",
                                        this.modalClose,
                                        () => this.handleRequestAutoPlanEdit(editId),
                                        this.modalClose);
    }

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
                    <div className={this.getClassName(wizardStep.MACHINE)}>
                      <div className="step-number">
                        {this.completeCheck(wizardStep.MACHINE)}
                      </div>
                      <div className="step-label">Machine</div>
                    </div>
                  </li>
                  <li>
                    <div className={this.getClassName(wizardStep.TARGET)}>
                      <div className="step-number">
                        {this.completeCheck(wizardStep.TARGET)}
                      </div>
                      <div className="step-label">Target</div>
                    </div>
                  </li>
                  <li>
                    <div className={this.getClassName(wizardStep.OPTION)}>
                      <div className="step-number">
                        {this.completeCheck(wizardStep.OPTION)}
                      </div>
                      <div className="step-label">Detail Options</div>
                    </div>
                  </li>
                  <li>
                    <div className={this.getClassName(wizardStep.CHECK)}>
                      <div className="step-number">
                        {this.completeCheck(wizardStep.CHECK)}
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
                  <CarouselItem key={wizardStep.MACHINE}>
                    <Machine isNew={isNew} />
                  </CarouselItem>
                  <CarouselItem key={wizardStep.TARGET}>
                    <Target isNew={isNew} />
                  </CarouselItem>
                  <CarouselItem key={wizardStep.OPTION}>
                    <Option isNew={isNew} />
                  </CarouselItem>
                  <CarouselItem key={wizardStep.CHECK}>
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
        {renderConfirmModal}
      </>
    );
  }
}

function invalidCheck(step, toolCnt, targetCnt, optionList) {
  switch(step) {
    case wizardStep.MACHINE:
      if (toolCnt === 0) {
        return modalMessage.MACHINE_ALERT_MESSAGE;
      } else {
        return null;
      }

    case wizardStep.TARGET:
      if (targetCnt === 0) {
        return modalMessage.TARGET_ALERT_MESSAGE;
      } else {
        return null;
      }

    case wizardStep.OPTION:
      const { planId, collectType, interval, from, to } = optionList.toJS();

      if (planId.toString().length < 1) {
        return modalMessage.PLAN_ID_ALERT_MESSAGE;
      } else if (from.isAfter(to)) {
        return modalMessage.FROM_TO_ALERT_MESSAGE;
      } else if (collectType === DEFINE.AUTO_MODE_CYCLE) {
        if (interval < 1) {
          return modalMessage.CYCLE_ALERT_MESSAGE;
        } else {
          return null;
        }
      } else {
        return null;
      }

    case wizardStep.CHECK:
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
