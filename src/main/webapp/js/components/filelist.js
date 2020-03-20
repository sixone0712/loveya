import React, { Component } from "react";
import { Card, CardBody, Table, ButtonToggle, Button, Input } from "reactstrap";
import PaginationComponent from "react-reactstrap-pagination";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faExclamationCircle,
  faDownload,
  faBan,
  faChevronCircleDown
} from "@fortawesome/free-solid-svg-icons";
import ReactTransitionGroup from "react-addons-css-transition-group";
import ScaleLoader from "react-spinners/ScaleLoader";
import _ from "lodash";
import Checkbox from "./checkbox";

const tableStyle = {
  boxShadow: "0 2px 5px 0 rgba(0,0,0,.16), 0 2px 10px 0 rgba(0,0,0,.12)",
  marginTop: ".5rem",
  marginBottom: ".5rem"
};

const cardStyle = {
  margin: "10px 0px",
  boxShadow: "0 1px 11px 0 rgba(0,0,0,.1)"
};

const divStyle = {
  paddingLeft: "10px",
  paddingRight: "10px"
};

const theadSelect = {
  width: "5%",
  textAlign: "center"
};

const checkStyle = {
  paddingLeft: "0.9rem"
};

const paginationStyle = {
  marginLeft: "auto",
  marginRight: "auto",
  fontSize: "14px"
};

const buttonPosition = {
  position: "absolute",
  top: "17px",
  right: "20px",
  display: "flex"
};

const selectStyle = {
  fontSize: "14px",
  marginLeft: "10px",
  marginRight: "20px"
};

const labelStyle = {
  top: "6px",
  position: "relative",
  whiteSpace: "nowrap",
  fontSize: "14px",
  fontWeight: "300"
};

const spinnerStyle = {
  display: "flex",
  alignItems: "center",
  flexDirection: "colunm",
  justifyContent: "center",
  padding: "16px"
};

class RSSfilelist extends Component {
  constructor(props) {
    super(props);
    this.state = {
      fileList: [
        {
          id: "file1",
          mname: "Machine 1",
          cname: "Category 1",
          fname: "errorhistory.log",
          date: "2020/03/10 14:24",
          fsize: "7KB"
        },
        {
          id: "file2",
          mname: "Machine 1",
          cname: "Category 1",
          fname: "errorhistory.log",
          date: "2020/03/10 14:24",
          fsize: "7KB"
        },
        {
          id: "file3",
          mname: "Machine 1",
          cname: "Category 1",
          fname: "errorhistory.log",
          date: "2020/03/10 14:24",
          fsize: "7KB"
        },
        {
          id: "file4",
          mname: "Machine 1",
          cname: "Category 1",
          fname: "errorhistory.log",
          date: "2020/03/10 14:24",
          fsize: "7KB"
        },
        {
          id: "file5",
          mname: "Machine 1",
          cname: "Category 1",
          fname: "errorhistory.log",
          date: "2020/03/10 14:24",
          fsize: "7KB"
        },
        {
          id: "file6",
          mname: "Machine 1",
          cname: "Category 1",
          fname: "errorhistory.log",
          date: "2020/03/10 14:24",
          fsize: "7KB"
        },
        {
          id: "file7",
          mname: "Machine 1",
          cname: "Category 1",
          fname: "errorhistory.log",
          date: "2020/03/10 14:24",
          fsize: "7KB"
        },
        {
          id: "file8",
          mname: "Machine 1",
          cname: "Category 1",
          fname: "errorhistory.log",
          date: "2020/03/10 14:24",
          fsize: "7KB"
        },
        {
          id: "file9",
          mname: "Machine 1",
          cname: "Category 1",
          fname: "errorhistory.log",
          date: "2020/03/10 14:24",
          fsize: "7KB"
        },
        {
          id: "file10",
          mname: "Machine 1",
          cname: "Category 1",
          fname: "errorhistory.log",
          date: "2020/03/10 14:24",
          fsize: "7KB"
        },
        {
          id: "file11",
          mname: "Machine 1",
          cname: "Category 1",
          fname: "errorhistory.log",
          date: "2020/03/10 14:24",
          fsize: "7KB"
        },
        {
          id: "file12",
          mname: "Machine 1",
          cname: "Category 1",
          fname: "errorhistory.log",
          date: "2020/03/10 14:24",
          fsize: "7KB"
        },
        {
          id: "file13",
          mname: "Machine 1",
          cname: "Category 1",
          fname: "errorhistory.log",
          date: "2020/03/10 14:24",
          fsize: "7KB"
        },
        {
          id: "file14",
          mname: "Machine 14",
          cname: "Category 14",
          fname: "errorhistory.log",
          date: "2020/03/10 14:24",
          fsize: "7KB"
        }
      ],
      checkedList: [],
      itemsChecked: false,
      pageSize: 10,
      currentPage: 1
    };
  }

  selectItem = () => {
    const { itemsChecked, fileList } = this.state;
    const collection = [];

    if (!itemsChecked) {
      for (const file of fileList) {
        collection.push(file.id);
      }
    }

    this.setState({
      checkedList: collection,
      itemsChecked: !itemsChecked
    });
  };

  handleCheckboxClick = e => {
    const { id, checked } = e.target;

    if (checked) {
      this.setState(prevState => ({
        checkedList: [...prevState.checkedList, id]
      }));
    } else {
      this.setState(prevState => ({
        checkedList: prevState.checkedList.filter(item => item !== id)
      }));
    }
  };

  handlePageChange = page => {
    this.setState({
      currentPage: page
    });
  };

  render() {
    const { length: count } = this.state.fileList;
    const {
      fileList: allFileList,
      checkedList,
      itemsChecked,
      pageSize,
      currentPage
    } = this.state;

    if (count === 0) {
      return (
        <div style={divStyle}>
          <Card className="ribbon-wrapper" style={cardStyle}>
            <CardBody className="card-body-filelist">
              <div className="ribbon ribbon-clip ribbon-info">File</div>
              <div style={{ textAlign: "center" }}>
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
      const files = filePaginate(allFileList, currentPage, pageSize);
      return (
        <div style={divStyle}>
          <Card className="ribbon-wrapper" style={cardStyle}>
            <CardBody className="card-body-filelist">
              <div className="ribbon ribbon-clip ribbon-info">File</div>
              <Table style={tableStyle}>
                <thead>
                  <tr>
                    <th style={theadSelect}>
                      <div>
                        <ButtonToggle
                          outline
                          size="sm"
                          color="info"
                          className={
                            "filelist-btn filelist-btn-toggle" +
                            (itemsChecked ? " active" : "")
                          }
                          onClick={this.selectItem.bind(this)}
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
                  {files.map((file, index) => {
                    return (
                      <tr key={index}>
                        <td style={checkStyle}>
                          <Checkbox
                            item={file}
                            isChecked={checkedList.includes(file.id)}
                            handleCheckboxClick={this.handleCheckboxClick}
                            labelClass="filelist-label"
                          />
                        </td>
                        <td>{file.mname}</td>
                        <td>{file.cname}</td>
                        <td>{file.fname}</td>
                        <td>{file.date}</td>
                        <td>{file.fsize}</td>
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
            <div style={buttonPosition}>
              <label style={labelStyle}>Rows per page:</label>
              <Input
                type="select"
                name="dispSize"
                id="dispSize"
                style={selectStyle}
                className="filelist-select"
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
    <div style={paginationStyle}>
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

class DownloadConfirmModal extends Component {
  constructor(props) {
    super(props);
    const { openbtn, message, leftbtn, rightbtn } = this.props;
    this.openParentModal = this.openParentModal.bind(this);
    this.closeParentModal = this.closeParentModal.bind(this);
    this.openProcessModal = this.openProcessModal.bind(this);
    this.closeProcessModal = this.closeProcessModal.bind(this);
    this.openCancelModal = this.openCancelModal.bind(this);
    this.closeCancelModal = this.closeCancelModal.bind(this);
    this.openCompleteModal = this.openCompleteModal.bind(this);
    this.closeCompleteModal = this.closeCompleteModal.bind(this);
    this.state = {
      openbtn,
      message,
      leftbtn,
      rightbtn,
      parentModalOpen: false,
      processModalOpen: false,
      cancelModalOpen: false,
      completeModalOpen: false
    };
  }

  openParentModal = () => {
    this.setState({
      parentModalOpen: true
    });
  };

  closeParentModal = () => {
    this.setState({
      parentModalOpen: false
    });
  };

  openProcessModal = () => {
    this.setState({
      processModalOpen: true
    });
  };

  closeProcessModal = () => {
    this.setState({
      processModalOpen: false
    });
  };

  openCancelModal = () => {
    this.setState({
      cancelModalOpen: true
    });
  };

  closeCancelModal = () => {
    this.setState({
      cancelModalOpen: false,
      processModalOpen: false,
      parentModalOpen: false
    });
  };

  openCompleteModal = () => {
    this.setState({
      completeModalOpen: true
    });
  };

  closeCompleteModal = () => {
    this.setState({
      completeModalOpen: false,
      cancelModalOpen: false,
      processModalOpen: false,
      parentModalOpen: false
    });
  };

  render() {
    const {
      openbtn,
      message,
      leftbtn,
      rightbtn,
      parentModalOpen,
      processModalOpen,
      cancelModalOpen,
      completeModalOpen
    } = this.state;
    return (
      <>
        <Button
          outline
          size="sm"
          color="info"
          className="filelist-btn"
          onClick={this.openParentModal}
        >
          {openbtn}
        </Button>
        {parentModalOpen ? (
          <ReactTransitionGroup
            transitionName={"Custom-modal-anim"}
            transitionEnterTimeout={200}
            transitionLeaveTimeout={200}
          >
            <div
              className="Custom-modal-overlay"
              onClick={this.closeParentModal}
            />
            <div className="Custom-modal">
              <div className="content-without-title">
                <p>
                  <FontAwesomeIcon icon={faDownload} size="6x" />
                </p>
                <p>{message}</p>
              </div>
              <div className="button-wrap">
                <button
                  className="secondary form-type left-btn"
                  onClick={this.openProcessModal}
                >
                  {leftbtn}
                </button>
                <button
                  className="secondary form-type right-btn"
                  onClick={this.closeParentModal}
                >
                  {rightbtn}
                </button>
              </div>
            </div>
          </ReactTransitionGroup>
        ) : (
          <ReactTransitionGroup
            transitionName={"Custom-modal-anim"}
            transitionEnterTimeout={200}
            transitionLeaveTimeout={200}
          />
        )}
        {processModalOpen ? (
          <ReactTransitionGroup
            transitionName={"Custom-modal-anim"}
            transitionEnterTimeout={200}
            transitionLeaveTimeout={200}
          >
            <div className="Custom-modal-overlay child-overlay" />
            <div className="Custom-modal">
              <div className="content-without-title">
                <div style={spinnerStyle}>
                  <ScaleLoader
                    loading={true}
                    height={45}
                    width={16}
                    radius={30}
                    margin={5}
                  />
                </div>
                <p>
                  Downloading...
                  <br />
                  (10/100)
                </p>
              </div>
              <div className="button-wrap">
                <button
                  className="secondary alert-type"
                  onClick={this.openCancelModal}
                >
                  Cancel
                </button>
              </div>
            </div>
          </ReactTransitionGroup>
        ) : (
          <ReactTransitionGroup
            transitionName={"Custom-modal-anim"}
            transitionEnterTimeout={200}
            transitionLeaveTimeout={200}
          />
        )}
        {cancelModalOpen ? (
          <ReactTransitionGroup
            transitionName={"Custom-modal-anim"}
            transitionEnterTimeout={200}
            transitionLeaveTimeout={200}
          >
            <div className="Custom-modal-overlay child-overlay" />
            <div className="Custom-modal">
              <div className="content-without-title">
                <p>
                  <FontAwesomeIcon icon={faBan} size="6x" />
                </p>
                <p>Are you sure you want to cancel the download?</p>
              </div>
              <div className="button-wrap">
                <button
                  className="secondary form-type left-btn"
                  onClick={this.closeCancelModal}
                >
                  Yes
                </button>
                <button
                  className="secondary form-type right-btn"
                  onClick={this.openCompleteModal}
                >
                  No
                </button>
              </div>
            </div>
          </ReactTransitionGroup>
        ) : (
          <ReactTransitionGroup
            transitionName={"Custom-modal-anim"}
            transitionEnterTimeout={200}
            transitionLeaveTimeout={200}
          />
        )}
        {completeModalOpen ? (
          <ReactTransitionGroup
            transitionName={"Custom-modal-anim"}
            transitionEnterTimeout={200}
            transitionLeaveTimeout={200}
          >
            <div className="Custom-modal-overlay child-overlay" />
            <div className="Custom-modal">
              <div className="content-without-title">
                <p>
                  <FontAwesomeIcon icon={faChevronCircleDown} size="6x" />
                </p>
                <p>Download Complete!</p>
              </div>
              <div className="button-wrap">
                <button
                  className="secondary form-type left-btn"
                  onClick={this.closeCompleteModal}
                >
                  Save
                </button>
                <button
                  className="secondary form-type right-btn"
                  onClick={this.closeCompleteModal}
                >
                  Cancel
                </button>
              </div>
            </div>
          </ReactTransitionGroup>
        ) : (
          <ReactTransitionGroup
            transitionName={"Custom-modal-anim"}
            transitionEnterTimeout={200}
            transitionLeaveTimeout={200}
          />
        )}
      </>
    );
  }
}

export default RSSfilelist;
