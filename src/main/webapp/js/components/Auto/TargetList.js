import React, { Component } from "react";
import { Col, FormGroup, ButtonToggle, Input } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faSearch,
  faExclamationCircle
} from "@fortawesome/free-solid-svg-icons";
import CheckBox from "../Common/CheckBox";

const SECTION_DISPLAY_ITEM = 10;

class RSSautoTargetlist extends Component {
  constructor(props) {
    super(props);
    this.state = {
      targetList: [
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
        },
        {
          id: "tg11",
          name: "Target 11",
          value: 11
        },
        {
          id: "tg12",
          name: "Target 12",
          value: 12
        },
        {
          id: "tg13",
          name: "Target 13",
          value: 13
        },
        {
          id: "tg14",
          name: "Target 14",
          value: 14
        },
        {
          id: "tg15",
          name: "Target 15",
          value: 15
        },
        {
          id: "tg16",
          name: "Target 16",
          value: 16
        },
        {
          id: "tg17",
          name: "Target 17",
          value: 17
        },
        {
          id: "tg18",
          name: "Target 18",
          value: 18
        },
        {
          id: "tg19",
          name: "Target 19",
          value: 19
        },
        {
          id: "tg20",
          name: "Target 20",
          value: 20
        }
      ],
      checkedList: [],
      sectionList: [],
      filteredData: [],
      query: "",
      ItemsChecked: false,
      showSearch: false
    };
  }

  handleSearchToggle = () => {
    const { showSearch, targetList } = this.state;

    if (showSearch === true) {
      this.createFilteredData(targetList);
    }

    this.setState({
      showSearch: !showSearch,
      query: ""
    });
  };

  selectItem = () => {
    const { ItemsChecked, targetList } = this.state;
    const collection = [];

    if (!ItemsChecked) {
      for (const cat of targetList) {
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
        checkedList: [...prevState.checkedList, parseInt(value, 10)]
      }));
    } else {
      this.setState(prevState => ({
        checkedList: prevState.checkedList.filter(
            item => item !== parseInt(value, 10)
        )
      }));
    }
  };

  handleSearch = e => {
    const { targetList } = this.state;
    const query = e.target.value;
    const filteredData = targetList.filter(element => {
      return element.name.toLowerCase().includes(query.toLowerCase());
    });

    this.setState({
      query: query
    });

    this.createFilteredData(filteredData);
  };

  createFilteredData = list => {
    const sectionList = this.createTargetSection(list);
    const targetList = [];
    let sectionIdx = 0;
    let writeCount = 1;

    for (let targetIdx = 0; targetIdx < list.length; targetIdx++) {
      let title = sectionList[sectionIdx].title;

      const tempData = {
        title: title,
        id: list[targetIdx].id,
        name: list[targetIdx].name,
        value: list[targetIdx].value
      };

      targetList.push(tempData);
      writeCount++;

      if (writeCount > 10) {
        sectionIdx++;
        writeCount = 1;
      }
    }

    this.setState({
      sectionList: sectionList,
      filteredData: targetList
    });
  };

  createTargetSection = list => {
    const count =
        list.length < SECTION_DISPLAY_ITEM
            ? 1
            : Math.ceil(list.length / SECTION_DISPLAY_ITEM);
    const targetSection = [];

    for (let idx = 1; idx <= count; idx++) {
      const tempData = { title: "section" + idx };
      targetSection.push(tempData);
    }

    return targetSection;
  };

  componentDidMount() {
    this.createFilteredData(this.state.targetList);
  }

  render() {
    const {
      showSearch,
      sectionList,
      filteredData,
      query,
      checkedList,
      ItemsChecked
    } = this.state;

    return (
        <div className="form-section targetlist">
          <Col className="pdl-10 pdr-0">
            <div className="form-header-section">
              <div className="form-title-section">
                Target List
                <p>Select a target from the list.</p>
              </div>
              <div className="form-btn-section dis-flex">
                <div
                    className={"search-btn-area" + (showSearch ? " active" : "")}
                >
                  <ButtonToggle
                      outline
                      size="sm"
                      color="info"
                      className={"form-btn" + (showSearch ? " active" : "")}
                      onClick={this.handleSearchToggle}
                  >
                    <FontAwesomeIcon icon={faSearch} />
                  </ButtonToggle>
                  <Input
                      type="text"
                      className="form-search-input"
                      placeholder="Enter the Target name to search."
                      value={query}
                      onChange={this.handleSearch}
                  />
                </div>
                <ButtonToggle
                    outline
                    size="sm"
                    color="info"
                    className={"form-btn" + (ItemsChecked ? " active" : "")}
                    onClick={this.selectItem}
                    style={{ zIndex: "2" }}
                >
                  All
                </ButtonToggle>
              </div>
            </div>
            <FormGroup className="custom-scrollbar auto-plan-form-group targetlist pd-5">
              {filteredData.length > 0 ? (
                  sectionList.map(section => {
                    return (
                        <div key={section.title} className="checkbox-section">
                          <CreateCheckBox
                              title={section.title}
                              list={filteredData}
                              checkedList={checkedList}
                              handleCheckboxClick={this.handleCheckboxClick}
                          />
                        </div>
                    );
                  })
              ) : (
                  <div style={{ alignSelf: "center", flex: "auto" }}>
                    <p style={{ textAlign: "center" }}>
                      <FontAwesomeIcon icon={faExclamationCircle} size="8x" />
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

const CreateCheckBox = props => {
  const { title, list, checkedList, handleCheckboxClick } = props;

  return (
      <>
        {list.map(item => {
          if (item.title === title) {
            return (
                <CheckBox
                    item={item}
                    key={item.value}
                    isChecked={checkedList.includes(item.value)}
                    handleCheckboxClick={handleCheckboxClick}
                    labelClass="form-check-label"
                />
            );
          } else {
            return "";
          }
        })}
      </>
  );
};

export default RSSautoTargetlist;