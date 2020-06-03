import React, { Component } from "react";
import { Col, Card, CardHeader, CardBody, Button, Table } from "reactstrap";
import { Select } from "antd";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
    faTrashAlt,
    faCheck,
    faExclamation,
    faExclamationCircle,
    faDownload
} from "@fortawesome/free-solid-svg-icons";
import { filePaginate, RenderPagination } from "../Common/Pagination";
import ConfirmModal from "../Common/ConfirmModal";
import AlertModal from "../Common/AlertModal";
import services from "../../services"
import moment from "moment";
import queryString from "query-string";
import * as Define from "../../define";
import {setRowsPerPage} from "../../api";
import * as API from "../../api";
import * as dlHistoryAction from "../../modules/dlHistory";

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

        console.log("requestList", requestList);

        const newList = requestList.filter(item => item.requestStatus === "new");

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

        console.log("download", this.state.download);

        if (isExist === true) {
            this.openModal(modalType.MODAL_DOWNLOAD_2);
        } else {
            this.openModal(modalType.MODAL_ALERT);
        }
    };

    saveDownloadFile = async () => {
        console.log("[DownladList][saveDownloadFile]id", this.state.download.id);
        if(this.state.download.id !== "") {
            const res = await services.axiosAPI.downloadFile(Define.REST_API_URL + '/plan/download?id=' + this.state.download.id);
            if(res.result == Define.RSS_SUCCESS) {
                this.closeModal();
                API.addDlHistory(Define.RSS_TYPE_FTP_AUTO ,res.fileName, "Download Completed")
            } else {
                this.closeModal();
                if(res.result == Define.COMMON_FAIL_NOT_FOUND) {
                    this.openModal(modalType.MODAL_FILE_NOT_FOUND)
                } else {
                    this.openModal(modalType.MODAL_NETWORK_ERROR)
                }
                API.addDlHistory(Define.RSS_TYPE_FTP_AUTO ,res.fileName, "Download Fail")
            }
        } else {
            console.error("[DownladList][saveDownloadFile]id is null");
            this.closeModal();
        }
        this.loadDownloadList(this.state.requestId, this.state.requestName);
    }

    requestDelete = async () => {
        const res = await services.axiosAPI.get(Define.REST_API_URL + '/downloadlist/delete?id=' + this.state.delete.id)
            .then( res  => {
                return Define.RSS_SUCCESS;
            })
            .catch(error => {
                const errResp = error.response;
                let res = Define.COMMON_FAIL_SERVER_ERROR;
                if(typeof errResp == "undefined") {
                    return res;
                }
                console.error("[DownLoadList][deleteDownloadFile]errResp", error.response);
                if(errResp.status === 404) {
                    res = Define.COMMON_FAIL_NOT_FOUND;
                }
                return res;
            });

        console.log("[DownLoadList][deleteDownloadFile]res", res);
        return res;
    }

    deleteDownloadFile = async () => {
        console.log("[DownladList][deleteDownloadFile]id", this.state.delete.id);
        if(this.state.delete.id !== "") {
            const res = await this.requestDelete();
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
        this.loadDownloadList(this.state.requestId, this.state.requestName);
    }


    loadDownloadList = async (id, name) => {
        let result = false;
        const res = await services.axiosAPI.get(Define.REST_API_URL + "/downloadlist/list?planId=" + id);
        const { data } = res;
        console.log("[DownloadList][componentDidMount]res", res);
        let newRequestList = [];
        if(data !== "") {
            newRequestList = data.map((item, idx) => {
                return {
                    requestId: item.title,
                    requestStatus: item.status,
                    id: item.id,
                    planId: item.planId,
                    path: item.path,
                    keyIndex: idx + 1
                }
            })
            result = true;
        }

        await this.setState({
            ...this.state,
            requestName: name,
            requestId: id,
            requestList: newRequestList
        })

        return result;
    }

    async componentDidMount() {
        const query = queryString.parse(this.props.location.search);
        const { id, name } = query;
        console.log("[DownloadList][componentDidMount]id", id);
        console.log("[DownloadList][componentDidMount]name", name);

        const res = await this.loadDownloadList(id, name);
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
                                  actionBg={this.closeModal}
                                  actionLeft={this.deleteDownloadFile}
                                  actionRight={this.closeModal}
                    />
                    <ConfirmModal isOpen={isSelectDownloadOpen}
                                  icon={faDownload}
                                  message={modalMessage}
                                  style={"auto-plan"}
                                  actionBg={this.closeModal}
                                  actionLeft={this.saveDownloadFile}
                                  actionRight={this.closeModal}
                    />
                    <ConfirmModal isOpen={isNewDownloadOpen}
                                  icon={faDownload}
                                  message={modalMessage}
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
                                                        {request.requestId}
                                                    </span>
                                                </td>
                                                <td>{CreateStatus(request.requestStatus)}</td>
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
