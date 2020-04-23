import React, { Component } from "react";
import { Col, FormGroup, ButtonToggle, Input, Collapse } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faSearch,
  faExclamationCircle
} from "@fortawesome/free-solid-svg-icons";
import CheckBox from "../Common/CheckBox";

class RSSautoTargetlist extends Component {
  constructor(props) {
    super(props);
    this.state = {
      targetlist: [
        {
          id: "tg1",
          name: "Target 1",
          value: 1
        },
        {
          id: "tg2",
          name: "Target 2",
          value: 2
        },
        {
          id: "tg3",
          name: "Target 3",
          value: 3
        },
        {
          id: "tg4",
          name: "Target 4",
          value: 4
        },
        {
          id: "tg5",
          name: "Target 5",
          value: 5
        },
        {
          id: "tg6",
          name: "Target 6",
          value: 6
        },
        {
          id: "tg7",
          name: "Target 7",
          value: 7
        },
        {
          id: "tg8",
          name: "Target 8",
          value: 8
        },
        {
          id: "tg9",
          name: "Target 9",
          value: 9
        },
        {
          id: "tg10",
          name: "Target 10",
          value: 10
        }
      ],
      checkedList: [],
      filteredData: [],
      query: "",
      ItemsChecked: false,
      showSearch: false
    };
  }

  handleSearchToggle = () => {
    const { showSearch } = this.state;

    if (showSearch === true) {
      const { targetlist } = this.state;

      this.setState({
        filteredData: targetlist
      });
    }

    this.setState({
      showSearch: !showSearch,
      query: ""
    });
  };

  selectItem = () => {
    const { ItemsChecked, targetlist } = this.state;
    const collection = [];

    if (!ItemsChecked) {
      for (const cat of targetlist) {
        collection.push(cat.value);
      }
    }

    this.setState({
      checkedList: collection,
      ItemsChecked: !ItemsChecked
    });
  };

  handleCheckboxClick = e => {
    const { value, checked } = e.target;

    if (checked) {
      this.setState(prevState => ({
        checkedList: [...prevState.checkedList, value * 1]
      }));
    } else {
      this.setState(prevState => ({
        checkedList: prevState.checkedList.filter(item => item != value)
      }));
    }
  };

  handleSearch = e => {
    const query = e.target.value;

    this.setState(prevState => {
      const filteredData = prevState.targetlist.filter(element => {
        return element.name.toLowerCase().includes(query.toLowerCase());
      });

      return { query, filteredData };
    });
  };

  componentDidMount() {
    const { targetlist } = this.state;

    this.setState({
      filteredData: targetlist
    });
  }

  render() {
    const {
      showSearch,
      filteredData,
      query,
      checkedList,
      ItemsChecked
    } = this.state;

    return (
        <div className="form-section targetlist">
          <Col className="pdl-10 pdr-0">
            <div className="form-section-header">
              <div className="form-section-title">
                Target List
                <p>Select a target from the list.</p>
              </div>
              <div>
                <ButtonToggle
                    outline
                    size="sm"
                    color="info"
                    className={"form-btn" + (showSearch ? " active" : "")}
                    onClick={this.handleSearchToggle}
                >
                  <FontAwesomeIcon icon={faSearch} />
                </ButtonToggle>{" "}
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
            <FormGroup className="custom-scrollbar auto-plan-form-group targetlist pd-5">
              <Collapse isOpen={showSearch}>
                <FormGroup>
                  <Input
                      type="text"
                      className="form-search-input"
                      placeholder="Enter the Target name to search."
                      value={query}
                      onChange={this.handleSearch}
                  />
                </FormGroup>
              </Collapse>
              {filteredData.length > 0 ? (
                  filteredData.map(cat => {
                    return (
                        <CheckBox
                            item={cat}
                            key={cat.value}
                            isChecked={checkedList.includes(cat.value)}
                            handleCheckboxClick={this.handleCheckboxClick}
                            labelClass="form-check-label"
                        />
                    );
                  })
              ) : (
                  <div>
                    <p style={{ marginTop: "3em", textAlign: "center" }}>
                      <FontAwesomeIcon icon={faExclamationCircle} size="6x" />
                    </p>
                    <p style={{ textAlign: "center" }}>Target not found.</p>
                  </div>
              )}
            </FormGroup>
          </Col>
        </div>
    );
  }
}

export default RSSautoTargetlist;
