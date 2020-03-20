import React, { Component } from "react";
import { Card, CardBody, Col, FormGroup, ButtonToggle } from "reactstrap";
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
    this.handleCheckboxClick = this.handleCheckboxClick.bind(this);
    this.state = {
      machinelist: [
        {
          id: "mc1",
          name: "Machine 1",
          value: 1
        },
        {
          id: "mc2",
          name: "Machine 2",
          value: 2
        },
        {
          id: "mc3",
          name: "Machine 3",
          value: 3
        },
        {
          id: "mc4",
          name: "Machine 4",
          value: 4
        },
        {
          id: "mc5",
          name: "Machine 5",
          value: 5
        },
        {
          id: "mc6",
          name: "Machine 6",
          value: 6
        },
        {
          id: "mc7",
          name: "Machine 7",
          value: 7
        },
        {
          id: "mc8",
          name: "Machine 8",
          value: 8
        },
        {
          id: "mc9",
          name: "Machine 9",
          value: 9
        },
        {
          id: "mc10",
          name: "Machine 10",
          value: 10
        }
      ],
      checkedListAll: [],
      ItemsChecked: false
    };
  }

  selectedItems = e => {
    const { value, checked } = e.target;
    let { checkedListAll } = this.state;

    if (checked) {
      checkedListAll = [...checkedListAll, value];
    } else {
      checkedListAll = checkedListAll.filter(item => item !== value);
      if (this.state.ItemsChecked) {
        this.setState({
          ItemsChecked: !this.state.ItemsChecked
        });
      }
    }
    this.setState({ checkedListAll });
  };

  selectItem = () => {
    const { ItemsChecked, machinelist } = this.state;
    const collection = [];

    if (!ItemsChecked) {
      for (const machine of machinelist) {
        collection.push(machine.value);
      }
    }

    this.setState({
      checkedListAll: collection,
      ItemsChecked: !ItemsChecked
    });
  };

  handleCheckboxClick = e => {
    const { value, checked } = e.target;

    if (checked) {
      this.setState(prevState => ({
        checkedListAll: [...prevState.checkedListAll, value * 1]
      }));
    } else {
      this.setState(prevState => ({
        checkedListAll: prevState.checkedListAll.filter(item => item != value)
      }));
    }
  };

  render() {
    const { machinelist, checkedListAll, ItemsChecked } = this.state;

    return (
      <Card className="ribbon-wrapper machinelist-custom">
        <CardBody className="custom-scrollbar card-body-custom card-body-machinelist">
          <div className="ribbon ribbon-clip ribbon-primary">Machine</div>
          <Col>
            <FormGroup style={formGroupStyle}>
              {machinelist.map(machine => {
                return (
                  <Checkbox
                    item={machine}
                    key={machine.value}
                    selectedItems={this.selectedItems.bind(this)}
                    ItemsChecked={ItemsChecked}
                    isChecked={checkedListAll.includes(machine.value)}
                    handleCheckboxClick={this.handleCheckboxClick}
                    labelClass="machinelist-label"
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

export default RSSmachinelist;
