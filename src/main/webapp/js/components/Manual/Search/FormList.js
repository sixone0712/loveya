import React, {Component} from "react";
import {Button, Card, CardBody, Col, FormGroup} from "reactstrap";
import ReactTransitionGroup from "react-addons-css-transition-group";
import ScaleLoader from "react-spinners/ScaleLoader";
import DatePicker from "./DatePicker";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../../modules/viewList";
import * as searchListActions from "../../../modules/searchList";
import * as API from "../../../api";
import * as Define from "../../../define";
import {faExclamationCircle} from "@fortawesome/free-solid-svg-icons";
import AlertModal from "../../Common/AlertModal";
import ConfirmModal from "../../Common/ConfirmModal";
import services from "../../../services";

export const modalType = {
    PROCESS: 1,
    CANCEL: 2,
    ALERT: 3,
    CANCEL_COMPLETE: 4
};

class FormList extends Component{
    constructor() {
        super();
        this.state = {
            alertMsg: null,
            intervalValue: null,
            isProcessOpen: false,
            isCancelOpen: false,
            isAlertOpen: false
        }
    }

    getIntervalFunc = () => {
        return this.state.intervalValue;
    };

    setIntervalFunc = (value) => {
        this.setState({
            ...this.state,
            intervalValue: value
        });
    };

    getResStatus = () => {
        let status = "init";
        if(this.props.resSuccess) {
            status = "success";
        } else if (this.props.resPending) {
            status = "pending";
        } else if (this.props.resError) {
            status = "error";
        }

        return status;
    };

    onSetErrorState = (errCode) => {
        let msg = "";

        switch (errCode) {
            case Define.SEARCH_FAIL_NO_MACHINE_AND_CATEGORY:
                msg = "Please choose a machine and category.";
                break;
            case Define.SEARCH_FAIL_DATE:
                msg = "Please set the start time before the end time.";
                break;
            case Define.SEARCH_FAIL_SERVER_ERROR:
                msg = "Network connection error.";
                break;
            default:
                break;
        }

        if (msg.toString().length > 0) {
            this.setState({
                alertMsg: msg
            });
            return true;
        }
        return false;
    };

    onSearch = async () => {
        let msg = "";
        // Save Seacrh Request Data
        const errCode = await API.setSearchList(this.props);

        if (this.onSetErrorState(errCode)) {
            await this.openModal(modalType.ALERT);
            return;
        }

        await this.openModal(modalType.PROCESS);

        API.startSearchList(this.props);

        const intervalProps = {
            closeProcessModal: () => this.closeModal(modalType.PROCESS),
            getIntervalFunc : this.getIntervalFunc,
            setIntervalFunc: this.setIntervalFunc,
            getResStatus: this.getResStatus,
            openErrorModal: () => this.openModal(modalType.ALERT),
            onSetErrorState: this.onSetErrorState
        };

        const interval = API.setWatchSearchStatus(intervalProps);
        this.setState({
            ...this.state,
            intervalValue: interval
        })
    };

    openModal = async (type) => {
        switch(type) {
            case modalType.PROCESS:
                this.setState({
                    isProcessOpen: true
                });
                break;
                
            case modalType.CANCEL:
                this.setState({
                    isCancelOpen: true
                });
                break;
                
            case modalType.ALERT:
                this.setState({
                   isAlertOpen: true
                });
                break;

            case modalType.CANCEL_COMPLETE:
                //this.closeModal();
                if(this.props.responseListCnt === 0) {
                    services.axiosAPI.postCancel();
                    clearTimeout(this.getIntervalFunc());
                }
                await this.setState({
                    ...this.state,
                    isProcessOpen: false,
                    isCancelOpen: false,
                    isAlertOpen: false,
                    alertMsg: "",
                    intervalValue: null
                })

                if(this.props.responseListCnt === 0) {
                    setTimeout(() => {
                        this.setState({
                            isAlertOpen: true,
                            alertMsg: "Search was canceled."
                        });
                    }, 200);
                }
                break;

            default:
                console.log("[formlist.js] invalid modal type!!!!");
                break;
        }
    };

    closeModal = (type) => {
        switch(type) {
            case modalType.PROCESS:
                this.setState({
                    isProcessOpen: false,
                });
                break;

            case modalType.CANCEL:
                this.setState({
                    isCancelOpen: false
                });
                break;

            case modalType.ALERT:
                this.setState({
                    isAlertOpen: false,
                    alertMsg: ""
                });
                break;

            default:
                this.setState({
                    isProcessOpen: false,
                    isCancelOpen: false,
                    isAlertOpen: false,
                    alertMsg: ""
                })
                break;
        }
    }

    render() {
        const { isProcessOpen, isCancelOpen, isAlertOpen, alertMsg } = this.state;

        return (
            <Card className="ribbon-wrapper formlist-card">
                <CardBody className="custom-scrollbar manual-card-body">
                    <div className="ribbon ribbon-clip ribbon-success">Date</div>
                    <Col>
                        <FormGroup className="formlist-form-group">
                            <DatePicker/>
                        </FormGroup>
                    </Col>
                    <div className="card-btn-area">
                        <Button
                            outline size="sm"
                            color="info"
                            className="formlist-btn"
                            onClick={this.onSearch}
                        >
                            Search
                        </Button>
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
                                        <p>Searching...</p>
                                    </div>
                                    <div className="button-wrap">
                                        <button className="alert-type green" onClick={() => this.openModal(modalType.CANCEL)}>
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
                                      icon={faExclamationCircle}
                                      message={"Are you sure want to cancel the search?"}
                                      style={"green"}
                                      leftBtn={"Yes"}
                                      rightBtn={"No"}
                                      actionBg={null}
                                      actionLeft={() => this.openModal(modalType.CANCEL_COMPLETE)}
                                      actionRight={() => this.closeModal(modalType.CANCEL)}
                        />
                        <AlertModal isOpen={isAlertOpen} icon={faExclamationCircle} message={alertMsg} style={"green"} closer={() => this.closeModal(modalType.ALERT)} />
                    </div>
                </CardBody>
            </Card>
        );
    }
}

export default connect(
    (state) => ({
      toolInfoListCheckCnt: state.viewList.get('toolInfoListCheckCnt'),
      logInfoListCheckCnt: state.viewList.get('logInfoListCheckCnt'),
      toolInfoList: state.viewList.get('toolInfoList'),
      logInfoList: state.viewList.get('logInfoList'),
      requestList: state.searchList.get('requestList'),
      responseListCnt: state.searchList.get('responseListCnt'),
      startDate: state.searchList.get('startDate'),
      endDate: state.searchList.get('endDate'),
      resSuccess: state.pender.success['searchList/SEARCH_LOAD_RESPONSE_LIST'],
      resPending: state.pender.pending['searchList/SEARCH_LOAD_RESPONSE_LIST'],
      resError: state.pender.failure['searchList/SEARCH_LOAD_RESPONSE_LIST'],
    }),
    (dispatch) => ({
      viewListActions: bindActionCreators(viewListActions, dispatch),
      searchListActions: bindActionCreators(searchListActions, dispatch)
    })
)(FormList);