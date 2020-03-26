import React, { Component } from "react";
import { Card, CardBody, Col, FormGroup, Button } from "reactstrap";
import ReactTransitionGroup from "react-addons-css-transition-group";
import ScaleLoader from "react-spinners/ScaleLoader";
import DatePicker from "./DatePicker";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../modules/viewList";
import * as searchListActions from "../../modules/searchList";
import * as API from "../../api";
import * as Define from "../../define";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faExclamationCircle} from "@fortawesome/free-solid-svg-icons";

class FormList extends Component{
    constructor() {
        super();
        this.state = {
            modalMsg: null,
            intervalValue: null,
            isProcessOpen: false,
            isErrorOpen: false
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
            status = "Error";
        }

        return status;
    };

    onSearch = async () => {
        let msg = "";
        const errCode = await API.setSearchList(this.props);

        switch (errCode) {
            case Define.SEARCH_FAIL_NO_MACHINE_AND_CATEGORY:
                msg = "Please choose a machine and category.";
                break;
            case Define.SEARCH_FAIL_DATE:
                msg = "Please set the start time before the end time.";
                break;
            default:
                break;
        }

        if (msg.toString().length > 0) {
            this.setState({
                modalMsg: msg
            });
            this.openErrorModal();
            return;
        }
        this.openProcessModal();
        console.log('##########################################################');
        console.log('setSearchList before');
        console.log('##########################################################');
        //await API.setSearchList(this.props);
        console.log('##########################################################');
        console.log('setSearchList after');
        console.log('##########################################################');
        API.startSearchList(this.props);
        console.log('##########################################################');
        console.log('startSearchList end');
        console.log('##########################################################');

        const intervalProps = {
            modalFunc: this.closeProcessModal,
            getIntervalFunc : this.getIntervalFunc,
            setIntervalFunc: this.setIntervalFunc,
            getResStatus: this.getResStatus
        };

        const interval = API.setWatchSearchStatus(intervalProps);
        console.log("!!!!!!!!!!!!!!!!!!!");
        console.log("interval", interval);
        this.setState({
            ...this.state,
            intervalValue: interval
        })

    };

    openProcessModal = () => {
        this.setState({
            isProcessOpen: true
        });
    };

    closeProcessModal = () => {
        this.setState({
            isProcessOpen: false
        });
    };

    openErrorModal = () => {
        this.setState({
           isErrorOpen: true
        });
    }

    closeErrorModal = () => {
        this.setState({
           isErrorOpen: false
        });
    }

    render() {
        const { isProcessOpen, isErrorOpen, modalMsg } = this.state;

        return (
            <Card className="ribbon-wrapper formlist-custom">
                <CardBody className="custom-scrollbar card-body-custom card-body-formlist">
                    <div className="ribbon ribbon-clip ribbon-success">Date</div>
                    <Col>
                        <FormGroup className="formlist-form-group">
                            <DatePicker/>
                        </FormGroup>
                    </Col>
                    <div className="manual-btn-area">
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
                                </div>
                            </ReactTransitionGroup>
                        ) : (
                            <ReactTransitionGroup
                                transitionName={"Custom-modal-anim"}
                                transitionEnterTimeout={200}
                                transitionLeaveTimeout={200}
                            />
                        )}
                        {isErrorOpen ? (
                            <ReactTransitionGroup
                                transitionName={"Custom-modal-anim"}
                                transitionEnterTimeout={200}
                                transitionLeaveTimeout={200}
                            >
                                <div className="Custom-modal-overlay" onClick={this.closeErrorModal} />
                                <div className="Custom-modal">
                                    <div className="content-without-title">
                                        <p>
                                            <FontAwesomeIcon icon={faExclamationCircle} size="6x" />
                                        </p>
                                        <p>{modalMsg}</p>
                                    </div>
                                    <div className="button-wrap">
                                        <button className="alert-type green" onClick={this.closeErrorModal}>
                                            Close
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
      startDate: state.searchList.get('startDate'),
      endDate: state.searchList.get('endDate'),
      resSuccess: state.pender.success['searchList/SEARCH_LOAD_RESPONSE_LIST'],
      resPending: state.pender.pending['searchList/SEARCH_LOAD_RESPONSE_LIST'],
      resError: state.pender.failure['searchList/SEARCH_LOAD_RESPONSE_LIST'],
    }),
    (dispatch) => ({
      // bindActionCreators 는 액션함수들을 자동으로 바인딩해줍니다.
      viewListActions: bindActionCreators(viewListActions, dispatch),
      searchListActions: bindActionCreators(searchListActions, dispatch)
    })
)(FormList);