import React, { Component } from "react";
import { Card, CardBody, Col, FormGroup, ButtonToggle } from "reactstrap";
import { Collapse } from "react-collapse";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBars } from "@fortawesome/free-solid-svg-icons";
import Checkbox from "./checkbox";

const formGroupStyle = {
  marginBottom: "0px",
  fontSize: "15px"
};

const buttonPosition = {
  position: "absolute",
  top: "17px",
  right: "20px"
};

class RSSmachinelist extends Component {
  constructor(props) {
    super(props);
    this.state = {
      titleList: [
        {
          structId: "Equipment"
        },
        {
          structId: "System"
        }
      ],
      machineList: [
        {
          structId: "Equipment",
          id: "mc1",
          name: "Machine 1"
        },
        {
          structId: "Equipment",
          id: "mc2",
          name: "Machine 2"
        },
        {
          structId: "Equipment",
          id: "mc3",
          name: "Machine 3"
        },
        {
          structId: "Equipment",
          id: "mc4",
          name: "Machine 4"
        },
        {
          structId: "Equipment",
          id: "mc5",
          name: "Machine 5"
        },
        {
          structId: "System",
          id: "mc6",
          name: "Machine 6"
        },
        {
          structId: "System",
          id: "mc7",
          name: "Machine 7"
        },
        {
          structId: "System",
          id: "mc8",
          name: "Machine 8"
        },
        {
          structId: "System",
          id: "mc9",
          name: "Machine 9"
        },
        {
          structId: "System",
          id: "mc10",
          name: "Machine 10"
        }
      ],
      checkedList: [],
      ItemsChecked: false
    };
  }

  selectItem = () => {
    const { ItemsChecked, machineList } = this.state;
    const collection = [];

    if (!ItemsChecked) {
      for (const machine of machineList) {
        collection.push(machine.id);
      }
    }

    this.setState({
      checkedList: collection,
      ItemsChecked: !ItemsChecked
    });
  };

  render() {
    const { machineList, checkedList, titleList, ItemsChecked } = this.state;

    return (
      <Card className="ribbon-wrapper machinelist-custom">
        <CardBody className="custom-scrollbar card-body-custom card-body-machinelist">
          <div className="ribbon ribbon-clip ribbon-primary">Machine</div>
          <Col>
            <FormGroup style={formGroupStyle}>
              {titleList.map((title, index) => {
                return (
                  <MachineCollapse
                    key={index}
                    structId={title.structId}
                    machineList={machineList}
                    checkedList={checkedList}
                  />
                );
              })}
            </FormGroup>
          </Col>
          <div style={buttonPosition}>
            <ButtonToggle
              outline
              size="sm"
              color="info"
              className={"machinelist-btn" + (ItemsChecked ? " active" : "")}
              onClick={this.selectItem.bind(this)}
            >
              All
            </ButtonToggle>
          </div>
        </CardBody>
      </Card>
    );
  }
}

class MachineCollapse extends Component {
  constructor(props) {
    super(props);
    const { machineList, checkedList, structId } = this.props;
    this.toggle = this.toggle.bind(this);
    this.handleCheckboxClick = this.handleCheckboxClick.bind(this);
    this.state = {
      machineList,
      checkedList,
      structId,
      isOpened: false
    };
  }

  toggle = () => {
    this.setState({
      isOpened: !this.state.isOpened
    });
  };

  handleCheckboxClick = e => {
    const { id, checked } = e.target;

    if (checked) {
      this.setState(prevState => ({
        checkedList: [...prevState.checkedList, id]
      }));
    } else {
      this.setState(prevState => ({
        checkedList: prevState.checkedList.filter(item => item !== id)
      }));
    }
  };

  render() {
    const { machineList, checkedList, structId, isOpened } = this.state;
    const titleStyle = {
      border: "1px solid rgba(171, 140, 228, 0.3)",
      borderRadius: "3px 3px 0 0",
      padding: "0.5rem",
      backgroundColor: "rgba(171, 140, 228, 0.1)",
      cursor: "pointer"
    };
    return (
      <>
        <div style={titleStyle} onClick={this.toggle}>
          <FontAwesomeIcon icon={faBars} /> {structId}
        </div>
        <Collapse isOpened={isOpened}>
          {machineList.map((machine, index) => {
            if (machine.structId === structId) {
              return (
                <Checkbox
                  item={machine}
                  key={index}
                  isChecked={checkedList.includes(machine.id)}
                  handleCheckboxClick={this.handleCheckboxClick}
                  labelClass="machinelist-label"
                />
              );
            }
            return "";
          })}
        </Collapse>
      </>
    );
  }
}

export default RSSmachinelist;
