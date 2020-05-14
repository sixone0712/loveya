import React, { Component } from "react";
import { Col, Card, CardHeader, CardBody, Button, Table } from "reactstrap";
import Select from "react-select";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
    faTrashAlt,
    faCheck,
    faExclamation,
    faExclamationCircle,
    faDownload
} from "@fortawesome/free-solid-svg-icons";
import { filePaginate, renderPagination } from "../Common/Pagination";
import ConfirmModal from "../Common/ConfirmModal";
import AlertModal from "../Common/AlertModal";

const modalMessage = {
    MODAL_DELETE_MESSAGE: "Are you sure you want to delete the selected file?",
    MODAL_DOWNLOAD_MESSAGE_1:
        "Do you want to download a file of the selected request ID?",
    MODAL_DOWNLOAD_MESSAGE_2: "Do you want to download a new file?",
    MODAL_ALERT_MESSAGE: "No new files to download."
};

const statusType = {
    STATUS_NEW: 1,
    STATUS_FINISH: 2
};

const modalType = {
    MODAL_DELETE: 1,
    MODAL_DOWNLOAD_1: 2,
    MODAL_DOWNLOAD_2: 3,
    MODAL_ALERT: 4
};

const customSelectStyles = {
    container: styles => ({
        ...styles,
        display: "inline-block",
        width: "85px",
        fontSize: "14px",
        marginLeft: "10px"
    }),
    option: (styles, { isFocused, isSelected }) => {
        return {
            ...styles,
            backgroundColor: isSelected
                ? "rgba(92, 124, 250, 0.5)"
                : isFocused
                    ? "rgba(92, 124, 250, 0.3)"
                    : null,
            color: "black",
            ":active": {
                ...styles[":active"],
                backgroundColor: isSelected
                    ? "rgba(92, 124, 250, 0.9)"
                    : isFocused
                        ? "rgba(92, 124, 250, 0.7)"
                        : null
            }
        };
    },
    control: () => ({
        display: "flex",
        border: "1px solid rgb(92, 124, 250)",
        borderRadius: "3px",
        caretColor: "transparent",
        transition: "all .15s ease-in-out",
        ":hover": {
            outline: "0",
            boxShadow: "0 0 0 0.2em rgba(92, 124, 250, 0.5)"
        }
    }),
    dropdownIndicator: styles => ({
        ...styles,
        color: "rgba(92, 124, 250, 0.6)",
        ":hover": {
            ...styles[":hover"],
            color: "rgb(92, 124, 250)"
        }
    }),
    indicatorSeparator: styles => ({
        ...styles,
        backgroundColor: "rgba(92, 124, 250, 0.6)"
    }),
    menu: styles => ({
        ...styles,
        borderRadius: "3px",
        boxShadow:
            "0 0 0 1px rgba(92, 124, 250, 0.6), 0 4px 11px rgba(92, 124, 250, 0.6)"
    })
};

const optionList = [
    { value: 10, label: "10" },
    { value: 30, label: "30" },
    { value: 50, label: "50" },
    { value: 100, label: "100" }
];

class RSSAutoDownloadList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            requestList: [
                {
                    requestId: "0000000123456781",
                    requestStatus: statusType.STATUS_NEW
                },
                {
                    requestId: "0000000123456782",
                    requestStatus: statusType.STATUS_FINISH
                },
                {
                    requestId: "0000000123456783",
                    requestStatus: statusType.STATUS_NEW
                },
                {
                    requestId: "0000000123456784",
                    requestStatus: statusType.STATUS_FINISH
                },
                {
                    requestId: "0000000123456785",
                    requestStatus: statusType.STATUS_NEW
                },
                {
                    requestId: "0000000123456786",
                    requestStatus: statusType.STATUS_FINISH
                },
                {
                    requestId: "0000000123456787",
                    requestStatus: statusType.STATUS_NEW
                },
                {
                    requestId: "0000000123456788",
                    requestStatus: statusType.STATUS_FINISH
                },
                {
                    requestId: "0000000123456789",
                    requestStatus: statusType.STATUS_NEW
                },
                {
                    requestId: "00000001234567810",
                    requestStatus: statusType.STATUS_FINISH
                },
                {
                    requestId: "00000001234567811",
                    requestStatus: statusType.STATUS_NEW
                },
                {
                    requestId: "00000001234567812",
                    requestStatus: statusType.STATUS_FINISH
                },
                {
                    requestId: "00000001234567813",
                    requestStatus: statusType.STATUS_NEW
                },
                {
                    requestId: "00000001234567814",
                    requestStatus: statusType.STATUS_FINISH
                },
                {
                    requestId: "00000001234567815",
                    requestStatus: statusType.STATUS_NEW
                },
                {
                    requestId: "00000001234567816",
                    requestStatus: statusType.STATUS_FINISH
                }
            ],
            pageSize: 10,
            currentPage: 1,
            isDeleteOpen: false,
            isSelectDownloadOpen: false,
            isNewDownloadOpen: false,
            isAlertOpen: false,
            modalMessage: ""
        };
    }

    openModal = type => {
        switch (type) {
            case modalType.MODAL_DELETE:
                this.setState({
                    isDeleteOpen: true,
                    modalMessage: modalMessage.MODAL_DELETE_MESSAGE
                });
                break;

            case modalType.MODAL_DOWNLOAD_1:
                this.setState({
                    isSelectDownloadOpen: true,
                    modalMessage: modalMessage.MODAL_DOWNLOAD_MESSAGE_1
                });
                break;

            case modalType.MODAL_DOWNLOAD_2:
                this.setState({
                    isNewDownloadOpen: true,
                    modalMessage: modalMessage.MODAL_DOWNLOAD_MESSAGE_2
                });
                break;

            case modalType.MODAL_ALERT:
                this.setState({
                    isAlertOpen: true,
                    modalMessage: modalMessage.MODAL_ALERT_MESSAGE
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

    handleSelectBoxChange = newValue => {
        this.setState({
            pageSize: newValue.value
        });
    };

    checkNewDownloadFile = value => {
        if (value === true) {
            this.openModal(modalType.MODAL_DOWNLOAD_2);
        } else {
            this.openModal(modalType.MODAL_ALERT);
        }
    };

    render() {
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

        const requests = filePaginate(requestList, currentPage, pageSize);
        const pagination = renderPagination(
            pageSize,
            count,
            this.handlePaginationChange,
            "custom-pagination"
        );

        const renderDeleteModal = ConfirmModal(
            isDeleteOpen,
            faTrashAlt,
            modalMessage,
            "auto-plan",
            this.closeModal,
            this.closeModal,
            this.closeModal
        );

        const renderSelectDownloadModal = ConfirmModal(
            isSelectDownloadOpen,
            faDownload,
            modalMessage,
            "auto-plan",
            this.closeModal,
            this.closeModal,
            this.closeModal
        );

        const renderNewDownloadModal = ConfirmModal(
            isNewDownloadOpen,
            faDownload,
            modalMessage,
            "auto-plan",
            this.closeModal,
            this.closeModal,
            this.closeModal
        );

        const renderAlertModal = AlertModal(
            isAlertOpen,
            faExclamationCircle,
            modalMessage,
            "auto-plan",
            this.closeModal
        );

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
            return (
                <>
                    {renderDeleteModal}
                    {renderSelectDownloadModal}
                    {renderNewDownloadModal}
                    {renderAlertModal}
                    <Card className="auto-plan-box">
                        <CardHeader className="auto-plan-card-header">
                            Download List
                            <p>
                                Check the download list of <span>collection plan.</span>
                            </p>
                            <div className="select-area">
                                <label>Rows per page : </label>
                                <Select
                                    options={optionList}
                                    styles={customSelectStyles}
                                    defaultValue={optionList[0]}
                                    onChange={this.handleSelectBoxChange}
                                />
                            </div>
                        </CardHeader>
                        <CardBody className="auto-plan-card-body">
                            <Col className="auto-plan-collection-list download-list">
                                <div className="content-section header">
                                    <div className="plan-id">ID: CollectionPlan-501201-01</div>
                                    <div>
                                        <Button size="sm" className="download-btn"
                                                onClick={() => this.checkNewDownloadFile(false)}>
                                            New File Download
                                        </Button>
                                    </div>
                                </div>
                                <Table className="content-section">
                                    <thead>
                                    <tr>
                                        <th>Request ID</th>
                                        <th>Status</th>
                                        <th>Delete</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {requests.map((request, idx) => {
                                        return (
                                            <tr key={idx}>
                                                <td>
                                                    <div className="request-id-area"
                                                         onClick={() => this.openModal(modalType.MODAL_DOWNLOAD_1)}>
                                                        {request.requestId}
                                                    </div>
                                                </td>
                                                <td>{CreateStatus(request.requestStatus)}</td>
                                                <td>
                                                    <div className="icon-area"
                                                         onClick={() => this.openModal(modalType.MODAL_DELETE)}>
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
                        {pagination}
                    </Card>
                </>
            );
        }
    }
}

function CreateStatus(status) {
    switch (status) {
        case statusType.STATUS_NEW:
            return (
                <>
                    <FontAwesomeIcon className="twinkle" icon={faExclamation} /> New
                </>
            );

        case statusType.STATUS_FINISH:
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
