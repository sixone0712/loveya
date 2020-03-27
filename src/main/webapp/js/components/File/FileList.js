import React, { Component } from "react";
import { Card, CardBody, Table, ButtonToggle, Input } from "reactstrap";
import PaginationComponent from "react-reactstrap-pagination";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faExclamationCircle } from "@fortawesome/free-solid-svg-icons";
import _ from "lodash";
import CheckBox from "../Common/CheckBox";
import DownloadConfirmModal from "./DownloadModal"
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as searchListActions from "../../modules/searchList";
import * as API from "../../api";
import {setRowsPerPage} from "../../api";
import * as Define from '../../define';

class FileList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      itemsChecked: true,
      pageSize: this.props.responsePerPage,
      currentPage: 1,
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

  onChangeRowsPerPage = (e) => {
    setRowsPerPage(this.props, e.target.value);
    this.setState(
        {
          ...this.state,
          pageSize: parseInt(e.target.value),
          currentPage: 1
        }
    )
  };

  checkFileItem = (e) => {
    const idx = e.target.id.split('_')[1];
    API.checkResponseList(this.props, idx);
  };

  checkAllFileItem = (checked) => {
    this.setState({
      ...this.state,
      itemsChecked: checked
    });

    console.log(this.state.i);
    API.checkAllResponseList(this.props, checked);
  };

  render() {
    const responseList = API.getResponseList(this.props);
    const count = API.getResponseListCnt(this.props);
    const {
      itemsChecked,
      pageSize,
      currentPage,
      isError
    } = this.state;

    console.log("responseList", responseList);
    console.log("responseListSort", responseList.sort((a, b) => { // 오름차순
      return a.fileName < b.fileName ? -1 : a.fileName > b.fileName ? 1 : 0;
    }));

    if (count === 0 || this.props.resError || this.props.resPending) {
      return (
        <div className="filelist-container">
          <Card className="ribbon-wrapper card-filelist">
            <CardBody className="card-body-filelist">
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
      const files = filePaginate(responseList, currentPage, pageSize);
      return (
        <div className="filelist-container">
          <Card className="ribbon-wrapper card-filelist">
            <CardBody className="card-body-filelist">
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
                    <th>Machine</th>
                    <th>Category</th>
                    <th>File Name</th>
                    <th>Date</th>
                    <th>Size</th>
                  </tr>
                </thead>
                 <tbody>
                {files.map((file, key) => {
                  const convFileDate = API.convertDateFormat(file.fileDate);
                  return (
                      <tr key={key}>
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
                        <td>{file.fileName}</td>
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
              <Input
                type="select"
                name="dispSize"
                id="dispSize"
                className="filelist-select"
                onChange={this.onChangeRowsPerPage}
              >
                <option value="10">10</option>
                <option value="30">30</option>
                <option value="50">50</option>
                <option value="100">100</option>
              </Input>
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
