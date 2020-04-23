import React, { Component } from "react";
import {
  Col,
  Card,
  CardHeader,
  CardBody,
  CardFooter,
  Button
} from "reactstrap";
import Machine from "./MachineList";
import Target from "./TargetList";
import Option from "./OptionList";

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

  handleTitleClick = step => {
    const { currentStep } = this.state;

    if (currentStep === step) {
      return;
    } else {
      const tempArray = [];

      for (let idx = 1; idx < step; idx++) {
        tempArray.push(idx);
      }

      this.setState({
        currentStep: step,
        completeStep: tempArray
      });
    }
  };

  render() {
    return (
        <Card className="auto-plan-box-shadow">
          <CardHeader className="auto-plan-card-header">
            Plan Settings
            <p>
              Set the <span>following items.</span>
            </p>
          </CardHeader>
          <CardBody className="auto-plan-card-body">
            <Col sm={{ size: 3 }} className="pdl-0 bd-right">
              <ul>
                <li>
                  <a
                      href="#/"
                      onClick={() => this.handleTitleClick(1)}
                      className={this.getClassName(1)}
                  >
                    <div className="step-number">{this.completeCheck(1)}</div>
                    <div className="step-label">Machine</div>
                  </a>
                </li>
                <li>
                  <a
                      href="#/"
                      onClick={() => this.handleTitleClick(2)}
                      className={this.getClassName(2)}
                  >
                    <div className="step-number">{this.completeCheck(2)}</div>
                    <div className="step-label">Target</div>
                  </a>
                </li>
                <li>
                  <a
                      href="#/"
                      onClick={() => this.handleTitleClick(3)}
                      className={this.getClassName(3)}
                  >
                    <div className="step-number">{this.completeCheck(3)}</div>
                    <div className="step-label">Detail Options</div>
                  </a>
                </li>
                <li>
                  <a
                      href="#/"
                      onClick={() => this.handleTitleClick(4)}
                      className={this.getClassName(4)}
                  >
                    <div className="step-number">{this.completeCheck(4)}</div>
                    <div className="step-label">Confirm</div>
                  </a>
                </li>
              </ul>
            </Col>
            <Col sm={{ size: 9 }} className="pdr-0 pdl-5">
              <Option />
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
