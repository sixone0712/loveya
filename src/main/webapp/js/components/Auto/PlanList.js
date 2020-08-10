import React, {Component} from "react";
import {Card, CardBody, CardHeader, Col, Table} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faCheck,
    faEdit,
    faExclamationCircle,
    faPause,
    faPlay,
    faRegistered,
    faStop,
    faTimes,
    faTrashAlt
} from "@fortawesome/free-solid-svg-icons";
import ClockLoader from "react-spinners/ClockLoader";
import {Select} from "antd";
import {filePaginate, RenderPagination} from "../Common/Pagination";
import ConfirmModal from "../Common/ConfirmModal";
import AlertModal from "../Common/AlertModal";
import * as Define from "../../define";
import services from "../../services"
import moment from "moment";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as autoPlanActions from "../../modules/autoPlan";
import * as viewListActions from "../../modules/viewList"

const { Option } = Select;

const messageType = {
    CONFIRM_DELETE_MESSAGE: "Are you sure you want to delete this collection plan?",
    CONFIRM_STOP_MESSAGE: "Are you sure you want to stop this collection plan?",
    CONFIRM_START_MESSAGE: "Are you sure you want to run this collection plan?",
    EDIT_ALERT_MESSAGE: "Because of the current collecting it can not be edited.",
    DELETE_ALERT_MESSAGE: "Because of the current collecting it can not be deleted."
};

export const statusType = {
    RUNNING: "running",
    STOPPED: "stop"
};

export const detailType = {
    REGISTERED: "registered",
    COLLECTING: "collecting",
    COLLECTED: "collected",
    SUSPENDED: "suspended",
    HALTED: "halted",
    COMPLETED: "completed"
}

const spinnerStyles = {
    display: "inline-block",
    top: "2px"
}

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
            isDeleteOpen: false,
            isStatusOpen: false,
            isAlertOpen: false,
            alertMessage: "",
            statusMessage: "",
            selectedPlanId: "",
            selectedPlanStatus: "",
            deleteIndex: ""
        };
    }

    componentDidMount() {
        this.loadPlanList().then(r => r).catch(e => console.log(e));
    }

    loadPlanList = async () => {
        try {
            const res = await services.axiosAPI.requestGet(Define.REST_PLANS_GET_PLANS);
            const { lists } = res.data;
            const newData = lists.map((item, idx) => {
                return (
                  {
                      planId: item.planName,
                      planDescription: item.description,
                      planTarget: item.categoryCodes.length,
                      planPeriodStart: moment(item.from, "YYYYMMDDHHmmss").format("YYYY-MM-DD HH:mm:ss"),
                      planPeriodEnd: moment(item.to, "YYYYMMDDHHmmss").format("YYYY-MM-DD HH:mm:ss"),
                      planStatus: item.status,
                      planLastRun: item.lastCollection == null
                        ? "-"
                        : moment(item.lastCollection, "YYYYMMDDHHmmss").format("YYYY-MM-DD HH:mm:ss"),
                      planDetail: item.detailedStatus,
                      id: item.planId,
                      tool: item.machineNames,
                      logType: item.categoryCodes,
                      interval: item.interval,
                      collectStart: moment(item.start, "YYYYMMDDHHmmss").format("YYYY-MM-DD HH:mm:ss"),
                      collectTypeStr: item.type,
                      keyIndex: idx + 1,
                      planType: null,       //need to add
                      commands: null,       //need to add
                  }
                );
            })
            //console.log("[AUTO][loadPlanList]newData", newData);
            await this.setState({
                ...this.state,
                registeredList: newData
            })
        } catch (e) {
            console.error(e);
        }
    }

    setEditPlanList = (id, status, detail) => {
        if (status === statusType.RUNNING || detail === detailType.COLLECTING) {
        //if (!status) {
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
                description: findList.planDescription,
                planType: findList.planType
            });
            console.log("id", id);
            console.log("findList.planType", findList.planType);
            this.props.history.push(`${Define.PAGE_REFRESH_AUTO_PLAN_EDIT}&editId=${String(id)}&type=${findList.planType}`);
        }
    }

    openDeleteModal = async (planId, status, detail, index) => {
        if (status === statusType.RUNNING || detail === detailType.COLLECTING) {
        //if(!status) {
            this.openAlert(messageType.DELETE_ALERT_MESSAGE);
        } else {
            await this.setState({
                ...this.state,
                isDeleteOpen: true,
                selectedPlanId: planId,
                deleteIndex: index
            });
        }
    };

    closeDeleteModal = async (deleting, selectedPlanId) => {
        const { pageSize, deleteIndex } = this.state;
        const numerator = deleteIndex - 1 === 0 ? 1 : deleteIndex - 1;

        if(deleting) {
            try {
                const res = await services.axiosAPI.requestDelete(`${Define.REST_PLANS_DELETE_PLANS}/${selectedPlanId}`);
                await this.setState({
                    currentPage: Math.ceil(numerator / pageSize),
                    deleteIndex: ""
                });
            } catch (error) {
                console.error(error);
            }
        }

        await this.setState({
            ...this.state,
            isDeleteOpen: false,
            selectedPlanId: ""
        });

        setTimeout(this.loadPlanList, 200);
    };

    openStatusModal = (status, planId) => {
        if(status === statusType.RUNNING) {
            this.setState({
                isStatusOpen: true,
                statusMessage: messageType.CONFIRM_STOP_MESSAGE,
                selectedPlanId: planId,
                selectedPlanStatus: statusType.RUNNING
            });
        } else {
            this.setState({
                isStatusOpen: true,
                statusMessage: messageType.CONFIRM_START_MESSAGE,
                selectedPlanId: planId,
                selectedPlanStatus: statusType.STOPPED
            });
        }
    }

    closeStatusModal = () => {
        this.setState({
           isStatusOpen: false,
           statusMessage: "",
           selectedPlanId: "",
           selectedPlanStatus: ""
        });
    }

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


    handleSelectBoxChange = value => {
        const { pageSize, currentPage } = this.state;
        const startIndex = (currentPage - 1) * pageSize === 0 ? 1 : (currentPage - 1) * pageSize + 1;

        this.setState({
            pageSize: parseInt(value),
            currentPage: Math.ceil(startIndex / parseInt(value))
        });
    };

    stopDownload = async (planId) => {
        try {
            const res = await services.axiosAPI.requestPut(`${Define.REST_PLANS_CHANGE_PLAN_STATUS}/${planId}/stop`);
        } catch (error) {
            console.error(error);
        }

        try {
            await this.loadPlanList();
        } catch (error) {
            console.error(error);
        }
    }

    restartDownload = async (planId) => {
        try {
            const res = await services.axiosAPI.requestPut(`${Define.REST_PLANS_CHANGE_PLAN_STATUS}/${planId}/restart`);
        } catch (error) {
            console.error(error);
        }

        try {
            await this.loadPlanList();
        } catch (error) {
            console.error(error);
        }
    }

    handleStatusChange = async (status, planId) => {
        switch(status) {
            case statusType.RUNNING:
                await this.stopDownload(planId);
                break;

            case statusType.STOPPED:
                await this.restartDownload(planId);
                break;

            default:
                console.log("[planlist.js] plan status error!!!");
                break;
        }

        this.closeStatusModal();
    }

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
            const { currentPage, pageSize, isStatusOpen, isDeleteOpen, isAlertOpen, alertMessage, statusMessage, selectedPlanId, selectedPlanStatus } = this.state;
            const plans = filePaginate(registeredList, currentPage, pageSize);

            return (
                <>
                    <ConfirmModal isOpen={isStatusOpen}
                                  icon={faExclamationCircle}
                                  message={statusMessage}
                                  style={"auto-plan"}
                                  leftBtn={"Yes"}
                                  rightBtn={"No"}
                                  actionBg={this.closeStatusModal}
                                  actionLeft={() => this.handleStatusChange(selectedPlanStatus, selectedPlanId)}
                                  actionRight={this.closeStatusModal}
                    />
                    <ConfirmModal isOpen={isDeleteOpen}
                                  icon={faTrashAlt}
                                  message={messageType.CONFIRM_DELETE_MESSAGE}
                                  style={"auto-plan"}
                                  leftBtn={"OK"}
                                  rightBtn={"Cancel"}
                                  actionBg={() => this.closeDeleteModal(false, selectedPlanId)}
                                  actionLeft={() => this.closeDeleteModal(true, selectedPlanId)}
                                  actionRight={() => this.closeDeleteModal(false, selectedPlanId)}
                    />
                    <AlertModal isOpen={isAlertOpen} icon={faExclamationCircle} message={alertMessage} style={"auto-plan"} closer={this.closeAlert} />
                    <Card className="auto-plan-box">
                        <CardHeader className="auto-plan-card-header">
                            Plan Status
                            <p>
                                Check the status of the <span>registered collection plan.</span>
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
                            <Col className="auto-plan-collection-list">
                                <Table>
                                    <thead>
                                    <tr>
                                        <th>Plan ID</th>
                                        <th>Description</th>
                                        <th>Type</th>
                                        <th>Target/Command</th>
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
                                                    <span
                                                        className="plan-id-area"
                                                        onClick={ () => {
                                                            const param = `?planId=${plan.id}&planName=${plan.planId}`;
                                                            this.props.history.push(Define.PAGE_AUTO_DOWNLOAD + param);
                                                        }}
                                                    >
                                                        {plan.planId}
                                                    </span>
                                                </td>
                                                <td>{plan.planDescription}</td>
                                                <td>-</td>
                                                <td>{plan.planTarget}</td>
                                                <td>{`${plan.planPeriodStart} ~ ${plan.planPeriodEnd}`}</td>
                                                <td>{CreateStatus(plan.planStatus, () => this.openStatusModal(plan.planStatus, plan.id))}</td>
                                                <td>{plan.planLastRun}</td>
                                                <td>{CreateDetail(plan.planDetail)}</td>
                                                <td>
                                                    <div
                                                        className="icon-area move-left"
                                                        /*onClick={ () =>  this.props.history.push(Define.PAGE_AUTO_PLAN_EDIT) }*/
                                                        onClick={ () =>  this.setEditPlanList(plan.id, plan.planStatus, plan.planDetail) }

                                                    >
                                                        <FontAwesomeIcon icon={faEdit} />
                                                    </div>
                                                </td>
                                                <td>
                                                    <div className="icon-area" onClick={ () => this.openDeleteModal(plan.id, plan.planStatus, plan.planDetail, plan.keyIndex) }>
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

export function CreateStatus(status, modalOpen) {
    let component = null;

    switch (status) {
        case statusType.RUNNING:
            component = <span className="status-area" onClick={modalOpen}><FontAwesomeIcon className="running" icon={faPlay}/> Running</span>;
            break;
        case statusType.STOPPED:
            component = <span className="status-area" onClick={modalOpen}><FontAwesomeIcon className="stopped" icon={faStop}/> Stopped</span>;
            break;
        default:
            console.error("plan detail error");   break;
    }

    /*
    if (!status) {
        component = <span className="status-area" onClick={modalOpen}><FontAwesomeIcon className="running" icon={faPlay}/> Running</span>;
    } else {
        component = <span className="status-area" onClick={modalOpen}><FontAwesomeIcon className="stopped" icon={faStop}/> Stopped</span>;
    }
    */
    return component;
}

export function CreateDetail(detail) {
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

