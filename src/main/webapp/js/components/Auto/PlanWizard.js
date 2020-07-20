import React, { Component } from "react";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../modules/viewList";
import services from '../../services';
import * as Define from "../../define"
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
import Command from "./VFTP/commandlist";
import Option from "./OptionList";
import Check from "./CheckSetting";
import moment from "moment";
import * as API from "../../api";
import * as autoPlanActions from "../../modules/autoPlan";
import { faExclamationCircle, faCheckCircle } from "@fortawesome/free-solid-svg-icons";
import AlertModal from "../Common/AlertModal";
import ConfirmModal from "../Common/ConfirmModal";

export const wizardStep = {
  MACHINE: 1,
  TARGET_COMMAND: 2,
  OPTION: 3,
  CHECK: 4
};

const modalMessage = {
  MACHINE_ALERT_MESSAGE: "You must select at least one or more machines.",
  TARGET_ALERT_MESSAGE: "You must select at least one or more targets.",
  PLAN_ID_ALERT_MESSAGE: "Plan ID is invalid.",
  FROM_TO_ALERT_MESSAGE: "Please set the from(Period) time before the To(Period) time.",
  CYCLE_ALERT_MESSAGE: "Interval is invalid.",
  DESCRIPTION_ALERT_MESSAGE: "Description is invalid.",
  PLAN_ADD_MESSAGE: "Are you sure you want to create a collection plan with this setting?",
  PLAN_EDIT_MESSAGE: "Are you sure you want to change the collection plan with this setting?"
};

export const modalType = {
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

    if(collectType === Define.AUTO_MODE_CYCLE) {
      switch (intervalUnit) {
        case Define.AUTO_UNIT_MINUTE:
          millisec = intervalInt * 60 * 1000;
          break;
        case Define.AUTO_UNIT_HOUR:
          millisec = intervalInt * 60 * 60 * 1000;
          break;
        case Define.AUTO_UNIT_DAY:
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

    const structId = newToolInfoList.map(item => item.structId);
    const tools = newToolInfoList.map(item => item.targetname);
    const logTypes = newLogInfoList.map(item => item.logCode);
    const logNames = newLogInfoList.map(item => item.logName);

    const reqData = {
      planName: planId,
      planType: "ftp",    // need to add
      fabNames: structId,
      machineNames: tools,
      categoryCodes: logTypes,
      categoryNames: logNames,
      commands: null,   // need to add
      start: moment(collectStart).format("YYYYMMDDHHmmss"),
      from: moment(from).format("YYYYMMDDHHmmss"),
      to: moment(to).format("YYYYMMDDHHmmss"),
      type: collectType,
      interval: convInterval,
      description: description,
    };
    //console.log("[PlanWizard][makeRequestAutoPlanData] reqData", reqData);
    return reqData;
  }

  handleRequestAutoPlanAdd = async () => {
    const reqData = this.makeRequestAutoPlanData();
    //console.log("reqData", reqData);
    const res = await services.axiosAPI.post(Define.REST_PLANS_POST_PLANS, reqData);
    //console.log(res);

    //console.log("this.props.history", this.props.history);
    this.props.history.push(Define.PAGE_REFRESH_AUTO_STATUS);
    // error process
  }

  handleRequestAutoPlanEdit = async (editId) => {
    const reqData = this.makeRequestAutoPlanData();
    //console.log("reqData", reqData);
    console.log("editID", editId);
    const res = await services.axiosAPI.putReqeust(`${Define.REST_PLANS_MODIFY_PLAN}/${editId}`, reqData);
    console.log(res);
    //console.log("this.props.history", this.props.history);
    this.props.history.push(Define.PAGE_REFRESH_AUTO_STATUS);
    // error process
  }


  handleNext = () => {
    const { currentStep, isNew } = this.state;
    const { autoPlan, toolInfoListCheckCnt, logInfoListCheckCnt, type } = this.props;
    const message = invalidCheck(currentStep, toolInfoListCheckCnt, logInfoListCheckCnt, autoPlan, type);

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
    console.log("this.state.editID", this.state.editId);
    const { currentStep, isNew, editId, isAlertOpen, isConfirmOpen, modalMessage } = this.state;
    const { logTypeSuccess, toolInfoSuccess, logTypeFailure, toolInfoFailure, type } = this.props;

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
                <ul className="wizard-step">
                  <li>
                    <div className={this.getClassName(wizardStep.MACHINE)}>
                      <div className="step-number">
                        {this.completeCheck(wizardStep.MACHINE)}
                      </div>
                      <div className="step-label">Machine</div>
                    </div>
                  </li>
                  <li>
                    <div className={this.getClassName(wizardStep.TARGET_COMMAND)}>
                      <div className="step-number">
                        {this.completeCheck(wizardStep.TARGET_COMMAND)}
                      </div>
                      <div className="step-label">{type === "FTP" ? "Target" : "Command"}</div>
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
                    keyboard={false}
                    interval={false}
                >
                  <CarouselItem key={wizardStep.MACHINE}>
                    <Machine isNew={isNew} />
                  </CarouselItem>
                  <CarouselItem key={wizardStep.TARGET_COMMAND}>
                    {type === "FTP" ? (
                        <Target isNew={isNew} />
                    ) : (
                        <Command isNew={isNew} />
                    )}
                  </CarouselItem>
                  <CarouselItem key={wizardStep.OPTION}>
                    <Option isNew={isNew} />
                  </CarouselItem>
                  <CarouselItem key={wizardStep.CHECK}>
                    <Check isNew={isNew} type={type}/>
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
          <div className="network-connection-error auto-plan">Network Connection Error</div>
        }
        <AlertModal isOpen={isAlertOpen} icon={faExclamationCircle} message={modalMessage} style={"auto-plan"} closer={this.modalClose} />
        {isNew ? (
            <ConfirmModal isOpen={isConfirmOpen}
                          icon={faCheckCircle}
                          message={modalMessage}
                          style={"auto-plan"}
                          leftBtn={"OK"}
                          rightBtn={"Cancel"}
                          actionBg={this.modalClose}
                          actionLeft={() => this.handleRequestAutoPlanAdd()}
                          actionRight={this.modalClose}
            />
        ) : (
            <ConfirmModal isOpen={isConfirmOpen}
                          icon={faCheckCircle}
                          message={modalMessage}
                          style={"auto-plan"}
                          leftBtn={"OK"}
                          rightBtn={"Cancel"}
                          actionBg={this.modalClose}
                          actionLeft={() => this.handleRequestAutoPlanEdit(editId)}
                          actionRight={this.modalClose}
            />
        )}
      </>
    );
  }
}

export function invalidCheck(step, toolCnt, targetCnt, optionList, type) {
  switch(step) {
    case wizardStep.MACHINE:
      if (toolCnt === 0) {
        return modalMessage.MACHINE_ALERT_MESSAGE;
      } else {
        return null;
      }

    case wizardStep.TARGET_COMMAND:
      if (targetCnt === 0 && type === "FTP") {
        return modalMessage.TARGET_ALERT_MESSAGE;
      } else {
        return null;
      }

    case wizardStep.OPTION:
      const { planId, collectType, interval, from, to, description, intervalUnit } = optionList.toJS();
      const planIdRegex = /^([\p{L}0-9]).{1,30}([\p{L}0-9]$)/gu;
      const planDescRegex = /^([\p{L}0-9]).{1,38}([\p{L}0-9]$)/gu;
      const planIntervalRegex = /[0-9]/g;

      if (!planIdRegex.test(planId)) {
        return modalMessage.PLAN_ID_ALERT_MESSAGE;
      }

      if (from.isAfter(to)) {
        return modalMessage.FROM_TO_ALERT_MESSAGE;
      }

      if (collectType === Define.AUTO_MODE_CYCLE) {
        if (interval < 1 || !planIntervalRegex.test(interval)) {
          return modalMessage.CYCLE_ALERT_MESSAGE;
        }

        switch(intervalUnit) {
          case Define.AUTO_UNIT_MINUTE:
            if (interval > 59) {
              return modalMessage.CYCLE_ALERT_MESSAGE;
            }
            break;

          case Define.AUTO_UNIT_HOUR:
            if (interval > 23) {
              return modalMessage.CYCLE_ALERT_MESSAGE;
            }
            break;

          case Define.AUTO_UNIT_DAY:
            if (interval > 365) {
              return modalMessage.CYCLE_ALERT_MESSAGE;
            }
            break;

          default:
            break;
        }
      }

      if (description.toString().length > 0) {
        if(!planDescRegex.test(description)) {
          return modalMessage.DESCRIPTION_ALERT_MESSAGE;
        }
      }

      return null;

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
