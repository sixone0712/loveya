import React, { Component } from "react";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../modules/viewList";
import * as API from '../../api'
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
    const logInfoList = API.getLogInfoList(props);
    const sectionList = this.createTargetSection(logInfoList);
    let sectionIdx = 0;
    let writeCount = 1;
    const targetList = logInfoList.map(item => {
      let title = sectionList[sectionIdx].title;
      writeCount++;
      if (writeCount > 10) {
        sectionIdx++;
        writeCount = 1;
      }
      return {
        title: title,
        ...item,
      }
    })

    this.state = {
      sectionList: sectionList,
      filteredData: targetList,
      query: "",
      ItemsChecked: false,
      showSearch: false
    };
 }

  handleSearchToggle = () => {
    const { showSearch } = this.state;

    this.setState({
      ...this.state,
      showSearch: !showSearch,
      query: ""
    }, () => {
      if (showSearch === true) {
        this.createFilteredData(API.getLogInfoList(this.props));
      }
    });
  };

  selectAllItem = async () => {
    const { ItemsChecked } = this.state;
    const newFilterData = this.state.filteredData.map(item => {
      item.checked = !ItemsChecked;
      return item;
    });

    await this.setState({
      ...this.state,
      ItemsChecked: !ItemsChecked,
      filteredData: newFilterData,
    }, () => {
      API.checkAllLogInfoList(this.props, !ItemsChecked);
    })
  };

  handleCheckboxClick = async e => {
    const idx = e.target.id.split('_{#div#}_')[1];

    const newFilterData = this.state.filteredData.map(item => {
      if(item.keyIndex === parseInt(idx)) {
        item.checked = !item.checked;
      }
      return item;
    });

    await this.setState({
      ...this.state,
      filteredData: newFilterData,
    }, () => {
      API.checkLogInfoList(this.props, idx);
    })
  }

  handleSearch = e => {
    const targetList = API.getLogInfoList(this.props);
    const query = e.target.value;
    const filteredData = targetList.filter(element => {
      return element.logName.toLowerCase().includes(query.toLowerCase());
    });

    this.setState({
      ...this.state,
      query: query
    }, () => {
      this.createFilteredData(filteredData);
    });
  };

  createFilteredData = list => {
    const sectionList = this.createTargetSection(list);
    let sectionIdx = 0;
    let writeCount = 1;

    const targetList = list.map(item => {
      let title = sectionList[sectionIdx].title;
      writeCount++;
      if (writeCount > 10) {
        sectionIdx++;
        writeCount = 1;
      }
      return {
        title: title,
        ...item,
      }
    })

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
                    className={"form-btn toggle-all" + (ItemsChecked ? " active" : "")}
                    onClick={this.selectAllItem}
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
                              handleCheckboxClick={this.handleCheckboxClick}
                          />
                        </div>
                    );
                  })
              ) : (
                  <div className="search-error-area">
                    <p>
                      <FontAwesomeIcon icon={faExclamationCircle} size="8x" />
                    </p>
                    <p>Target not found.</p>
                  </div>
              )}
            </FormGroup>
          </Col>
        </div>
    );
  }
}

const CreateCheckBox = props => {
  const { title, list, handleCheckboxClick } = props;

  return (
      <>
        {list.map((item, key) => {
          if (item.title === title) {
            return (
                <CheckBox
                    key={key}
                    index={item.keyIndex}
                    name={item.logName}
                    isChecked={item.checked}
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

export default connect(
    (state) => ({
      logInfoList: state.viewList.get('logInfoList'),
    }),
    (dispatch) => ({
      viewListActions: bindActionCreators(viewListActions, dispatch),
    })
)(RSSautoTargetlist);