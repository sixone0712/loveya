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
import CheckBox from "../../Common/CheckBox";
import InputModal from "./InputModal";
import ConfirmModal from "./ConfirmModal";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../../modules/viewList";
import * as genreListActions from "../../../modules/genreList";
import * as API from '../../../api'

class CategoryList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      nowAction: "",
      ItemsChecked: false,
      showGenre: false,
      selectedGenre: 0,
      selectedGenreName: "",
    };
  }

  handleGenreToggle = () => {
    this.setState({
      showGenre: !this.state.showGenre
    });
  };

  handleSelectBoxChange = async (id) => {
    console.log("handleSelectBoxChange");
    await API.selectGenreList(this.props, id);
    const genreList = await API.getGenreList(this.props).list;
    let name = "";
    if(id !== 0) {
      const findGenre = genreList.find(item => item.id == id);
      name = findGenre.name;
    }
    console.log("name", name);

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
    const idx = e.target.id.split('_')[1];
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

  setNowAction = async (name) => {
    await this.setState({
      ...this.state,
      nowAction: name
    })
  };

  render() {
    const {
      showGenre,
      ItemsChecked,
    } = this.state;

    const categorylist = API.getLogInfoList(this.props);
    const genreList = API.getGenreList(this.props);
    console.log("chpark_genreList", genreList);

    return (
        <Card className="ribbon-wrapper catlist-custom">
          <CardBody className="custom-scrollbar card-body-custom card-body-catlist">
            <div className="ribbon ribbon-clip ribbon-secondary">
              File Category
            </div>
            <Col>
              <FormGroup className="catlist-form-group">
                <Collapse isOpen={showGenre}>
                  <div className="catlist-genre-area">
                    <Input
                        type="select"
                        name="genreSel"
                        id="genreSel"
                        className="catlist-select"
                        value={this.state.selectedGenre}
                        onChange={(e) => this.handleSelectBoxChange(e.target.value)}
                    >
                    <option key={0} value={0} disabled hidden>
                      Select Genre
                    </option>
                      { genreList.totalCnt > 0 &&
                        genreList.list.map((list, idx) => <option key={idx+1} value={list.id}>{list.name}</option>)
                      }
                    </Input>

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
                        handleSelectBoxChange={this.handleSelectBoxChange}
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
                        handleSelectBoxChange={this.handleSelectBoxChange}
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
                        handleSelectBoxChange={this.handleSelectBoxChange}
                        getSelectedIdByName={this.getSelectedIdByName}
                    />
                  </div>
                </Collapse>
                {categorylist.map((cat, key) => {
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
                })}
              </FormGroup>
            </Col>
            <div className="manual-btn-area">
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