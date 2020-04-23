import React, { Component } from "react";
import { Card, CardBody, Table, ButtonToggle, Button } from "reactstrap";
import PaginationComponent from "react-reactstrap-pagination";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faExclamationCircle } from "@fortawesome/free-solid-svg-icons";
import { faFileAlt } from "@fortawesome/free-regular-svg-icons";
import Select from "react-select";
import _ from "lodash";
import CheckBox from "../../Common/CheckBox";
import DownloadConfirmModal from "./DownloadModal"
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as searchListActions from "../../../modules/searchList";
import * as API from "../../../api";
import {setRowsPerPage} from "../../../api";
import * as Define from '../../../define';

const customSelectStyles = {
  container: styles => ({
    ...styles,
    width: "85px",
    fontSize: "14px",
    marginLeft: "10px",
    marginRight: "20px"
  }),
  option: (styles, { isFocused, isSelected }) => {
    return {
      ...styles,
      backgroundColor: isSelected
        ? "rgba(133, 164, 179, 0.5)"
        : isFocused
        ? "rgba(133, 164, 179, 0.3)"
        : null,
      color: "black",
      ":active": {
        ...styles[":active"],
        backgroundColor: isSelected
          ? "rgba(133, 164, 179, 0.9)"
          : isFocused
          ? "rgba(133, 164, 179, 0.7)"
          : null
      }
    };
  },
  control: () => ({
    display: "flex",
    border: "1px solid #85a4b3",
    borderRadius: "3px",
    caretColor: "transparent",
    transition: "color .15s ease-in-out, background-color .15s ease-in-out, border-color .15s ease-in-out, box-shadow .15s ease-in-out",
    ":hover": {
      outline: "0",
      boxShadow: "0 0 0 0.2em rgba(133, 164, 179, 0.5)"
    }
  }),
  dropdownIndicator: styles => ({
    ...styles,
    color: "rgba(133, 164, 179, 0.6)",
    ":hover": {
      ...styles[":hover"],
      color: "rgba(133, 164, 179, 1)"
    }
  }),
  indicatorSeparator: styles => ({
    ...styles,
    backgroundColor: "rgba(133, 164, 179, 0.6)"
  }),
  menu: styles => ({
    ...styles,
    borderRadius: "3px",
    boxShadow: "0 0 0 1px rgba(133, 164, 179, 0.6), 0 4px 11px rgba(133, 164, 179, 0.6)"
  })
};

const optionList = [
  { value: 10, label: "10" },
  { value: 30, label: "30" },
  { value: 50, label: "50" },
  { value: 100, label: "100" }
];

class FileList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      itemsChecked: true,
      pageSize: this.props.responsePerPage,
      currentPage: 1,
      sortDirection: "",
      sortKey: "",
      isError: Define.RSS_SUCCESS
    };
  }

  setErrorStatus = (error) => {
    this.setState({
      ...this.state,
      isError: error
    })
  };

  handlePageChange = page => {
    this.setState({
      ...this.state,
      currentPage: page
    });
  };

  onChangeRowsPerPage = (option) => {
    setRowsPerPage(this.props, option.value);
    this.setState(
        {
          ...this.state,
          pageSize: parseInt(option.value),
          currentPage: 1
        }
    )
  };

  checkFileItem = (e) => {
    const idx = e.target.id.split('_')[1];
    API.checkResponseList(this.props, idx);
  };

  handleTrClick = e => {
    const id = e.target.parentElement.getAttribute("cbinfo");
    API.checkResponseList(this.props, id);
    e.stopPropagation();
  };

  checkAllFileItem = (checked) => {
    this.setState({
      ...this.state,
      itemsChecked: checked
    });

    console.log(this.state.i);
    API.checkAllResponseList(this.props, checked);
  };

  handleThClick = key => {
    const { sortKey, sortDirection } = this.state;
    let changeDirection = "asc";

    if (sortKey === key && sortDirection === "asc") {
      changeDirection = "desc";
    }

    this.setState({
      sortDirection: changeDirection,
      sortKey: key,
    });
  };

  sortIconRender = name => {
    const { sortKey, sortDirection } = this.state;
    const style = "sort-icon";

    console.log(name);

    return sortKey === name ? style + " sort-active " + sortDirection : style;
  };

  render() {
    const responseList = API.getResponseList(this.props);
    const count = API.getResponseListCnt(this.props);
    const {
      itemsChecked,
      pageSize,
      currentPage,
      isError,
      sortKey,
      sortDirection
    } = this.state;

    console.log("responseList", responseList);
    console.log("responseListSort", responseList.sort((a, b) => { // 오름차순
      return a.fileName < b.fileName ? -1 : a.fileName > b.fileName ? 1 : 0;
    }));

    if (count === 0 || this.props.resError || this.props.resPending) {
      return (
        <div className="filelist-container">
          <Card className="ribbon-wrapper filelist-card">
            <CardBody className=".filelist-card-body">
              <div className="ribbon ribbon-clip ribbon-info">File</div>
              <div className="filelist-no-search">
                <p>
                  <FontAwesomeIcon icon={faExclamationCircle} size="7x" />
                </p>
                <p>Logs not found.</p>
              </div>
            </CardBody>
          </Card>
        </div>
      );
    } else {
      const tempKey = sortKey === "" ? "targetName" : sortKey;
      const tempDirection = sortDirection === "" ? "asc" : sortDirection;

      const sortedList = responseList.sort((a, b) => {
        const preVal = a[tempKey].toLowerCase();
        const nextVal = b[tempKey].toLowerCase();

        if (tempDirection === "asc") {
          return preVal.localeCompare(nextVal, "en", { numeric: true });
        } else {
          return nextVal.localeCompare(preVal, "en", { numeric: true });
        }
      });
      const files = filePaginate(sortedList, currentPage, pageSize);
      return (
        <div className="filelist-container">
          <Card className="ribbon-wrapper filelist-card">
            <CardBody className="filelist-card-body">
              <div className="ribbon ribbon-clip ribbon-info">File</div>
              <Table>
                <thead>
                  <tr>
                    <th>
                      <div>
                        <ButtonToggle
                          outline
                          size="sm"
                          color="info"
                          className={
                            "filelist-btn filelist-btn-toggle" +
                            (itemsChecked ? " active" : "")
                          }
                          onClick={()=> this.checkAllFileItem(!itemsChecked)}
                        >
                          All
                        </ButtonToggle>
                      </div>
                    </th>
                    <th onClick={() => this.handleThClick("targetName")}>
                      <span className="sortLabel-root">
                        Machine
                        <span className={this.sortIconRender("targetName")}>➜</span>
                      </span>
                    </th>
                    <th onClick={() => this.handleThClick("logName")}>
                      <span className="sortLabel-root">
                        Category
                        <span className={this.sortIconRender("logName")}>➜</span>
                      </span>
                    </th>
                    <th onClick={() => this.handleThClick("fileName")}>
                      <span className="sortLabel-root">
                        File Name
                        <span className={this.sortIconRender("fileName")}>➜</span>
                      </span>
                    </th>
                    <th onClick={() => this.handleThClick("fileDate")}>
                      <span className="sortLabel-root">
                        Date
                        <span className={this.sortIconRender("fileDate")}>➜</span>
                      </span>
                    </th>
                    <th onClick={() => this.handleThClick("sizeKB")}>
                      <span className="sortLabel-root">
                        Size
                        <span className={this.sortIconRender("sizeKB")}>➜</span>
                      </span>
                    </th>
                  </tr>
                </thead>
                 <tbody>
                {files.map((file, key) => {
                  const convFileDate = API.convertDateFormat(file.fileDate);
                  return (
                      <tr
                          key={key}
                          onClick={(e) => this.handleTrClick(e)}
                          cbinfo={file.keyIndex}
                      >
                        <td>
                          <CheckBox
                              key={key}
                              index={file.keyIndex}
                              name={file.fileName}
                              isChecked={file.checked}
                              handleCheckboxClick={this.checkFileItem}
                              labelClass="filelist-label"
                          />
                        </td>
                        <td>{file.targetName}</td>
                        <td>{file.logName}</td>
                        <td>
                          <FontAwesomeIcon icon={faFileAlt} />{" "}
                          {file.fileName}
                        </td>
                        <td>{convFileDate}</td>
                        <td>{file.sizeKB}</td>
                      </tr>
                  );
                })}
                </tbody>
              </Table>
            </CardBody>
            <FilePagination
              pageSize={pageSize}
              itemsCount={count}
              onPageChange={this.handlePageChange}
            />
            <div className="filelist-info-area">
              <label>{this.props.downloadCnt} File Selected</label>
            </div>
            <div className="filelist-item-area">
              <label>Rows per page:</label>
              <Select
                  onChange={this.onChangeRowsPerPage}
                  options={optionList}
                  styles={customSelectStyles}
                  defaultValue={optionList[0]}
              />
              <DownloadConfirmModal
                openbtn={"Download"}
                message={"Do you want to download the selected file?"}
                leftbtn={"Download"}
                rightbtn={"Cancel"}
                isError={isError}
                setErrorStatus={this.setErrorStatus}
              />
            </div>
          </Card>
        </div>
      );
    }
  }
}

function filePaginate(items, pageNumber, pageSize) {
  const startIndex = (pageNumber - 1) * pageSize;

  return _(items)
    .slice(startIndex)
    .take(pageSize)
    .value();
}

const FilePagination = props => {
  const { itemsCount, pageSize, onPageChange } = props;
  const pageCount = Math.ceil(itemsCount / pageSize);

  if (pageCount === 1) {
    return null;
  }

  return (
    <div className="filelist-pagination">
      <PaginationComponent
        totalItems={itemsCount}
        pageSize={pageSize}
        onSelect={onPageChange}
        maxPaginationNumbers={10}
        firstPageText={"«"}
        previousPageText={"‹"}
        nextPageText={"›"}
        lastPageText={"»"}
      />
    </div>
  );
};

export default connect(
    (state) => ({
      responseList: state.searchList.get('responseList'),
      responseListCnt: state.searchList.get('responseListCnt'),
      downloadCnt: state.searchList.get('downloadCnt'),
      responsePerPage: state.searchList.get('responsePerPage'),
      resSuccess: state.pender.success['searchList/SEARCH_LOAD_RESPONSE_LIST'],
      resPending: state.pender.pending['searchList/SEARCH_LOAD_RESPONSE_LIST'],
      resError: state.pender.failure['searchList/SEARCH_LOAD_RESPONSE_LIST'],
    }),
    (dispatch) => ({
      searchListActions: bindActionCreators(searchListActions, dispatch)
    })
)(FileList);
