import React, { Component } from "react";
import { Col, FormGroup, ButtonToggle } from "reactstrap";
import { Collapse } from "react-collapse";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBars } from "@fortawesome/free-solid-svg-icons";
import CheckBox from "../Common/CheckBox";

class RSSautomachinelist extends Component {
  constructor(props) {
    super(props);
    this.state = {
      titleList: [
        { structId: "Equipment" },
        { structId: "System" },
        { structId: "CR7" },
        { structId: "RSS" },
        { structId: "BBC" },
        { structId: "RMC" },
        { structId: "LQF" },
        { structId: "BLT" },
        { structId: "MRC" },
        { structId: "CP3" }
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
        },
        {
          structId: "CR7",
          id: "mc11",
          name: "Machine 11"
        },
        {
          structId: "CR7",
          id: "mc12",
          name: "Machine 12"
        },
        {
          structId: "CR7",
          id: "mc13",
          name: "Machine 13"
        },
        {
          structId: "CR7",
          id: "mc14",
          name: "Machine 14"
        },
        {
          structId: "CR7",
          id: "mc15",
          name: "Machine 15"
        },
        {
          structId: "RSS",
          id: "mc16",
          name: "Machine 16"
        },
        {
          structId: "RSS",
          id: "mc17",
          name: "Machine 17"
        },
        {
          structId: "RSS",
          id: "mc18",
          name: "Machine 18"
        },
        {
          structId: "RSS",
          id: "mc19",
          name: "Machine 19"
        },
        {
          structId: "RSS",
          id: "mc20",
          name: "Machine 20"
        },
        {
          structId: "BBC",
          id: "mc21",
          name: "Machine 21"
        },
        {
          structId: "BBC",
          id: "mc22",
          name: "Machine 22"
        },
        {
          structId: "BBC",
          id: "mc23",
          name: "Machine 23"
        },
        {
          structId: "BBC",
          id: "mc24",
          name: "Machine 24"
        },
        {
          structId: "BBC",
          id: "mc25",
          name: "Machine 25"
        },
        {
          structId: "RMC",
          id: "mc26",
          name: "Machine 26"
        },
        {
          structId: "RMC",
          id: "mc27",
          name: "Machine 27"
        },
        {
          structId: "RMC",
          id: "mc28",
          name: "Machine 28"
        },
        {
          structId: "RMC",
          id: "mc29",
          name: "Machine 29"
        },
        {
          structId: "RMC",
          id: "mc30",
          name: "Machine 30"
        },
        {
          structId: "LQF",
          id: "mc31",
          name: "Machine 31"
        },
        {
          structId: "LQF",
          id: "mc32",
          name: "Machine 32"
        },
        {
          structId: "LQF",
          id: "mc33",
          name: "Machine 33"
        },
        {
          structId: "LQF",
          id: "mc34",
          name: "Machine 34"
        },
        {
          structId: "LQF",
          id: "mc35",
          name: "Machine 35"
        },
        {
          structId: "BLT",
          id: "mc36",
          name: "Machine 36"
        },
        {
          structId: "BLT",
          id: "mc37",
          name: "Machine 37"
        },
        {
          structId: "BLT",
          id: "mc38",
          name: "Machine 38"
        },
        {
          structId: "BLT",
          id: "mc39",
          name: "Machine 39"
        },
        {
          structId: "BLT",
          id: "mc40",
          name: "Machine 40"
        },
        {
          structId: "MRC",
          id: "mc41",
          name: "Machine 41"
        },
        {
          structId: "MRC",
          id: "mc42",
          name: "Machine 42"
        },
        {
          structId: "MRC",
          id: "mc43",
          name: "Machine 43"
        },
        {
          structId: "MRC",
          id: "mc44",
          name: "Machine 44"
        },
        {
          structId: "MRC",
          id: "mc45",
          name: "Machine 45"
        },
        {
          structId: "CP3",
          id: "mc46",
          name: "Machine 46"
        },
        {
          structId: "CP3",
          id: "mc47",
          name: "Machine 47"
        },
        {
          structId: "CP3",
          id: "mc48",
          name: "Machine 48"
        },
        {
          structId: "CP3",
          id: "mc49",
          name: "Machine 49"
        },
        {
          structId: "CP3",
          id: "mc50",
          name: "Machine 50"
        },
        {
          structId: "CP3",
          id: "mc51",
          name: "Machine 51"
        },
        {
          structId: "CP3",
          id: "mc52",
          name: "Machine 52"
        },
        {
          structId: "CP3",
          id: "mc53",
          name: "Machine 53"
        },
        {
          structId: "CP3",
          id: "mc54",
          name: "Machine 54"
        },
        {
          structId: "CP3",
          id: "mc55",
          name: "Machine 55"
        },
        {
          structId: "CP3",
          id: "mc56",
          name: "Machine 56"
        },
        {
          structId: "CP3",
          id: "mc57",
          name: "Machine 57"
        },
        {
          structId: "CP3",
          id: "mc58",
          name: "Machine 58"
        },
        {
          structId: "CP3",
          id: "mc59",
          name: "Machine 59"
        },
        {
          structId: "CP3",
          id: "mc60",
          name: "Machine 60"
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
        <div className="form-section machinelist">
          <Col className="pd-0">
            <div className="form-header-section">
              <div className="form-title-section">
                Machine List
                <p>Select a machine from the list.</p>
              </div>
              <div className="form-btn-section">
                <ButtonToggle
                    outline
                    size="sm"
                    color="info"
                    className={"form-btn" + (ItemsChecked ? " active" : "")}
                    onClick={this.selectItem}
                >
                  All
                </ButtonToggle>
              </div>
            </div>
            <FormGroup className="custom-scrollbar auto-plan-form-group machinelist">
              {titleList.map((title, index) => {
                return (
                    <div className="machine-section" key={index}>
                      <MachineCollapse
                          structId={title.structId}
                          machineList={machineList}
                          checkedList={checkedList}
                      />
                    </div>
                );
              })}
            </FormGroup>
          </Col>
        </div>
    );
  }
}

class MachineCollapse extends Component {
  constructor(props) {
    super(props);
    const { machineList, checkedList, structId } = this.props;
    this.state = {
      machineList,
      checkedList,
      structId,
      isOpened: true
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

    return (
        <>
          <div className="collapse-title" onClick={this.toggle}>
            <FontAwesomeIcon icon={faBars} /> {structId}
          </div>
          <Collapse isOpened={isOpened}>
            {machineList.map((machine, index) => {
              if (machine.structId === structId) {
                return (
                    <CheckBox
                        item={machine}
                        key={index}
                        isChecked={checkedList.includes(machine.id)}
                        handleCheckboxClick={this.handleCheckboxClick}
                        labelClass="form-check-label"
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

export default RSSautomachinelist;