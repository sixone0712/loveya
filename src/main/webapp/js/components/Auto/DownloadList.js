import React, {Component} from "react";
import {Button, Card, CardBody, CardHeader, Col, Table} from "reactstrap";
import {Select} from "antd";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheck, faDownload, faExclamation, faExclamationCircle, faTrashAlt} from "@fortawesome/free-solid-svg-icons";
import {filePaginate, RenderPagination} from "../Common/CommonFunction";
import ConfirmModal from "../Common/ConfirmModal";
import AlertModal from "../Common/AlertModal";
import services from "../../services"
import queryString from "query-string";
import * as Define from "../../define";
import * as API from "../../api";

const { Option } = Select;

const modalMessage = {
    MODAL_DELETE_MESSAGE: "Are you sure you want to delete the selected file?",
    MODAL_DOWNLOAD_MESSAGE_1:
        "Do you want to download a file of the selected request ID?",
    MODAL_DOWNLOAD_MESSAGE_2: "Do you want to download a new file?",
    MODAL_ALERT_MESSAGE: "No new files to download.",
    MODAL_NETWORK_ERROR: "Network Error.",
    MODAL_FILE_NOT_FOUND: "File not found."
};

export const statusType = {
    STATUS_NEW: "new",
    STATUS_FINISHED: "finished"
};

export const modalType = {
    MODAL_DELETE: 1,
    MODAL_DOWNLOAD_1: 2,
    MODAL_DOWNLOAD_2: 3,
    MODAL_ALERT: 4,
    MODAL_NETWORK_ERROR: 5,
    MODAL_FILE_NOT_FOUND: 6
};

class RSSAutoDownloadList extends Component {
    state = {
        requestName: "",
        requestId: "",
        requestList: [],
        download: {},
        delete: {},
        pageSize: 10,
        currentPage: 1,
        isDeleteOpen: false,
        isSelectDownloadOpen: false,
        isNewDownloadOpen: false,
        isAlertOpen: false,
        modalMessage: ""
    };

    openModal = async type => {
        switch (type) {
            case modalType.MODAL_DELETE:
                await this.setState({
                    isDeleteOpen: true,
                    modalMessage: modalMessage.MODAL_DELETE_MESSAGE
                });
                break;

            case modalType.MODAL_DOWNLOAD_1:
                await this.setState({
                    isSelectDownloadOpen: true,
                    modalMessage: modalMessage.MODAL_DOWNLOAD_MESSAGE_1
                });
                break;

            case modalType.MODAL_DOWNLOAD_2:
                await this.setState({
                    isNewDownloadOpen: true,
                    modalMessage: modalMessage.MODAL_DOWNLOAD_MESSAGE_2
                });
                break;

            case modalType.MODAL_ALERT:
                await this.setState({
                    isAlertOpen: true,
                    modalMessage: modalMessage.MODAL_ALERT_MESSAGE
                });
                break;

            case modalType.MODAL_NETWORK_ERROR:
                await this.setState({
                    isAlertOpen: true,
                    modalMessage: modalMessage.MODAL_NETWORK_ERROR
                });
                break;

            case modalType.MODAL_FILE_NOT_FOUND:
                await this.setState({
                    isAlertOpen: true,
                    modalMessage: modalMessage.MODAL_FILE_NOT_FOUND
                });
                break;

            default:
                console.log("type error!!");
                break;
        }
    };

    closeModal = () => {
        this.setState({
            isDeleteOpen: false,
            isSelectDownloadOpen: false,
            isNewDownloadOpen: false,
            isAlertOpen: false,
            modalMessage: ""
        });
    };

    handlePaginationChange = page => {
        this.setState({
            currentPage: page
        });
    };

    handleSelectBoxChange = value => {
        const { pageSize, currentPage } = this.state;
        const startIndex = (currentPage - 1) * pageSize === 0 ? 1 : (currentPage - 1) * pageSize + 1;

        this.setState({
            pageSize: parseInt(value),
            currentPage: Math.ceil(startIndex / parseInt(value))
        });
    };

    checkNewDownloadFile = async () => {

        const { requestList } = this.state;
        let isExist = false;

        //console.log("requestList", requestList);

        const newList = requestList.filter(item => item.status === "new");

        if(newList.length > 0) {
            isExist = true;
            await this.setState({
                ...this.state,
                download: newList[newList.length - 1]
            });
        } else {
            await this.setState({
                ...this.state,
                download: {}
            });
        }

        //console.log("download", this.state.download);

        if (isExist === true) {
            this.openModal(modalType.MODAL_DOWNLOAD_2);
        } else {
            this.openModal(modalType.MODAL_ALERT);
        }
    };

    saveDownloadFile = async () => {
        const { downloadUrl } = this.state.download;
        console.log("[DownladList][saveDownloadFile]downloadUrl", downloadUrl);
        if(downloadUrl !== "") {
            const res = await services.axiosAPI.downloadFile(downloadUrl);
            if(res.result == Define.RSS_SUCCESS) {
                this.closeModal();
                await API.addDlHistory(Define.RSS_TYPE_FTP_AUTO ,res.fileName, "Download Completed")
            } else {
                this.closeModal();
                if(res.result == Define.COMMON_FAIL_NOT_FOUND) {
                    this.openModal(modalType.MODAL_FILE_NOT_FOUND)
                } else {
                    this.openModal(modalType.MODAL_NETWORK_ERROR)
                }
                await API.addDlHistory(Define.RSS_TYPE_FTP_AUTO ,res.fileName, "Download Fail")
            }
        } else {
            console.error("[DownladList][saveDownloadFile]downloadUrl is null");
            this.closeModal();
        }
        await this.loadDownloadList(this.state.requestId, this.state.requestName);
    }

    requestDownloadCancel = async () => {
        const { planId, fileId } = this.state.delete;

        try {
            const res = await services.axiosAPI.requestDelete(`${Define.REST_PLANS_DELETE_FILE}/${planId}/fileLists/${fileId}`)
            return Define.RSS_SUCCESS;

        } catch (error) {
            console.error(error);
            const { response: { status } } = error;
            if(status === Define.NOT_FOUND) return Define.COMMON_FAIL_NOT_FOUND;
            else return Define.COMMON_FAIL_SERVER_ERROR
        }
    }

    deleteDownloadFile = async () => {
        console.log("[DownladList][deleteDownloadFile]fileId", this.state.delete.fileId);
        if(this.state.delete.fileId !== "") {
            const res = await this.requestDownloadCancel();
            console.log("[DownladList][deleteDownloadFile]res", res);
            if(res === Define.RSS_SUCCESS) {
                const numerator = this.state.delete.keyIndex - 1 === 0 ? 1 : this.state.delete.keyIndex - 1;
                this.setState({
                    currentPage: Math.ceil(numerator / this.state.pageSize)
                });
                this.closeModal();
            } else {
                this.closeModal();
                if(res === Define.COMMON_FAIL_NOT_FOUND) {
                    this.openModal(modalType.MODAL_FILE_NOT_FOUND)
                } else {
                    this.openModal(modalType.MODAL_NETWORK_ERROR)
                }
            }
        } else {
            console.error("[DownladList][deleteDownloadFile]id is null");
            this.closeModal();
        }
        await this.loadDownloadList(this.state.requestId, this.state.requestName);
    }


    loadDownloadList = async (planId, planName) => {
        try {
            const res = await services.axiosAPI.requestGet(`${Define.REST_PLANS_GET_FILELIST}/${planId}/filelists`);
            const {lists} = res.data;
            console.log("[DownloadList][componentDidMount]res", res);
            let newRequestList = [];
            if (lists !== undefined) {
                newRequestList = lists.map((item, idx) => {
                    return {
                        created: item.created,
                        status: item.status,
                        fileId: item.fileId,
                        planId: item.planId,
                        downloadUrl: item.downloadUrl,
                        keyIndex: idx + 1
                    }
                })
            }

            await this.setState({
                ...this.state,
                requestName: planName,
                requestId: planId,
                requestList: newRequestList
            })
        } catch (error) {
            console.error(error);
        }
    }

    componentDidMount() {
        const requestList = async () => {
            const query = queryString.parse(this.props.location.search);
            const { planId, planName } = query;
            console.log("[DownloadList][componentDidMount]planId", planId);
            console.log("[DownloadList][componentDidMount]planName", planName);
            return await this.loadDownloadList(planId, planName);
        }
        requestList().then(r => r).catch(e => console.error(e));
    }

    render() {
        console.log("[DownloadList][render]");
        const {
            requestList,
            pageSize,
            currentPage,
            isDeleteOpen,
            isSelectDownloadOpen,
            isNewDownloadOpen,
            isAlertOpen,
            modalMessage
        } = this.state;
        const { length: count } = requestList;

        if (count === 0) {
            return (
                <Card className="auto-plan-box">
                    <CardHeader className="auto-plan-card-header">
                        Download List
                        <p>
                            Check the download list of <span>collection plan.</span>
                        </p>
                    </CardHeader>
                    <CardBody className="auto-plan-card-body">
                        <Col className="auto-plan-collection-list download-list">
                            <p className="no-download-list">
                                <FontAwesomeIcon icon={faExclamationCircle} size="7x" />
                            </p>
                            <p className="no-download-list">No completed requests.</p>
                        </Col>
                    </CardBody>
                </Card>
            );
        } else {
            const requests = filePaginate(requestList, currentPage, pageSize);

            return (
                <>
                    <ConfirmModal isOpen={isDeleteOpen}
                                  icon={faTrashAlt}
                                  message={modalMessage}
                                  style={"auto-plan"}
                                  leftBtn={"OK"}
                                  rightBtn={"Cancel"}
                                  actionBg={this.closeModal}
                                  actionLeft={this.deleteDownloadFile}
                                  actionRight={this.closeModal}
                    />
                    <ConfirmModal isOpen={isSelectDownloadOpen}
                                  icon={faDownload}
                                  message={modalMessage}
                                  style={"auto-plan"}
                                  leftBtn={"OK"}
                                  rightBtn={"Cancel"}
                                  actionBg={this.closeModal}
                                  actionLeft={this.saveDownloadFile}
                                  actionRight={this.closeModal}
                    />
                    <ConfirmModal isOpen={isNewDownloadOpen}
                                  icon={faDownload}
                                  message={modalMessage}
                                  leftBtn={"OK"}
                                  rightBtn={"Cancel"}
                                  style={"auto-plan"}
                                  actionBg={this.closeModal}
                                  actionLeft={this.saveDownloadFile}
                                  actionRight={this.closeModal}
                    />
                    <AlertModal isOpen={isAlertOpen} icon={faExclamationCircle} message={modalMessage} style={"auto-plan"} closer={this.closeModal} />
                    <Card className="auto-plan-box">
                        <CardHeader className="auto-plan-card-header">
                            Download List
                            <p>
                                Check the download list of <span>collection plan.</span>
                            </p>
                            <div className="select-area">
                                <label>Rows per page : </label>
                                <Select
                                    defaultValue= {10}
                                    onChange={this.handleSelectBoxChange}
                                    className="planlist"
                                >
                                    <Option value={10}>10</Option>
                                    <Option value={30}>30</Option>
                                    <Option value={50}>50</Option>
                                    <Option value={100}>100</Option>
                                </Select>
                            </div>
                        </CardHeader>
                        <CardBody className="auto-plan-card-body">
                            <Col className="auto-plan-collection-list download-list">
                                <div className="content-section header">
                                    <div className="plan-id">Plan ID: {this.state.requestName}</div>
                                    <div>
                                        <Button size="sm" className="download-btn"
                                                onClick={() => this.checkNewDownloadFile()}>
                                            New File Download
                                        </Button>
                                    </div>
                                </div>
                                <Table className="content-section">
                                    <thead>
                                    <tr>
                                        <th>File</th>
                                        <th>Status</th>
                                        <th>Delete</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {requests.map((request, idx) => {
                                        return (
                                            <tr key={idx}>
                                                <td>
                                                    <span className="request-id-area"
                                                         onClick={ () => {
                                                             this.setState({
                                                                 ...this.state,
                                                                 download: request
                                                             }, () => {
                                                                 this.openModal(modalType.MODAL_DOWNLOAD_1)
                                                             });
                                                         }}>
                                                        {request.created}
                                                    </span>
                                                </td>
                                                <td>{CreateStatus(request.status)}</td>
                                                <td>
                                                    <div className="icon-area"
                                                         onClick={ () => {
                                                             this.setState({
                                                                 ...this.state,
                                                                 delete: request
                                                             }, () => {
                                                                 this.openModal(modalType.MODAL_DELETE)
                                                             });
                                                         }}>
                                                        <FontAwesomeIcon icon={faTrashAlt}/>
                                                    </div>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                    </tbody>
                                </Table>
                            </Col>
                        </CardBody>
                        <RenderPagination
                            pageSize={pageSize}
                            itemsCount={count}
                            onPageChange={this.handlePaginationChange}
                            currentPage={currentPage}
                            className={"custom-pagination"}
                        />
                    </Card>
                </>
            );
        }
    }
}

export function CreateStatus(status) {
    switch (status) {
        case statusType.STATUS_NEW:
            return (
                <>
                    <FontAwesomeIcon className="twinkle" icon={faExclamation} /> New
                </>
            );

        case statusType.STATUS_FINISHED:
            return (
                <>
                    <FontAwesomeIcon icon={faCheck} /> Finished
                </>
            );

        default:
            console.log("invalid status!!!");
            return null;
    }
}

export default RSSAutoDownloadList;
