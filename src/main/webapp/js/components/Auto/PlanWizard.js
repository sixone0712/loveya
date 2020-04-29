import React, { Component } from "react";
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

const STEP_MACHINE = 1;
const STEP_TARGET = 2;
const STEP_OPTION = 3;
const STEP_CHECK = 4;

class RSSautoplanwizard extends Component {
  constructor(props) {
    super(props);
    this.state = {
      currentStep: 1,
      completeStep: [],
      machineList: [],
      targetList: [],
      planNumber: "",
      periodFrom: "",
      periodTo: "",
      startDate: "",
      planMode: "",
      cycleOption: { count: "", unit: "" },
      planDescription: ""
    };
  }

  handleNext = () => {
    const currentStep = this.state.currentStep + 1;

    this.setState(prevState => ({
      completeStep: [...prevState.completeStep, currentStep - 1],
      currentStep: currentStep
    }));
  };

  handlePrev = () => {
    const currentStep =
        this.state.currentStep <= 1 ? 1 : this.state.currentStep - 1;

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

    if (currentStep !== 1 && currentStep !== 5) {
      return (
          <Button className="footer-btn" onClick={this.handlePrev}>
            Previous
          </Button>
      );
    }

    return null;
  };

  drawNextButton = () => {
    const { currentStep } = this.state;
    let buttonName = "";

    if (currentStep < 4) {
      buttonName = "Next";
    } else if (currentStep === 4) {
      buttonName = "Add Plan";
    } else {
      return null;
    }

    return (
        <Button className="footer-btn pos-right" onClick={this.handleNext}>
          {buttonName}
        </Button>
    );
  };

  render() {
    const { currentStep } = this.state;

    return (
        <Card className="auto-plan-box-shadow">
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
                  <div className={this.getClassName(1)}>
                    <div className="step-number">{this.completeCheck(1)}</div>
                    <div className="step-label">Machine</div>
                  </div>
                </li>
                <li>
                  <div className={this.getClassName(2)}>
                    <div className="step-number">{this.completeCheck(2)}</div>
                    <div className="step-label">Target</div>
                  </div>
                </li>
                <li>
                  <div className={this.getClassName(3)}>
                    <div className="step-number">{this.completeCheck(3)}</div>
                    <div className="step-label">Detail Options</div>
                  </div>
                </li>
                <li>
                  <div className={this.getClassName(4)}>
                    <div className="step-number">{this.completeCheck(4)}</div>
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
                  <Machine />
                </CarouselItem>
                <CarouselItem key={STEP_TARGET}>
                  <Target />
                </CarouselItem>
                <CarouselItem key={STEP_OPTION}>
                  <Option />
                </CarouselItem>
                <CarouselItem key={STEP_CHECK}>
                  <Check />
                </CarouselItem>
              </Carousel>
            </Col>
          </CardBody>
          <CardFooter className="auto-plan-card-footer">
            {this.drawPrevButton()}
            {this.drawNextButton()}
          </CardFooter>
        </Card>
    );
  }
}

export default RSSautoplanwizard;
