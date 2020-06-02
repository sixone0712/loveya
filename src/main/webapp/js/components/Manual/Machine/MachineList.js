import React, { Component } from "react";
import { Card, CardBody, Col, FormGroup, ButtonToggle } from "reactstrap";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../../modules/viewList";
import * as API from '../../../api'
import EquipmentCollapse from "./EquipmentCollapse";

class MachineList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      ItemsChecked: false
    };
  }

  checkMachineItem = (e) => {
    const idx = e.target.id.split('_{#div#}_')[1];
    API.checkToolInfoList(this.props, idx);
  };

  checkAllMachineItem = (checked) => {
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
      <Card className="ribbon-wrapper machinelist-card">
        <CardBody className="custom-scrollbar manual-card-body">
          <div className="ribbon ribbon-clip ribbon-primary">Machine</div>
          <Col>
            <FormGroup className="machinelist-form-group">
              {titleList.map((title, index) => {
                return (
                  <EquipmentCollapse
                    key={index}
                    structId={title.equipmentId}
                    machineList={machineList}
                    checkMachineItem={this.checkMachineItem}
                  />
                );
              })}
            </FormGroup>
          </Col>
          <div className="card-btn-area">
            <ButtonToggle
              outline
              size="sm"
              color="info"
              className={"machinelist-btn" + (ItemsChecked ? " active" : "")}
              onClick={()=> this.checkAllMachineItem(!ItemsChecked)}
            >
              All
            </ButtonToggle>
          </div>
        </CardBody>
      </Card>
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
)(MachineList);
