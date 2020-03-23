import React, { Component } from "react";
import { Card, CardBody, Col, FormGroup, ButtonToggle } from "reactstrap";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../modules/viewList";
import * as API from '../../api'
import EquipmentCollapse from "./EquipmentCollapse";

const formGroupStyle = {
  marginBottom: "0px",
  fontSize: "15px"
};

const buttonPosition = {
  position: "absolute",
  top: "17px",
  right: "20px"
};

class MachineList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      ItemsChecked: false
    };
  }

  checkMachineItem = (e) => {
    const idx = e.target.id.split('_')[1];
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
    const { checkedList, ItemsChecked } = this.state;
    const titleList = API.getEquipmentList(this.props);
    const machineList = API.getToolInfoList(this.props);

    return (
      <Card className="ribbon-wrapper machinelist-custom">
        <CardBody className="custom-scrollbar card-body-custom card-body-machinelist">
          <div className="ribbon ribbon-clip ribbon-primary">Machine</div>
          <Col>
            <FormGroup style={formGroupStyle}>
              {titleList.map((title, index) => {
                return (
                  <EquipmentCollapse
                    key={index}
                    structId={title.equipmentId}
                    machineList={machineList}
                    checkedList={checkedList}
                    checkMachineItem={this.checkMachineItem}
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
      // bindActionCreators 는 액션함수들을 자동으로 바인딩해줍니다.
      viewListActions: bindActionCreators(viewListActions, dispatch),
    })
)(MachineList);
