import React, {Component} from "react";
import {Button, ButtonToggle, Card, CardBody, Table} from "reactstrap";
import ReactTransitionGroup from "react-addons-css-transition-group";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBan, faChevronCircleDown, faDownload, faExclamationCircle} from "@fortawesome/free-solid-svg-icons";
import {faFileAlt} from "@fortawesome/free-regular-svg-icons";
import ScaleLoader from "react-spinners/ScaleLoader";
import {Select} from "antd";
import CheckBox from "../../Common/CheckBox";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as searchListActions from "../../../modules/searchList";
import * as dlHistoryAction from "../../../modules/dlHistory";
import * as API from "../../../api";
import {setRowsPerPage} from "../../../api";
import * as Define from '../../../define';
import services from "../../../services";
import {filePaginate, RenderPagination} from "../../Common/CommonFunction";
import ConfirmModal from "../../Common/ConfirmModal";
import AlertModal from "../../Common/AlertModal";
import _ from "lodash";

const { Option } = Select;

class FileList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      itemsChecked: true,
      pageSize: 10,
      currentPage: 1,
      sortDirection: "",
      sortKey: "",
      isError: Define.RSS_SUCCESS,
      isDownloadOpen: false,
      isProcessOpen: false,
      isCancelOpen: false,
      isCompleteOpen: false,
      isAlertOpen: false,
      modalMessage: "",
      searchComplatedDate: props.requestCompletedDate
    };
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    if(nextProps.requestCompletedDate != prevState.searchComplatedDate) {
      console.log("[getDerivedStateFromProps] init filelist state");
      return {
        itemsChecked: true,
        pageSize: 10,
        currentPage: 1,
        sortDirection: "",
        sortKey: "",
        isError: Define.RSS_SUCCESS,
        isDownloadOpen: false,
        isProcessOpen: false,
        isCancelOpen: false,
        isCompleteOpen: false,
        isAlertOpen: false,
        modalMessage: "",
        searchComplatedDate: nextProps.requestCompletedDate
      }
    }
    return prevState;
  }

  setErrorMsg = (errCode) => {
    const  msg = API.getErrorMsg(errCode);
    if (msg.toString().length > 0) {
      this.setState({
        modalMessage: msg
      });
      return true;
    }
    return false;
  };

  openDownloadModal = () => {
    if(this.props.downloadCnt <= 0) {
      this.setErrorStatus(Define.FILE_FAIL_NO_ITEM);
      this.setErrorMsg(Define.FILE_FAIL_NO_ITEM);
      this.openAlertModal();
    } else {
      this.setErrorStatus(Define.RSS_SUCCESS);
      this.setState({
        isDownloadOpen: true,
        modalMessage: "Do you want to download the selected file?"
      });
    }
  };

  closeDownloadModal = () => {
    this.setState({
      isDownloadOpen: false,
      modalMessage: ""
    });
  };

  openProcessModal = async () => {
    this.closeDownloadModal();

    setTimeout(() => {
      this.setState({
        isProcessOpen: true
      });
    }, 100);

    // Init
    const { searchListActions } = this.props;
    searchListActions.searchSetDlStatus({func:null, dlId: "", status: "init", totalFiles: 0, downloadFiles: 0, downloadUrl: ""})
    this.setErrorStatus(Define.RSS_SUCCESS);

    // Request Download
    const requestId = await API.requestDownload(this.props);
    console.log("requestId", requestId);
    searchListActions.searchSetDlStatus({dlId: requestId});

    // Add Modal Func to use in SetInterval
    const modalFunc = {
      closeProcessModal: this.closeProcessModal,
      openCompleteModal: this.openCompleteModal,
      closeCompleteModal: this.closeCompleteModal,
      setErrorMsg: this.setErrorMsg,
      openErrorModal: this.openAlertModal,
      getDownloadStatus: this.getDownloadStatus,
      setSearchListActions: this.setSearchListActions
    };

    // Request Download Status
    if(requestId !== "") {
      const intervalFunc = await API.setWatchDlStatus(requestId, modalFunc);
      searchListActions.searchSetDlStatus({func: intervalFunc});
    }
  };

  closeProcessModal = () => {
    this.setState({
      isProcessOpen: false
    });
  };

  openCancelModal = () => {
    this.setState({
      isCancelOpen: true,
      modalMessage: "Are you sure you want to cancel the download?"
    });
  };

  closeCancelModal = async (isCancel) => {
    if(isCancel) {
      const downloadStatus = this.props.downloadStatus;
      const { func, dlId } = downloadStatus.toJS();

      // Stop requesting status to SetInveral
      if(func !== null){
        clearInterval(func);
        // Reauest Cancel
        try {
          const res = await services.axiosAPI.requestDelete(Define.REST_FTP_DELETE_DOWNLOAD + "/" + dlId);
          //console.log("res", res)
        } catch (error) {
          console.error(error);
        }
      }

      const { searchListActions } = this.props;
      // Initialize state
      await searchListActions.searchSetDlStatus({func:null, dlId: "", status: "init", totalFiles: 0, downloadFiles: 0});
      this.setErrorStatus(Define.RSS_SUCCESS);
      this.closeProcessModal();
      setTimeout(() => {
        this.setState({
          isCancelOpen: false,
          modalMessage: ""
        });
      }, 100);
    } else {
      // setState is asynchronous, so it waits with await.
      await this.setState({
        isCancelOpen: false,
        modalMessage: ""
      });

      const downloadStatus = this.props.downloadStatus;
      const { status, totalFiles, downloadFiles} = downloadStatus.toJS();
      // If the download has already been completed, open openCompleteModal.
      //if(status === "done" && totalFiles === downloadFiles) {
      if(status === "done") {
        this.openCompleteModal();
      }
    }
  };

  openCompleteModal = () => {
    if(this.state.isCancelOpen !== true) {
      this.setState({
        isCompleteOpen: true,
        modalMessage: "Download Complete!"
      });
    }
  };

  closeCompleteModal = async (isSave) => {
    let result = Define.RSS_SUCCESS;
    this.setState({
      isCompleteOpen: false,
      modalMessage: ""
    });

    if(isSave) {
      const { downloadStatus } = this.props;
      let res = 0;
      console.log("downloadStatus.toJS().downloadUrl", downloadStatus.toJS().downloadUrl);
      res = await services.axiosAPI.downloadFile(downloadStatus.toJS().downloadUrl);
      console.log("res: ",res);
      if(res.result === Define.RSS_SUCCESS)
        await API.addDlHistory(Define.RSS_TYPE_FTP_MANUAL ,res.fileName, "Download Completed")
      else
        await API.addDlHistory(Define.RSS_TYPE_FTP_MANUAL ,res.fileName, "Download Fail");
      this.setErrorStatus(res.result);
    }
    else {
      await API.addDlHistory(Define.RSS_TYPE_FTP_MANUAL ,"unknown", "User Cancel");
    }
    // Initialize state
    const { searchListActions } = this.props;
    await searchListActions.searchSetDlStatus({func:null, dlId: "", status: "init", totalFiles: 0, downloadFiles: 0});
    this.setErrorStatus(Define.RSS_SUCCESS);
  };

  openAlertModal = () => {
    this.setState({
      isAlertOpen: true
    });
  };

  closeAlertModal = () => {
    this.setState({
      isAlertOpen: false,
      modalMessage: ""
    });
  };

  getDownloadStatus = () => {
    return this.props.downloadStatus.toJS();
  };

  setSearchListActions = (values) => {
    const { searchListActions }  = this.props;
    searchListActions.searchSetDlStatus({...values});
  };

  setErrorStatus = (error) => {
    this.setState({
      isError: error
    })
  };

  handlePageChange = page => {
    this.setState({
      currentPage: page
    });
  };

  onChangeRowsPerPage = (value) => {
    setRowsPerPage(this.props, value);

    const { pageSize, currentPage } = this.state;
    const startIndex = (currentPage - 1) * pageSize === 0 ? 1 : (currentPage - 1) * pageSize + 1;

    this.setState({
      pageSize: parseInt(value),
      currentPage: Math.ceil(startIndex / parseInt(value))
    });
  };

  checkFileItem = (e) => {
    if(e !== null && e !== undefined) {
      const idx = e.target.id.split('_{#div#}_')[1];
      if (idx !== null && idx !== undefined) {
        API.checkResponseList(this.props, idx);
      }
      e.stopPropagation();
    }
  };

  handleTrClick = e => {
    if(e !== null && e !== undefined) {
      const id = e.target.parentElement.getAttribute("cbinfo");
      if (id !== null && id !== undefined) {
        API.checkResponseList(this.props, id);
      }
      e.stopPropagation();
    }
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

    //console.log(name);

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
      sortDirection,
      isDownloadOpen,
      isProcessOpen,
      isCancelOpen,
      isCompleteOpen,
      isAlertOpen,
      modalMessage
    } = this.state;

    //console.log("responseList", responseList);

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


      // changed to use lodash
      /*
      const sortedList = responseList.sort((a, b) => {
        const preVal = a[tempKey].toLowerCase();
        const nextVal = b[tempKey].toLowerCase();

        if (tempDirection === "asc") {
          return preVal.localeCompare(nextVal, "en", { numeric: true });
        } else {
          return nextVal.localeCompare(preVal, "en", { numeric: true });
        }
      });
      */
      const sortedList = _.orderBy(responseList, tempKey, sortDirection);

      const files = filePaginate(sortedList, currentPage, pageSize);
      const { totalFiles, downloadFiles } = this.props.downloadStatus.toJS();

      return (
        <>
          <ConfirmModal isOpen={isDownloadOpen}
                        icon={faDownload}
                        message={modalMessage}
                        style={"secondary"}
                        leftBtn={"Yes"}
                        rightBtn={"No"}
                        actionBg={this.closeDownloadModal}
                        actionLeft={this.openProcessModal}
                        actionRight={this.closeDownloadModal}
          />
          {isProcessOpen ? (
              <ReactTransitionGroup
                  transitionName={"Custom-modal-anim"}
                  transitionEnterTimeout={200}
                  transitionLeaveTimeout={200}
              >
                <div className="Custom-modal-overlay" />
                <div className="Custom-modal">
                  <div className="content-without-title">
                    <div className="spinner-area">
                      <ScaleLoader
                          loading={true}
                          height={45}
                          width={16}
                          radius={30}
                          margin={5}
                      />
                    </div>
                    <p className="no-margin-no-padding">
                      Downloading...
                    </p>
                    {totalFiles > 0 && true &&
                    <p className="no-margin-no-padding">
                      ({downloadFiles}/{totalFiles})
                    </p>
                    }
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
          <ConfirmModal isOpen={isCancelOpen}
                        icon={faBan}
                        message={modalMessage}
                        style={"secondary"}
                        leftBtn={"Yes"}
                        rightBtn={"No"}
                        actionBg={null}
                        actionLeft={() => this.closeCancelModal(true)}
                        actionRight={() => this.closeCancelModal(false)}
          />
          <ConfirmModal isOpen={isCompleteOpen}
                        icon={faChevronCircleDown}
                        message={modalMessage}
                        leftBtn={"Save"}
                        rightBtn={"Cancel"}
                        style={"secondary"}
                        actionBg={null}
                        actionLeft={() => this.closeCompleteModal(true)}
                        actionRight={() => this.closeCompleteModal(false)}
          />
          <AlertModal isOpen={isAlertOpen} icon={faExclamationCircle} message={modalMessage} style={"secondary"} closer={this.closeAlertModal} />
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
                      <th onClick={() => this.handleThClick("fileSize")}>
                        <span className="sortLabel-root">
                          Size
                          <span className={this.sortIconRender("fileSize")}>➜</span>
                        </span>
                      </th>
                    </tr>
                  </thead>
                   <tbody>
                  {files.map((file, key) => {
                    const convFileDate = API.convertDateFormat(file.fileDate);
                    const convFileName = [];
                    if (file.fileName.indexOf("/") !== -1) {
                      const fileNameSplit = file.fileName.split("/");
                      for (let i = 0; i < fileNameSplit.length; i++) {
                        if (i === fileNameSplit.length - 1) {
                          convFileName.push(<><FontAwesomeIcon icon={faFileAlt}/>{" " + fileNameSplit[i]}</>);
                        } else {
                          convFileName.push(<>{fileNameSplit[i] + " / "}</>);
                        }
                      }
                    } else {
                      convFileName.push(<><FontAwesomeIcon icon={faFileAlt}/>{" " + file.fileName}</>);
                    }
                    return (
                        <tr
                            key={key}
                            onClick={(e) => this.handleTrClick(e)}
                            cbinfo={file.keyIndex}
                        >
                          <td>
                            <div className="custom-control custom-checkbox">
                              <CheckBox
                                  index={file.keyIndex}
                                  name={file.fileName}
                                  isChecked={file.checked}
                                  labelClass={"filelist-label"}
                                  handleCheckboxClick={this.checkFileItem}
                              />
                            </div>
                          </td>
                          <td>{file.targetName}</td>
                          <td>{file.logName}</td>
                          <td>{convFileName}</td>
                          <td>{convFileDate}</td>
                          <td>{API.bytesToSize(file.fileSize)}</td>
                        </tr>
                    );
                  })}
                  </tbody>
                </Table>
              </CardBody>
              <RenderPagination
                  pageSize={pageSize}
                  itemsCount={count}
                  onPageChange={this.handlePageChange}
                  currentPage={currentPage}
                  className={"custom-pagination"}
              />
              <div className="filelist-info-area">
                <label>{this.props.downloadCnt} File Selected</label>
              </div>
              <div className="filelist-item-area">
                <label>Rows per page:</label>
                <Select
                    defaultValue={pageSize}
                    onChange={this.onChangeRowsPerPage}
                    className="filelist"
                >
                  <Option value={10}>10</Option>
                  <Option value={30}>30</Option>
                  <Option value={50}>50</Option>
                  <Option value={100}>100</Option>
                </Select>
                <Button
                    outline
                    size="sm"
                    color="info"
                    className="filelist-btn"
                    onClick={this.openDownloadModal}
                >
                  Download
                </Button>
              </div>
            </Card>
          </div>
        </>
      );
    }
  }
}

export default connect(
    (state) => ({
      responseList: state.searchList.get('responseList'),
      responseListCnt: state.searchList.get('responseListCnt'),
      downloadCnt: state.searchList.get('downloadCnt'),
      downloadStatus: state.searchList.get('downloadStatus'),
      requestCompletedDate: state.searchList.get('requestCompletedDate'),
      resSuccess: state.pender.success['searchList/SEARCH_LOAD_RESPONSE_LIST'],
      resPending: state.pender.pending['searchList/SEARCH_LOAD_RESPONSE_LIST'],
      resError: state.pender.failure['searchList/SEARCH_LOAD_RESPONSE_LIST'],
    }),
    (dispatch) => ({
      searchListActions: bindActionCreators(searchListActions, dispatch),
      dlHistoryAction: bindActionCreators(dlHistoryAction, dispatch)
    })
)(FileList);
