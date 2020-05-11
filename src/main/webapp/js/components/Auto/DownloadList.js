import React, { Component } from "react";
import { Col, Card, CardHeader, CardBody, Button, Table } from "reactstrap";
import Select from "react-select";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
    faTrashAlt,
    faCheck,
    faExclamation
} from "@fortawesome/free-solid-svg-icons";
import { filePaginate, RenderPagination } from "../common/pagination";
import ConfirmModal from "./confirmmodal";

const MODAL_MESSAGE = "Are you sure you want to delete this request id?";

const STATUS_NEW = 1;
const STATUS_FINISH = 2;

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
                    requestStatus: STATUS_NEW
                },
                {
                    requestId: "0000000123456782",
                    requestStatus: STATUS_FINISH
                },
                {
                    requestId: "0000000123456783",
                    requestStatus: STATUS_NEW
                },
                {
                    requestId: "0000000123456784",
                    requestStatus: STATUS_FINISH
                },
                {
                    requestId: "0000000123456785",
                    requestStatus: STATUS_NEW
                },
                {
                    requestId: "0000000123456786",
                    requestStatus: STATUS_FINISH
                },
                {
                    requestId: "0000000123456787",
                    requestStatus: STATUS_NEW
                },
                {
                    requestId: "0000000123456788",
                    requestStatus: STATUS_FINISH
                },
                {
                    requestId: "0000000123456789",
                    requestStatus: STATUS_NEW
                },
                {
                    requestId: "00000001234567810",
                    requestStatus: STATUS_FINISH
                },
                {
                    requestId: "00000001234567811",
                    requestStatus: STATUS_NEW
                },
                {
                    requestId: "00000001234567812",
                    requestStatus: STATUS_FINISH
                },
                {
                    requestId: "00000001234567813",
                    requestStatus: STATUS_NEW
                },
                {
                    requestId: "00000001234567814",
                    requestStatus: STATUS_FINISH
                },
                {
                    requestId: "00000001234567815",
                    requestStatus: STATUS_NEW
                },
                {
                    requestId: "00000001234567816",
                    requestStatus: STATUS_FINISH
                }
            ],
            pageSize: 10,
            currentPage: 1,
            isOpen: false
        };
    }

    openModal = () => {
        this.setState({
            isOpen: true
        });
    };

    closeModal = () => {
        this.setState({
            isOpen: false
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

    render() {
        const { requestList, pageSize, currentPage, isOpen } = this.state;
        const { length: count } = requestList;

        const requests = filePaginate(requestList, currentPage, pageSize);
        const pagination = RenderPagination(
            pageSize,
            count,
            this.handlePaginationChange,
            "custom-pagination"
        );

        const renderModal = ConfirmModal(
            isOpen,
            faTrashAlt,
            MODAL_MESSAGE,
            this.closeModal
        );

        return (
            <>
                {renderModal}
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
                                    <Button size="sm" className="download-btn">
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
                                                <div className="request-id-area">
                                                    {request.requestId}
                                                </div>
                                            </td>
                                            <td>{CreateStatus(request.requestStatus)}</td>
                                            <td>
                                                <div className="icon-area" onClick={this.openModal}>
                                                    <FontAwesomeIcon icon={faTrashAlt} />
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

function CreateStatus(status) {
    switch (status) {
        case STATUS_NEW:
        default:
            return (
                <>
                    <FontAwesomeIcon className="twinkle" icon={faExclamation} /> New
                </>
            );

        case STATUS_FINISH:
            return (
                <>
                    <FontAwesomeIcon icon={faCheck} /> Finished
                </>
            );
    }
}

export default RSSAutoDownloadList;
