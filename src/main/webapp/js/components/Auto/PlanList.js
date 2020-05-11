import React, { Component } from "react";
import { Col, Card, CardHeader, CardBody, Table } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
    faEdit,
    faPlay,
    faStop,
    faCheck,
    faTimes,
    faTrashAlt
} from "@fortawesome/free-solid-svg-icons";
import Select from "react-select";
import { filePaginate, RenderPagination } from "../common/Pagination";
import ConfirmModal from "./ConfirmModal";

const PAGE_EDIT = 2;
const PAGE_DOWNLOAD = 3;
const MODAL_MESSAGE = "Are you sure you want to delete this collection plan?";

const STATUS_RUNNING = 1;
const STATUS_STOPPED = 2;

const DETAIL_COMPLETE = 1;
const DETAIL_FAILED = 2;

const optionList = [
    { value: 10, label: "10" },
    { value: 30, label: "30" },
    { value: 50, label: "50" },
    { value: 100, label: "100" }
];

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

class RSSautoplanlist extends Component {
    constructor(props) {
        super(props);
        this.state = {
            registeredList: [
                {
                    planId: "Plan 1",
                    planDescription: "default Plan",
                    planTarget: "3",
                    planPeriod: "2020-04-20 00:00 ~ 2020-04-27 23:59",
                    planStatus: STATUS_RUNNING,
                    planLastRun: "2020-04-27 15:37",
                    planDetail: DETAIL_COMPLETE
                },
                {
                    planId: "Plan 2",
                    planDescription: "gtpark's Plan",
                    planTarget: "14",
                    planPeriod: "2019-01-24 00:22 ~ 2019-05-31 23:59",
                    planStatus: STATUS_STOPPED,
                    planLastRun: "2019-06-04 00:00",
                    planDetail: DETAIL_FAILED
                },
                {
                    planId: "Plan 3",
                    planDescription: "chpark's Plan",
                    planTarget: "33",
                    planPeriod: "2020-04-20 00:00 ~ 2020-04-27 23:59",
                    planStatus: STATUS_RUNNING,
                    planLastRun: "2020-04-27 15:37",
                    planDetail: DETAIL_COMPLETE
                },
                {
                    planId: "Plan 4",
                    planDescription: "ymkwon's Plan",
                    planTarget: "14",
                    planPeriod: "2019-01-24 00:22 ~ 2019-05-31 23:59",
                    planStatus: STATUS_STOPPED,
                    planLastRun: "2019-06-04 00:00",
                    planDetail: DETAIL_FAILED
                },
                {
                    planId: "Plan 5",
                    planDescription: "gtpark's Plan",
                    planTarget: "14",
                    planPeriod: "2019-01-24 00:22 ~ 2019-05-31 23:59",
                    planStatus: STATUS_STOPPED,
                    planLastRun: "2019-06-04 00:00",
                    planDetail: DETAIL_FAILED
                },
                {
                    planId: "Plan 6",
                    planDescription: "chpark's Plan",
                    planTarget: "33",
                    planPeriod: "2020-04-20 00:00 ~ 2020-04-27 23:59",
                    planStatus: STATUS_RUNNING,
                    planLastRun: "2020-04-27 15:37",
                    planDetail: DETAIL_COMPLETE
                },
                {
                    planId: "Plan 7",
                    planDescription: "ymkwon's Plan",
                    planTarget: "14",
                    planPeriod: "2019-01-24 00:22 ~ 2019-05-31 23:59",
                    planStatus: STATUS_STOPPED,
                    planLastRun: "2019-06-04 00:00",
                    planDetail: DETAIL_FAILED
                },
                {
                    planId: "Plan 8",
                    planDescription: "gtpark's Plan",
                    planTarget: "14",
                    planPeriod: "2019-01-24 00:22 ~ 2019-05-31 23:59",
                    planStatus: STATUS_STOPPED,
                    planLastRun: "2019-06-04 00:00",
                    planDetail: DETAIL_FAILED
                },
                {
                    planId: "Plan 9",
                    planDescription: "chpark's Plan",
                    planTarget: "33",
                    planPeriod: "2020-04-20 00:00 ~ 2020-04-27 23:59",
                    planStatus: STATUS_RUNNING,
                    planLastRun: "2020-04-27 15:37",
                    planDetail: DETAIL_COMPLETE
                },
                {
                    planId: "Plan 10",
                    planDescription: "ymkwon's Plan",
                    planTarget: "14",
                    planPeriod: "2019-01-24 00:22 ~ 2019-05-31 23:59",
                    planStatus: STATUS_STOPPED,
                    planLastRun: "2019-06-04 00:00",
                    planDetail: DETAIL_FAILED
                },
                {
                    planId: "Plan 11",
                    planDescription: "gtpark's Plan",
                    planTarget: "14",
                    planPeriod: "2019-01-24 00:22 ~ 2019-05-31 23:59",
                    planStatus: STATUS_STOPPED,
                    planLastRun: "2019-06-04 00:00",
                    planDetail: DETAIL_FAILED
                },
                {
                    planId: "Plan 12",
                    planDescription: "chpark's Plan",
                    planTarget: "33",
                    planPeriod: "2020-04-20 00:00 ~ 2020-04-27 23:59",
                    planStatus: STATUS_RUNNING,
                    planLastRun: "2020-04-27 15:37",
                    planDetail: DETAIL_COMPLETE
                },
                {
                    planId: "Plan 13",
                    planDescription: "ymkwon's Plan",
                    planTarget: "14",
                    planPeriod: "2019-01-24 00:22 ~ 2019-05-31 23:59",
                    planStatus: STATUS_STOPPED,
                    planLastRun: "2019-06-04 00:00",
                    planDetail: DETAIL_FAILED
                },
                {
                    planId: "Plan 14",
                    planDescription: "gtpark's Plan",
                    planTarget: "14",
                    planPeriod: "2019-01-24 00:22 ~ 2019-05-31 23:59",
                    planStatus: STATUS_STOPPED,
                    planLastRun: "2019-06-04 00:00",
                    planDetail: DETAIL_FAILED
                },
                {
                    planId: "Plan 15",
                    planDescription: "chpark's Plan",
                    planTarget: "33",
                    planPeriod: "2020-04-20 00:00 ~ 2020-04-27 23:59",
                    planStatus: STATUS_RUNNING,
                    planLastRun: "2020-04-27 15:37",
                    planDetail: DETAIL_COMPLETE
                },
                {
                    planId: "Plan 16",
                    planDescription: "ymkwon's Plan",
                    planTarget: "14",
                    planPeriod: "2019-01-24 00:22 ~ 2019-05-31 23:59",
                    planStatus: STATUS_STOPPED,
                    planLastRun: "2019-06-04 00:00",
                    planDetail: DETAIL_FAILED
                }
            ],
            pageSize: 10,
            currentPage: 1,
            isConfirmOpen: false
        };
    }

    openModal = () => {
        this.setState({
            isConfirmOpen: true
        });
    };

    closeModal = () => {
        this.setState({
            isConfirmOpen: false
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
        const { registeredList } = this.state;
        const { length: count } = registeredList;

        if (count === 0) {
            return (
                <Card className="auto-plan-box">
                    <CardHeader className="auto-plan-card-header">
                        Plan Status
                        <p>
                            Check the status of the <span>registered collection plan.</span>
                        </p>
                    </CardHeader>
                    <CardBody className="auto-plan-card-body">
                        <Col className="auto-plan-collection-list" />
                    </CardBody>
                </Card>
            );
        } else {
            const { currentPage, pageSize, isConfirmOpen } = this.state;
            const { pageChanger } = this.props;

            const plans = filePaginate(registeredList, currentPage, pageSize);
            const pagination = RenderPagination(
                pageSize,
                count,
                this.handlePaginationChange,
                "custom-pagination"
            );

            const renderConfirmModal = ConfirmModal(
                isConfirmOpen,
                faTrashAlt,
                MODAL_MESSAGE,
                this.closeModal
            );

            return (
                <>
                    {renderConfirmModal}
                    <Card className="auto-plan-box">
                        <CardHeader className="auto-plan-card-header">
                            Plan Status
                            <p>
                                Check the status of the <span>registered collection plan.</span>
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
                            <Col className="auto-plan-collection-list">
                                <Table>
                                    <thead>
                                    <tr>
                                        <th>Plan ID</th>
                                        <th>Description</th>
                                        <th>Target</th>
                                        <th>Collection Period</th>
                                        <th>Status</th>
                                        <th>Last Run Time</th>
                                        <th>Detail</th>
                                        <th>Edit</th>
                                        <th>Delete</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {plans.map((plan, idx) => {
                                        return (
                                            <tr key={idx}>
                                                <td>
                                                    <div
                                                        className="plan-id-area"
                                                        onClick={() => pageChanger(PAGE_DOWNLOAD)}
                                                    >
                                                        {plan.planId}
                                                    </div>
                                                </td>
                                                <td>{plan.planDescription}</td>
                                                <td>{plan.planTarget}</td>
                                                <td>{plan.planPeriod}</td>
                                                <td>{CreateStatus(plan.planStatus)}</td>
                                                <td>{plan.planLastRun}</td>
                                                <td>{CreateDetail(plan.planDetail)}</td>
                                                <td>
                                                    <div
                                                        className="icon-area move-left"
                                                        onClick={() => pageChanger(PAGE_EDIT)}
                                                    >
                                                        <FontAwesomeIcon icon={faEdit} />
                                                    </div>
                                                </td>
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
}

function CreateStatus(status) {
    switch (status) {
        case STATUS_RUNNING:
        default:
            return (
                <>
                    <FontAwesomeIcon className="running" icon={faPlay} /> Running
                </>
            );

        case STATUS_STOPPED:
            return (
                <>
                    <FontAwesomeIcon className="stopped" icon={faStop} /> Stopped
                </>
            );
    }
}

function CreateDetail(detail) {
    switch (detail) {
        case DETAIL_COMPLETE:
        default:
            return (
                <>
                    <FontAwesomeIcon className="completed" icon={faCheck} /> Completed
                </>
            );

        case DETAIL_FAILED:
            return (
                <>
                    <FontAwesomeIcon className="failed" icon={faTimes} /> Falied
                </>
            );
    }
}

export default RSSautoplanlist;
