import React, { Component } from "react";
import { Col, Card, CardHeader, CardBody, Table } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
    faEdit,
    faPlay,
    faStop,
    faCheck,
    faTimes,
    faTrashAlt,
    faExclamationCircle,
    faRegistered,
    faPause
} from "@fortawesome/free-solid-svg-icons";
import ClockLoader from "react-spinners";
import Select from "react-select";
import { filePaginate, renderPagination } from "../Common/Pagination";
import ConfirmModal from "../Common/ConfirmModal";
import AlertModal from "../Common/AlertModal";
import * as Define from "../../define";
import services from "../../services"
import moment from "moment";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as autoPlanActions from "../../modules/autoPlan";
import * as viewListActions from "../../modules/viewList"
import {setRowsPerPage} from "../../api";
import {message} from "antd";

const messageType = {
    CONFIRM_MESSAGE: "Are you sure you want to delete this collection plan?",
    EDIT_ALERT_MESSAGE: "Because of the current collecting it can not be edited.",
    DELETE_ALERT_MESSAGE: "Because of the current collecting it can not be deleted."
};

const statusType = {
    RUNNING: "running",
    STOPPED: "stop"
};

const detailType = {
    REGISTERED: "registered",
    COLLECTING: "collecting",
    COLLECTED: "collected",
    SUSPENDED: "suspended",
    HALTED: "halted",
    COMPLETED: "completed"
}

const MODAL_MESSAGE = "Are you sure you want to delete this collection plan?";

const STATUS_RUNNING = "running";
const STATUS_STOPPED = "stop";

const DETAIL_REGISTERED = "registered";
const DETAIL_COLLECTING = "collecting";
const DETAIL_COLLECTED = "collected";
const DETAIL_SUSPENDED = "suspended";
const DETAIL_HALTED = "halted";
const DETAIL_COMPLETED = "completed";


const optionList = [
    { value: 10, label: "10" },
    { value: 30, label: "30" },
    { value: 50, label: "50" },
    { value: 100, label: "100" }
];

const spinnerStyles = {
    display: "inline-block",
    top: "2px"
}

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
                /*
                {
                    planId: "Plan 1",
                    planDescription: "default Plan",
                    planTarget: "3",
                    planPeriod: "2020-04-20 00:00 ~ 2020-04-27 23:59",
                    planStatus: STATUS_RUNNING,
                    planLastRun: "2020-04-27 15:37",
                    planDetail: DETAIL_COMPLETE
                }
                */
            ],
            pageSize: 10,
            currentPage: 1,
            isConfirmOpen: false,
            isAlertOpen: false,
            alertMessage: "",
            selectedPlanId: "",
        };
    }

    componentDidMount() {
        this.loadPlanList();
    }

    loadPlanList = async () => {
        const res =  await services.axiosAPI.get(Define.REST_API_URL + "/plan/list?withExpired=yes");
        console.log("[AUTO][loadPlanList]res", res);
        const { data } = res;
        const newData = data.map(item => {
            const targetArray = item.logType.split(",")
            return (
                {
                    planId: item.planName,
                    planDescription: item.description,
                    planTarget: targetArray.length,
                    planPeriodStart: moment(item.start).format("YYYY-MM-DD HH:mm:ss"),
                    planPeriodEnd:moment(item.end).format("YYYY-MM-DD HH:mm:ss"),
                    planStatus: item.status,
                    planLastRun: item.lastCollect == null ? "-" : moment(item.lastCollect).format("YYYY-MM-DD HH:mm:ss"),
                    planDetail: item.detail,
                    id: item.id,
                    tool: item.tool,
                    logType: item.logType,
                    interval: item.interval,
                    collectStart: moment(item.collectStart).format("YYYY-MM-DD HH:mm:ss"),
                    collectTypeStr: item.collectTypeStr,
                    expired: item.expired,
                }
            );
        })

        console.log("[AUTO][loadPlanList]newData", newData);

        this.setState({
            ...this.state,
            registeredList: newData
        })

    }

    setEditPlanList = (id, status) => {
        if (status === statusType.RUNNING) {
            this.openAlert(messageType.EDIT_ALERT_MESSAGE);
        } else {
            console.log("[AUTO][setEditPlanList]setEditPlanList");
            console.log("[AUTO][setEditPlanList]id", id);
            const {registeredList} = this.state;
            const findList = registeredList.find(item => item.id == id);

            const {viewListActions} = this.props;
            viewListActions.viewSetEditPlanList({tool: findList.tool, logCode: findList.logType});

            const {autoPlanActions} = this.props;
            autoPlanActions.autoPlanSetEditPlanList({
                planId: findList.planId,
                collectStart: findList.collectStart,
                from: findList.planPeriodStart,
                to: findList.planPeriodEnd,
                collectType: findList.collectTypeStr,
                interval: findList.interval,
                description: findList.planDescription
            });
            console.log("id", id);
            this.props.history.push(Define.PAGE_REFRESH_AUTO_PLAN_EDIT + "&editId=" + String(id));
        }
    }

    openModal = async (planId, status) => {
        if (status === statusType.RUNNING) {
            this.openAlert(messageType.DELETE_ALERT_MESSAGE);
        } else {
            await this.setState({
                ...this.state,
                isConfirmOpen: true,
                selectedPlanId: planId,
            });
        }
    };

    closeModal = async (deleting, selectedPlanId) => {
        let res;
        if(deleting) {
            res = await services.axiosAPI.get(Define.REST_API_URL + '/plan/delete?id='+selectedPlanId);
        }

        await this.setState({
            ...this.state,
            isConfirmOpen: false,
            selectedPlanId: ""
        });

        setTimeout(this.loadPlanList, 300);
    };

    openAlert = (message) => {
        this.setState({
           isAlertOpen: true,
           alertMessage: message
        });
    }

    closeAlert = () => {
        this.setState({
           isAlertOpen: false,
           alertMessage: ""
        });
    }

    handlePaginationChange = page => {
        this.setState({
            ...this.state,
            currentPage: page
        });
    };


    handleSelectBoxChange = newValue => {
        const { pageSize, currentPage } = this.state;
        const startIndex = (currentPage - 1) * pageSize === 0 ? 1 : (currentPage - 1) * pageSize + 1;

        this.setState({
            ...this.state,
            pageSize: parseInt(newValue.value),
            currentPage: Math.ceil(startIndex / parseInt(newValue.value))
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
                        <Col className="auto-plan-collection-list">
                            <p className="no-registered-plan">
                                <FontAwesomeIcon icon={faExclamationCircle} size="7x" />
                            </p>
                            <p className="no-registered-plan">
                                No registered collection plans.
                            </p>
                        </Col>
                    </CardBody>
                </Card>
            );
        } else {
            const { currentPage, pageSize, isConfirmOpen, isAlertOpen, alertMessage, selectedPlanId } = this.state;
            const plans = filePaginate(registeredList, currentPage, pageSize);
            const pagination = renderPagination(
                pageSize,
                count,
                this.handlePaginationChange,
                currentPage,
                "custom-pagination"
            );

            const renderConfirmModal = ConfirmModal(
                isConfirmOpen,
                faTrashAlt,
                messageType.CONFIRM_MESSAGE,
                "auto-plan",
                () => this.closeModal(false, selectedPlanId),
                () => this.closeModal(true, selectedPlanId),
                () => this.closeModal(false, selectedPlanId)
            );

            const renderAlertModal = AlertModal(
                isAlertOpen,
                faExclamationCircle,
                alertMessage,
                "auto-plan",
                this.closeAlert);

            return (
                <>
                    {renderConfirmModal}
                    {renderAlertModal}
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
                                                        onClick={ () => {
                                                            const param = `?id=${plan.id}&name=${plan.planId}`;
                                                            this.props.history.push(Define.PAGE_AUTO_DOWNLOAD + param);
                                                            }
                                                        }
                                                    >
                                                        {plan.planId}
                                                    </div>
                                                </td>
                                                <td>{plan.planDescription}</td>
                                                <td>{plan.planTarget}</td>
                                                <td>{`${plan.planPeriodStart} ~ ${plan.planPeriodEnd}`}</td>
                                                <td>{CreateStatus(plan.planStatus)}</td>
                                                <td>{plan.planLastRun}</td>
                                                <td>{CreateDetail(plan.planDetail)}</td>
                                                <td>
                                                    <div
                                                        className="icon-area move-left"
                                                        /*onClick={ () =>  this.props.history.push(Define.PAGE_AUTO_PLAN_EDIT) }*/
                                                        onClick={ () =>  this.setEditPlanList(plan.id, plan.planStatus) }

                                                    >
                                                        <FontAwesomeIcon icon={faEdit} />
                                                    </div>
                                                </td>
                                                <td>
                                                    <div className="icon-area" onClick={ () => this.openModal(plan.id, plan.planStatus) }>
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
    let component = null;
    switch (status) {
        case statusType.RUNNING:
            component = (<><FontAwesomeIcon className="running" icon={faPlay} /> Running</>);    break;
        case statusType.STOPPED:
            component = (<><FontAwesomeIcon className="stopped" icon={faStop} /> Stopped</>);    break;
        default:
            console.error("plan detail error");   break;
    }

    return component;
}

function CreateDetail(detail) {
    let component = null;
    switch (detail) {
        case detailType.REGISTERED:
            component = (<><FontAwesomeIcon className="completed" icon={faRegistered} /> Registered</>);   break;
        case detailType.COLLECTING:
            component = (<><ClockLoader size={15} color={"rgb(47, 158, 68)"} css={spinnerStyles}/> Collecting</>);   break;
        case detailType.COLLECTED:
            component = (<><FontAwesomeIcon className="completed" icon={faCheck} /> Collected</>);   break;
        case detailType.SUSPENDED:
            component = (<><FontAwesomeIcon className="failed" icon={faPause} /> Suspended</>);   break;
        case detailType.HALTED:
            component = (<><FontAwesomeIcon className="failed" icon={faTimes} /> Halted</>);   break;
        case detailType.COMPLETED:
            component = (<><FontAwesomeIcon className="completed" icon={faCheck} /> Completed</>);   break;
        default:
            console.error("plan detail error");   break;
    }

    return component;
}

export default connect(
    (state) => ({
        toolInfoList: state.viewList.get('toolInfoList'),
        logInfoList: state.viewList.get('logInfoList'),
        autoPlan: state.autoPlan.get('autoPlan'),
    }),
    (dispatch) => ({
        viewListActions: bindActionCreators(viewListActions, dispatch),
        autoPlanActions: bindActionCreators(autoPlanActions, dispatch),
    })
)(RSSautoplanlist);

