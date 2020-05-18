import React, { Component } from "react";
import {Button} from "reactstrap";
import ReactTransitionGroup from "react-addons-css-transition-group";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBan, faChevronCircleDown, faDownload, faExclamationCircle} from "@fortawesome/free-solid-svg-icons";
import ScaleLoader from "react-spinners/ScaleLoader";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as searchListActions from "../../../modules/searchList";
import * as API from "../../../api";
import * as Define from "../../../define";
import services from "../../../services";
import axios from 'axios';
import moment from "moment";

class DownloadConfirmModal extends Component {
    constructor(props) {
        super(props);
        const { openbtn, message, leftbtn, rightbtn } = this.props;
        this.state = {
            openbtn,
            message,
            leftbtn,
            rightbtn,
            parentModalOpen: false,
            processModalOpen: false,
            cancelModalOpen: false,
            completeModalOpen: false,
            errorModalOpen: false,
            modalMsg: ""
        };
    }

    setErrorMsg = (errCode) => {
        const  msg = API.getErrorMsg(errCode);
        if (msg.toString().length > 0) {
            this.setState({
                modalMsg: msg
            });
            return true;
        }
        return false;
    };

    openParentModal = () => {
        if(this.props.downloadCnt <= 0) {
            this.props.setErrorStatus(Define.FILE_FAIL_NO_ITEM);
            this.setErrorMsg(Define.FILE_FAIL_NO_ITEM);
            this.openErrorModal();
        } else {
            this.props.setErrorStatus(Define.RSS_SUCCESS);
            this.setState({
                ...this.state,
                parentModalOpen: true
            });
        }
    };

    closeParentModal = () => {
        this.setState({
            ...this.state,
            parentModalOpen: false
        });
    };

    getDownloadStatus = () => {
        return this.props.downloadStatus.toJS();
    };

    setSearchListActions = (values) => {
        const { searchListActions }  = this.props;
        searchListActions.searchSetDlStatus({...values});
    };

    openProcessModal = async () => {
        this.closeParentModal();

        setTimeout(() => {
            this.setState({
                ...this.state,
                processModalOpen: true
            });
        }, 100);

        // 초기화
        const { searchListActions } = this.props;
        searchListActions.searchSetDlStatus({func:null, dlId: "", status: "init", totalFiles: 0, downloadFiles: 0})
        this.props.setErrorStatus(Define.RSS_SUCCESS);

        // Download Request 요청
        const requestId = await API.requestDownload(this.props);
        console.log("requestId", requestId);
        searchListActions.searchSetDlStatus({dlId: requestId});

        // SetInterval에서 사용할 Modal Func 추가
        const modalFunc = {
            closeProcessModal: this.closeProcessModal,
            openCompleteModal: this.openCompleteModal,
            closeCompleteModal: this.closeCompleteModal,
            setErrorMsg: this.setErrorMsg,
            openErrorModal: this.openErrorModal,
            getDownloadStatus: this.getDownloadStatus,
            setSearchListActions: this.setSearchListActions
        };

        // Download Status 요청
        if(requestId !== "") {
            const intervalFunc = await API.setWatchDlStatus(requestId, modalFunc);
            searchListActions.searchSetDlStatus({func: intervalFunc});
        }
    };

    closeProcessModal = () => {
        this.setState({
            ...this.state,
            processModalOpen: false
        });
    };

    openCancelModal = () => {
        this.setState({
            ...this.state,
            cancelModalOpen: true
        });
    };

    closeCancelModal = async (isCancel) => {
        if(isCancel) {
            const downloadStatus = this.props.downloadStatus;
            const { func, dlId } = downloadStatus.toJS();

            // Stop requesting status to SetInveral
            if(func !== null){
                clearInterval(func);
                // Reauest Cancel
                const res = await services.axiosAPI.get("/dl/cancel?dlId=" + dlId);
                console.log("res", res)
                // 에러 처리 추가 필요
            }

            const {searchListActions } = this.props;
            // Initialize state
            searchListActions.searchSetDlStatus({func:null, dlId: "", status: "init", totalFiles: 0, downloadFiles: 0});
            this.props.setErrorStatus(Define.RSS_SUCCESS);
            this.closeProcessModal();
            setTimeout(() => {
                this.setState({
                    ...this.state,
                    cancelModalOpen: false
                });
            }, 100);
        } else {
            // setState is asynchronous, so it waits with await.
            await this.setState({
                ...this.state,
                cancelModalOpen: false,
            });

            const downloadStatus = this.props.downloadStatus;
            const { status, totalFiles, downloadFiles} = downloadStatus.toJS();
            // If the download has already been completed, open openCompleteModal.
            if(status === "done" && totalFiles === downloadFiles) {
                this.openCompleteModal();
            }
        }
    };

    openCompleteModal = () => {
        if(this.state.cancelModalOpen !== true) {
            this.setState({
                ...this.state,
                completeModalOpen: true
            });
        }
    };

    closeCompleteModal = async (isSave) => {
        let result = Define.RSS_SUCCESS;
        this.setState({
            ...this.state,
            completeModalOpen: false
        });

        if(isSave) {
            const { downloadStatus } = this.props;
            result = await services.axiosAPI.downloadFile("/dl/download?dlId=" + downloadStatus.toJS().dlId);
            this.props.setErrorStatus(result);
        }
        // 상태 초기화
        const { searchListActions } = this.props;
        searchListActions.searchSetDlStatus({func:null, dlId: "", status: "init", totalFiles: 0, downloadFiles: 0});
        this.props.setErrorStatus(Define.RSS_SUCCESS);
    };

    openErrorModal = () => {
        this.setState({
            errorModalOpen: true
        });
    };

    closeErrorModal = () => {
        this.setState({
            errorModalOpen: false
        });
    };

    render() {
        const {
            openbtn,
            message,
            leftbtn,
            rightbtn,
            parentModalOpen,
            processModalOpen,
            cancelModalOpen,
            completeModalOpen,
            errorModalOpen,
            modalMsg
        } = this.state;

        const { totalFiles, downloadFiles} = this.props.downloadStatus.toJS();

        return (
            <>
                <Button
                    outline
                    size="sm"
                    color="info"
                    className="filelist-btn"
                    onClick={this.openParentModal}
                >
                    {openbtn}
                </Button>
                {parentModalOpen ? (
                    <ReactTransitionGroup
                        transitionName={"Custom-modal-anim"}
                        transitionEnterTimeout={200}
                        transitionLeaveTimeout={200}
                    >
                        <div
                            className="Custom-modal-overlay"
                            onClick={this.closeParentModal}
                        />
                        <div className="Custom-modal">
                            <div className="content-without-title">
                                <p>
                                    <FontAwesomeIcon icon={faDownload} size="6x" />
                                </p>
                                <p>{message}</p>
                            </div>
                            <div className="button-wrap">
                                <button
                                    className="secondary form-type left-btn"
                                    onClick={this.openProcessModal}
                                >
                                    {leftbtn}
                                </button>
                                <button
                                    className="secondary form-type right-btn"
                                    onClick={this.closeParentModal}
                                >
                                    {rightbtn}
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
                {processModalOpen ? (
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
                                <p className="no-margin-no-padding">
                                    Downloading...
                                </p>
                                {totalFiles > 0 && true &&
                                <p className="no-margin-no-padding">
                                    ({downloadFiles}/{totalFiles})
                                </p>
                                }
                            </div>
                            <div className="button-wrap">
                                <button
                                    className="secondary alert-type"
                                    onClick={this.openCancelModal}
                                >
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
                {cancelModalOpen ? (
                    <ReactTransitionGroup
                        transitionName={"Custom-modal-anim"}
                        transitionEnterTimeout={200}
                        transitionLeaveTimeout={200}
                    >
                        <div className="Custom-modal-overlay" />
                        <div className="Custom-modal">
                            <div className="content-without-title">
                                <p>
                                    <FontAwesomeIcon icon={faBan} size="6x" />
                                </p>
                                <p>Are you sure you want to cancel the download?</p>
                            </div>
                            <div className="button-wrap">
                                <button
                                    className="secondary form-type left-btn"
                                    onClick={()=> this.closeCancelModal(true)}
                                >
                                    Yes
                                </button>
                                <button
                                    className="secondary form-type right-btn"
                                    onClick={()=> this.closeCancelModal(false)}
                                >
                                    No
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
                {completeModalOpen ? (
                    <ReactTransitionGroup
                        transitionName={"Custom-modal-anim"}
                        transitionEnterTimeout={200}
                        transitionLeaveTimeout={200}
                    >
                        <div className="Custom-modal-overlay" />
                        <div className="Custom-modal">
                            <div className="content-without-title">
                                <p>
                                    <FontAwesomeIcon icon={faChevronCircleDown} size="6x" />
                                </p>
                                <p>Download Complete!</p>
                            </div>
                            <div className="button-wrap">
                                <button
                                    className="secondary form-type left-btn"
                                    onClick={() => this.closeCompleteModal(true)}
                                >
                                    Save
                                </button>
                                <button
                                    className="secondary form-type right-btn"
                                    onClick={() => this.closeCompleteModal(false)}
                                >
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
                {errorModalOpen ? (
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
                                <p>{this.state.modalMsg}</p>
                            </div>
                            <div className="button-wrap">
                                <button className="alert-type secondary" onClick={this.closeErrorModal}>
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
            </>
        );
    }
}

export default connect(
    (state) => ({
        responseList: state.searchList.get('responseList'),
        downloadCnt: state.searchList.get('downloadCnt'),
        downloadStatus: state.searchList.get('downloadStatus'),
    }),
    (dispatch) => ({
        searchListActions: bindActionCreators(searchListActions, dispatch)
    })
)(DownloadConfirmModal);
