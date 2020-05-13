import React, { Component } from "react";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../modules/viewList";
import * as API from '../../api'

import { Col, FormGroup, ButtonToggle } from "reactstrap";
import { Collapse } from "react-collapse";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBars } from "@fortawesome/free-solid-svg-icons";
import CheckBox from "../Common/CheckBox";

class RSSautomachinelist extends Component {
  constructor(props) {
    super(props);
    this.state = {
      ItemsChecked: false
    };
  }

  checkAutoMachineItem = (e) => {
    const idx = e.target.id.split('_')[1];
    API.checkToolInfoList(this.props, idx);
  };

  checkAutoAllMachineItem = (checked) => {
    this.setState({
      ...this.state,
      ItemsChecked: checked
    });
    API.checkAllToolInfoList(this.props, checked);
  };

  render() {
    const { ItemsChecked } = this.state;
    const titleList = API.getEquipmentList(this.props);
    const machineList = API.getToolInfoList(this.props);

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
                    onClick={()=> this.checkAutoAllMachineItem(!ItemsChecked)}
                >
                  All
                </ButtonToggle>
              </div>
            </div>
            <FormGroup className="custom-scrollbar auto-plan-form-group machinelist">
              {titleList.map((title, index) => {
                console.log(title, index);
                return (
                    <div className="machine-section" key={index}>
                      <MachineCollapse
                          structId={title.equipmentId}
                          machineList={machineList}
                          checkItem={this.checkAutoMachineItem}
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
    this.state = {
      isOpened: true
    };
  }

  toggle = () => {
    this.setState({
      isOpened: !this.state.isOpened
    });
  };

  render() {
    const { isOpened } = this.state;
    const { machineList, structId, checkItem } = this.props;

    return (
        <>
          <div className="collapse-title" onClick={this.toggle}>
            <FontAwesomeIcon icon={faBars} /> {structId}
          </div>
          <Collapse isOpened={isOpened}>
            {machineList.map((machine, key) => {
              if (machine.structId === structId) {
                return (
                    <CheckBox
                        key={key}
                        index={machine.keyIndex}
                        name={machine.targetname}
                        isChecked={machine.checked}
                        handleCheckboxClick={checkItem}
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

export default connect(
    (state) => ({
      equipmentList: state.viewList.get('equipmentList'),
      toolInfoList: state.viewList.get('toolInfoList'),
    }),
    (dispatch) => ({
      viewListActions: bindActionCreators(viewListActions, dispatch),
    })
)(RSSautomachinelist);