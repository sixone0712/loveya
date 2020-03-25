import React, { Component } from "react";
import {Button} from "reactstrap";
import ReactTransitionGroup from "react-addons-css-transition-group";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBan, faChevronCircleDown, faDownload} from "@fortawesome/free-solid-svg-icons";
import ScaleLoader from "react-spinners/ScaleLoader";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as searchListActions from "../../modules/searchList";
import * as API from "../../api";
import * as Define from "../../define";
import services from "../../services";
import axios from 'axios';
import moment from "moment";

class DownloadConfirmModal extends Component {
    constructor(props) {
        super(props);
        const { openbtn, message, leftbtn, rightbtn } = this.props;
        this.openParentModal = this.openParentModal.bind(this);
        this.closeParentModal = this.closeParentModal.bind(this);
        this.openProcessModal = this.openProcessModal.bind(this);
        this.closeProcessModal = this.closeProcessModal.bind(this);
        this.openCancelModal = this.openCancelModal.bind(this);
        this.closeCancelModal = this.closeCancelModal.bind(this);
        this.openCompleteModal = this.openCompleteModal.bind(this);
        this.closeCompleteModal = this.closeCompleteModal.bind(this);
        this.state = {
            openbtn,
            message,
            leftbtn,
            rightbtn,
            parentModalOpen: false,
            processModalOpen: false,
            cancelModalOpen: false,
            completeModalOpen: false,
        };
    }

    openParentModal = () => {
        if(this.props.downloadCnt <= 0) {
            this.props.setErrorStatus(Define.FILE_FAIL_NO_ITEM);
            API.dispAlert(Define.FILE_FAIL_NO_ITEM);
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

    openProcessModal = async () => {
        this.setState({
            ...this.state,
            parentModalOpen: false,
            processModalOpen: true
        });

        // 초기화
        const { searchListActions } = this.props;
        searchListActions.searchSetDlStatus({dlId: "", status: "", totalFiles: 0, downloadFiles: 0})

        // Download Request 요청
        const requestId = await API.requestDownload(this.props);
        searchListActions.searchSetDlStatus({dlId: requestId});
        console.log("requestId", requestId);

        // Download Status 요청
        const modalFunc = {
            closeProcessModal: this.closeProcessModal,
            openCompleteModal: this.openCompleteModal,
            closeCompleteModal: this.closeCompleteModal
        };

        if(requestId !== "") {
            const intervalFunc = await API.setWatchDlStatus(this.props, requestId, modalFunc);
            console.log("intervalFunc", intervalFunc);
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

    closeCancelModal = (siCancel) => {
        if(cancel) {
            this.setState({
                ...this.state,
                processModalOpen: false,
                cancelModalOpen: false

            });
        } else {
            this.setState({
                ...this.state,
                cancelModalOpen: false,
            });
        }
    };

    openCompleteModal = () => {
        this.setState({
            ...this.state,
            completeModalOpen: true
        });
    };

    closeCompleteModal = async (isSave) => {
        let result = Define.RSS_SUCCESS;
        this.setState({
            ...this.state,
            completeModalOpen: false,
            cancelModalOpen: false,
            processModalOpen: false,
            parentModalOpen: false
        });

        if(isSave) {
            const {downloadStatus} = this.props;
            //const res = await services.axiosAPI.get("dl/download?dlId=" + downloadStatus.toJS().dlId);
            //console.log("res.data", res.data);
            result = await services.axiosAPI.downloadFile(downloadStatus.toJS().dlId);
        }
        console.log("result", result);
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
            completeModalOpen
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
                        <div className="Custom-modal-overlay child-overlay" />
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
                                <p>
                                    Downloading...
                                </p>
                                {totalFiles > 0 && true &&
                                <p>
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
                        <div className="Custom-modal-overlay child-overlay" />
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
                        <div className="Custom-modal-overlay child-overlay" />
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
