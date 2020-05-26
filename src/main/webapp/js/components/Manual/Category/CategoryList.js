import React, { Component } from "react";
import {
  Card,
  CardBody,
  Col,
  FormGroup,
  ButtonToggle,
  Input,
  Collapse
} from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch, faExclamationCircle } from "@fortawesome/free-solid-svg-icons";
import Select from "react-select";
import CheckBox from "../../Common/CheckBox";
import InputModal from "./InputModal";
import ConfirmModal from "./ConfirmModal";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../../modules/viewList";
import * as genreListActions from "../../../modules/genreList";
import * as API from '../../../api'
import {element, object} from "prop-types";

const customSelectStyles = {
  container: (styles, { isDisabled }) => ({
    ...styles,
    transition: "all .2s ease-in-out",
    backgroundColor: isDisabled ? "rgba(0,0,0,0.12)" : null
  }),
  option: (styles, { isFocused, isSelected }) => {
    return {
      ...styles,
      backgroundColor: isSelected
          ? "rgba(255, 169, 77, 0.5)"
          : isFocused
              ? "rgba(255, 169, 77, 0.3)"
              : null,
      color: "black",
      ":active": {
        ...styles[":active"],
        backgroundColor: isSelected
            ? "rgba(255, 169, 77, 0.9)"
            : isFocused
                ? "rgba(255, 169, 77, 0.7)"
                : null
      }
    };
  },
  control: () => ({
    display: "flex",
    border: "1px solid #ffa94d",
    borderRadius: "3px",
    transition:
        "all .15s ease-in-out",
    ":hover": {
      outline: "0",
      boxShadow: "0 0 0 0.2em rgba(255, 169, 77, 0.5)"
    }
  }),
  dropdownIndicator: styles => ({
    ...styles,
    color: "rgba(255, 169, 77, 0.6)",
    ":hover": {
      ...styles[":hover"],
      color: "rgba(255, 169, 77, 1)"
    }
  }),
  indicatorSeparator: styles => ({
    ...styles,
    backgroundColor: "rgba(255, 169, 77, 0.6)"
  }),
  menu: styles => ({
    ...styles,
    borderRadius: "3px",
    boxShadow:
        "0 0 0 1px rgba(255, 169, 77, 0.6), 0 4px 11px rgba(255, 169, 77, 0.6)"
  })
};

class CategoryList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      nowAction: "",
      ItemsChecked: false,
      showGenre: false,
      showSearch: false,
      query: "",
      selectedGenre: 0,
      selectedGenreName: "Select genre..."
    };
  }

  handleGenreToggle = () => {
    if (this.state.showSearch) {
      this.setState({
        showSearch: !this.state.showSearch,
        query: ""
      });

      setTimeout(() => {
        this.setState({
          showGenre: !this.state.showGenre
        });
      }, 400);
    } else {
      this.setState({
        showGenre: !this.state.showGenre
      });
    }
  };

  handleSearchToggle = () => {
    if (this.state.showGenre) {
      this.setState({
        showGenre: false
      });

      setTimeout(() => {
        this.setState({
          showSearch: !this.state.showSearch,
          query: ""
        });
      }, 400);
    } else {
      this.setState({
        showSearch: !this.state.showSearch,
        query: ""
      });
    }
  };

  handleSelectBoxChange = async (genre) => {
    let id = 0;
    let name = "Select genre...";

    if (typeof(genre) === "object") {
      id = genre === null ? 0 : genre.value;
    } else {
      id = genre;
    }

    if (id !== 0) {
      await API.selectGenreList(this.props, id);
      const genreList = await API.getGenreList(this.props).list;
      const findGenre = genreList.find(item => item.id == id);
      name = findGenre.name;
    } else {
      await API.selectGenreList(this.props, 0);
    }

    await this.setState({
        ...this.state,
        selectedGenre: id,
        selectedGenreName : name
    });
  };

  getSelectedIdByName = (name) => {
    const genreList = API.getGenreList(this.props);
    console.log("getSelectedIdByName.genreList", genreList);
    const findList = genreList.list.find(item => {
      console.log(item.name, name);
      return item.name == name
    });
    console.log("getSelectedIdByName.findList", findList);
    return findList.id;
  }

  checkCategoryItem = (e) => {
    const idx = e.target.id.split('_{#div#}_')[1];
    API.checkLogInfoList(this.props, idx);
  };

  checkAllLogInfoList = (checked) => {
    this.setState({
      ...this.state,
      ItemsChecked: checked
    });
    API.checkAllLogInfoList(this.props, checked);
  };

  addGenreList = async (id, name) => {
    console.log("[CategoryList] addGenreList");
    const result = await API.addGenreList(this.props, name);
    console.log("result", result);
    return result;
  };

  editGenreList = async (id, name) => {
    console.log("[CategoryList] editGenreList");
    const result = await API.editGenreList(this.props, id, name);
    console.log("result", result);
    return result;
  };

  deleteGenreList = async (id, name) => {
    console.log("[CategoryList] deleteGenreList");
    const result =  await API.deleteGenreList(this.props, id);
    console.log("result", result);
    return result;
  };

  handleSearch = e => {
    const query = e.target.value;

    this.setState({
      query: query
    });
  };

  setNowAction = async (name) => {
    await this.setState({
      ...this.state,
      nowAction: name
    })
  };

  selectFilter = ({ label }, string) => {
    if (string.toString().length === 0) {
      return true;
    }

    return label === string;
  }

  render() {
    const {
      showGenre,
      showSearch,
      ItemsChecked,
      selectedGenre,
      query
    } = this.state;

    const categorylist  = API.getLogInfoList(this.props);
    const genreList = API.getGenreList(this.props);
    const filteredData = categorylist.filter(element => {
      return element.logName.toLowerCase().includes(query.toLowerCase());
    });
    console.log("genreList", genreList);

    return (
        <Card className="ribbon-wrapper catlist-card">
          <CardBody className="custom-scrollbar manual-card-body">
            <div className="ribbon ribbon-clip ribbon-secondary">
              File Category
            </div>
            <Col>
              <FormGroup className="catlist-form-group">
                <Collapse isOpen={showGenre}>
                  <div className="catlist-genre-area">
                    <div className="genre-select-area">
                      <Select
                          isSearchable={ genreList.totalCnt > 0 }
                          isDisabled={ genreList.totalCnt === 0 }
                          onChange={this.handleSelectBoxChange}
                          options={ genreList.totalCnt > 0 &&
                            genreList.list.map((list) => {
                              return { value: list.id, label: list.name };
                            })
                          }
                          value={ {value: this.state.selectedGenre, label:this.state.selectedGenreName} }
                          placeholder={"Select genre..."}
                          styles={customSelectStyles}
                          noOptionsMessage={() => "Genre not found."}
                          filterOption={this.selectFilter}
                          onInputChange={inputValue =>
                              (inputValue.length <= 20 ? inputValue : inputValue.substr(0, 20))
                          }
                      />
                    </div>
                    <div className="genre-btn-area">
                      <InputModal
                          title={"Create Genre"}
                          openbtn={"Create"}
                          inputname={"genName"}
                          inputpholder={"Enter Genre Name"}
                          leftbtn={"Create"}
                          rightbtn={"Cancel"}
                          nowAction={this.state.nowAction}
                          setNowAction={this.setNowAction}
                          confirmFunc={this.addGenreList}
                          selectedGenre={this.state.selectedGenre}
                          selectedGenreName={this.state.selectedGenreName}
                          logInfoListCheckCnt={this.props.logInfoListCheckCnt}
                          handleSelectBoxChange={(selectedGenre) => this.handleSelectBoxChange(selectedGenre)}
                          getSelectedIdByName={this.getSelectedIdByName}
                      />
                      <InputModal
                          title={"Edit Genre"}
                          openbtn={"Edit"}
                          inputname={"genName"}
                          inputpholder={"Edit Genre Name"}
                          leftbtn={"Edit"}
                          rightbtn={"Cancel"}
                          nowAction={this.state.nowAction}
                          setNowAction={this.setNowAction}
                          confirmFunc={this.editGenreList}
                          selectedGenre={this.state.selectedGenre}
                          selectedGenreName={this.state.selectedGenreName}
                          logInfoListCheckCnt={this.props.logInfoListCheckCnt}
                          handleSelectBoxChange={(selectedGenre) => this.handleSelectBoxChange(selectedGenre)}
                          getSelectedIdByName={this.getSelectedIdByName}
                       />
                      <ConfirmModal
                          openbtn={"Delete"}
                          message={"Do you want to delete the selected genre?"}
                          leftbtn={"Delete"}
                          rightbtn={"Cancel"}
                          nowAction={this.state.nowAction}
                          setNowAction={this.setNowAction}
                          confirmFunc={this.deleteGenreList}
                          selectedGenre={this.state.selectedGenre}
                          selectedGenreName={this.state.selectedGenreName}
                          handleSelectBoxChange={(selectedGenre) => this.handleSelectBoxChange(selectedGenre)}
                          getSelectedIdByName={this.getSelectedIdByName}
                      />
                    </div>
                  </div>
                </Collapse>
                <Collapse isOpen={showSearch}>
                  <FormGroup>
                    <Input
                        type="text"
                        className="catlist-search-input"
                        placeholder="Enter the category name to search."
                        value={query}
                        onChange={this.handleSearch}
                    />
                  </FormGroup>
                </Collapse>
                {filteredData.length > 0 ? (
                    filteredData.map((cat, key) => {
                      return (
                          <CheckBox
                              key={key}
                              index={cat.keyIndex}
                              name={cat.logName}
                              isChecked={cat.checked}
                              handleCheckboxClick={this.checkCategoryItem}
                              labelClass="catlist-label"
                          />
                      );
                    })
                ) : (
                    <div className="no-search-genre">
                      <p>
                        <FontAwesomeIcon icon={faExclamationCircle} size="6x" />
                      </p>
                      <p>Category not found.</p>
                    </div>
                )}
              </FormGroup>
            </Col>
            <div className="card-btn-area">
              <ButtonToggle
                  outline
                  size="sm"
                  color="info"
                  className={"catlist-btn catlist-btn-toggle" + (showSearch ? " active" : "")}
                  onClick={this.handleSearchToggle}
              >
                <FontAwesomeIcon icon={faSearch} />
              </ButtonToggle>{" "}
              <ButtonToggle
                  outline
                  size="sm"
                  color="info"
                  className={
                    "catlist-btn catlist-btn-toggle" +
                    (ItemsChecked ? " active" : "")
                  }
                  onClick={()=> this.checkAllLogInfoList(!ItemsChecked)}
              >
                All
              </ButtonToggle>{" "}
              <ButtonToggle
                  outline
                  size="sm"
                  color="info"
                  className={
                    "catlist-btn catlist-btn-toggle" + (showGenre ? " active" : "")
                  }
                  onClick={this.handleGenreToggle}
              >
                Genre
              </ButtonToggle>
            </div>
          </CardBody>
        </Card>
    );
  }
}

export default connect(
    (state) => ({
      logInfoList: state.viewList.get('logInfoList'),
      logInfoListCheckCnt: state.viewList.get('logInfoListCheckCnt'),
      genreList: state.genreList.get('genreList'),
    }),
    (dispatch) => ({
      viewListActions: bindActionCreators(viewListActions, dispatch),
      genreListActions: bindActionCreators(genreListActions, dispatch),
    })
)(CategoryList);