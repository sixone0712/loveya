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
import * as Define from "../../../define";

class CategoryList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      ItemsChecked: false,
      showGenre: false,
      selectedGenre: "selectGenre",
      selectedKeyName: "selectGenre",
      genreName: "",
      isError: Define.RSS_SUCCESS
    };
  }

  setErrorStatus = (error) => {
    this.setState({
      ...this.state,
      isError: error
    })
  };

  handleGenreToggle = () => {
    this.setState({
      showGenre: !this.state.showGenre
    });
  };

  handleSelectBoxChange = async (dispName) => {
    console.log("handleSelectBoxChange");
    let genreName = dispName === "selectGenre" ? "" : dispName;
    API.selectGenreList(this.props, dispName);
    await this.setState({
        ...this.state,
        selectedGenre: dispName,
        selectedKeyName: dispName,
        genreName: genreName,

    });
    console.log("selectedGenre", this.state.selectedGenre);
    console.log("selectedKeyName", this.state.selectedKeyName);
  };

  onChangeGenreName = (genreName) => {
    this.setState({
      ...this.state,
      genreName: genreName
    });
  };

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

  addGenreList = async (dispName, keyName) => {
    console.log("###addGenreListCate Start");
    let result = await API.addGenreList(this.props, dispName, keyName);

    if(result === Define.RSS_SUCCESS) {
      console.log("=========");
      result = await API.setGenreList(this.props).then(result => {
        console.log("setGenreList/return");
        return result;
      });
    }
    console.log("addGenreList/return")
    console.log("###addGenreListCate End");
    return result;
  };


  editGenreList = async (dispName, keyName) => {
    let result =  await API.editGenreList(this.props, dispName, keyName);

    if(result === Define.RSS_SUCCESS) {
      console.log("=========");
      result = await API.setGenreList(this.props).then(result => {
        console.log("setGenreList/return");
        return result;
      });
    }
    console.log("editGenreList/return")
    console.log("###editGenreList End");
    return result;
  };

  deleteGenreList = async (keyName) => {
    console.log("###deleteGenreList Start");
    let result =  await API.deleteGenreList(this.props, keyName);
    console.log("deleteGenreList=>deleteGenreList=>result", result);

    if(result === Define.RSS_SUCCESS) {
      console.log("=========");
      result = await API.setGenreList(this.props).then(result => {
        console.log("deleteGenreList/return", result);
        return result;
      });
    }
    console.log("deleteGenreList/return");
    console.log("###deleteGenreList End");
    return result;
  };

  render() {
    const {
      showGenre,
      ItemsChecked,
      selectedKeyName
    } = this.state;

    const categorylist = API.getLogInfoList(this.props);
    const genreList = API.getGenreList(this.props);
    const genreCnt = API.getGenreCnt(this.props);
    console.log("categorylist", categorylist);
    console.log("genreList", genreList);
    console.log("genreCnt", genreCnt);

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
                      {/*<option value="0" disabled hidden>*/}
                      {/*  Select Genre*/}
                      {/*</option>*/}
                      {/*<option value="1">1</option>*/}
                      {/*<option value="2">2</option>*/}
                      {/*<option value="3">3</option>*/}
                      {/*<option value="4">4</option>*/}
                      {/*<option value="5">5</option>*/}
                    <option value="selectGenre" disabled hidden>
                      Select Genre
                    </option>
                      { genreCnt > 0 &&
                        genreList.map((list, idx) => <option key={idx+1} value={list.keyName}>{list.dispName}</option>)
                      }
                    </Input>

                    <InputModal
                        title={"Create Genre"}
                        openbtn={"Create"}
                        inputname={"genName"}
                        inputpholder={"Enter Genre Name"}
                        leftbtn={"Create"}
                        rightbtn={"Cancel"}
                        inputValue={this.state.selectedKeyName}
                        confirmFunc={this.addGenreList}
                        selectedKeyName={selectedKeyName}
                        logInfoListCheckCnt={this.props.logInfoListCheckCnt}
                        handleSelectBoxChange={this.handleSelectBoxChange}
                        genreName={this.state.genreName}
                        onChangeGenreName={this.onChangeGenreName}
                        isError={this.state.isError}
                        setErrorStatus={this.setErrorStatus}
                    />
                    <InputModal
                        title={"Edit Genre"}
                        openbtn={"Edit"}
                        inputname={"genName"}
                        inputpholder={"Edit Genre Name"}
                        leftbtn={"Edit"}
                        rightbtn={"Cancel"}
                        inputValue={this.state.selectedKeyName}
                        confirmFunc={this.editGenreList}
                        selectedKeyName={selectedKeyName}
                        logInfoListCheckCnt={this.props.logInfoListCheckCnt}
                        handleSelectBoxChange={this.handleSelectBoxChange}
                        genreName={this.state.genreName}
                        onChangeGenreName={this.onChangeGenreName}
                        isError={this.state.isError}
                        setErrorStatus={this.setErrorStatus}
                    />
                    <ConfirmModal
                        openbtn={"Delete"}
                        message={"Do you want to delete the selected genre?"}
                        leftbtn={"Delete"}
                        rightbtn={"Cancel"}
                        confirmFunc={this.deleteGenreList}
                        selectedKeyName={selectedKeyName}
                        handleSelectBoxChange={this.handleSelectBoxChange}
                        isError={this.state.isError}
                        setErrorStatus={this.setErrorStatus}
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
      genreCnt: state.genreList.get('genreCnt'),
    }),
    (dispatch) => ({
      // bindActionCreators 는 액션함수들을 자동으로 바인딩해줍니다.
      viewListActions: bindActionCreators(viewListActions, dispatch),
      genreListActions: bindActionCreators(genreListActions, dispatch),
    })
)(CategoryList);