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
    faExclamationCircle
} from "@fortawesome/free-solid-svg-icons";
import Select from "react-select";
import { filePaginate, renderPagination } from "../Common/Pagination";
import ConfirmModal from "../Common/ConfirmModal";
import * as DEFINE from "../../define";
import services from "../../services"
import moment from "moment";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as autoPlanActions from "../../modules/autoPlan";
import * as viewListActions from "../../modules/viewList"

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
            selectedPlanId: "",
        };
    }

    componentDidMount() {
        this.loadPlanList();
    }

    loadPlanList = async () => {
        const res =  await services.axiosAPI.get("/plan/list");
        console.log("res", res);
        const { data } = res;

        const newData = data.map(item => {
            const targetArray = item.logType.split(",")
            return (
                {
                    planId: "Plan " + item.id,
                    planDescription: item.description,
                    planTarget: targetArray.length,
                    planPeriodStart: moment(item.start).format("YYYY-MM-DD HH:mm:ss"),
                    planPeriodEnd:moment(item.end).format("YYYY-MM-DD HH:mm:ss"),
                    planStatus: STATUS_RUNNING,     // 수정필요
                    planLastRun: moment(item.lastCollect).format("YYYY-MM-DD HH:mm:ss"),    // 수정필요
                    planDetail: DETAIL_COMPLETE,    // 수정필요
                    id: item.id,
                    tool: item.tool,
                    logType: item.logType,
                    interval: item.interval,
                    collectStart: moment(item.start).format("YYYY-MM-DD HH:mm:ss"),    //수정필요
                }
            );
        })

        this.setState({
            ...this.state,
            registeredList: newData
        })

    }

    setEditPlanList = (id) => {
        console.log("setEditPlanList");
        console.log("id", id);
        const { registeredList } = this.state;
        const findList = registeredList.find(item => item.id == id);

        const { viewListActions } = this.props;
        viewListActions.viewSetEditPlanList({ tool: findList.tool, logCode: findList.logType });

        const { autoPlanActions } = this.props;
        autoPlanActions.autoPlanSetEditPlanList({
            planId: findList.planId,
            collectStart: findList.collectStart,
            from: findList.planPeriodStart,
            to: findList.planPeriodEnd,
            collectType: DEFINE.AUTO_MODE_CYCLE,       // 수정필요
            interval: findList.interval,
            description: findList.planDescription
        });
        console.log("id", id);
        this.props.history.push(DEFINE.PAGE_REFRESH_AUTO_PLAN_EDIT +  "&editId=" + String(id));
    }

    openModal = async (planId) => {
        await this.setState({
            ...this.state,
            isConfirmOpen: true,
            selectedPlanId: planId,
        });
    };

    closeModal = async (deleting, selectedPlanId) => {
        let res;
        if(deleting) {
            res = await services.axiosAPI.get('/plan/delete?id='+selectedPlanId);
        }

        await this.setState({
            ...this.state,
            isConfirmOpen: false,
            selectedPlanId: ""
        });

        setTimeout(this.loadPlanList, 300);
    };

    handlePaginationChange = page => {
        this.setState({
            ...this.state,
            currentPage: page
        });
    };

    handleSelectBoxChange = newValue => {
        this.setState({
            ...this.state,
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
                        <Col className="auto-plan-collection-list">
                            <p className="no-registered-plan">
                                <FontAwesomeIcon icon={faExclamationCircle} size="7x" />
                            </p>
                            <p className="no-registered-plan">
                                There are no registered collection plans.
                            </p>
                        </Col>
                    </CardBody>
                </Card>
            );
        } else {
            const { currentPage, pageSize, isConfirmOpen, selectedPlanId } = this.state;
            const plans = filePaginate(registeredList, currentPage, pageSize);
            const pagination = renderPagination(
                pageSize,
                count,
                this.handlePaginationChange,
                "custom-pagination"
            );

            const renderConfirmModal = ConfirmModal(
                isConfirmOpen,
                faTrashAlt,
                MODAL_MESSAGE,
                "auto-plan",
                () => this.closeModal(false, selectedPlanId),
                () => this.closeModal(true, selectedPlanId),
                () => this.closeModal(false, selectedPlanId)
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
                                                        onClick={ () =>  this.props.history.push(DEFINE.PAGE_AUTO_DOWNLOAD) }
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
                                                        /*onClick={ () =>  this.props.history.push(DEFINE.PAGE_AUTO_PLAN_EDIT) }*/
                                                        onClick={ () =>  this.setEditPlanList(plan.id) }

                                                    >
                                                        <FontAwesomeIcon icon={faEdit} />
                                                    </div>
                                                </td>
                                                <td>
                                                    <div className="icon-area" onClick={ () => this.openModal(plan.id) }>
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
            return (
                <>
                    <FontAwesomeIcon className="completed" icon={faCheck} /> Completed
                </>
            );

        case DETAIL_FAILED:
            return (
                <>
                    <FontAwesomeIcon className="failed" icon={faTimes} /> Failed
                </>
            );

        default:
            console.log("plan status detail error");
            return null;
    }
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

