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
            completeModalOpen: false
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
            processModalOpen: true
        });

        const { searchListActions } = this.props;
        searchListActions.searchSetDlId({dlId: "", status: "", totalFiles: 0, downloadFiles: 0})

        const requestId = await API.requestDownload(this.props);
        searchListActions.searchSetDlId({dlId: requestId});
        console.log("requestId", requestId);
        console.log(await services.axiosAPI.get("dl/status?dlId=" + requestId));
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

    closeCancelModal = () => {
        this.setState({
            ...this.state,
            cancelModalOpen: false,
            processModalOpen: false,
            parentModalOpen: false
        });
    };

    openCompleteModal = () => {
        this.setState({
            ...this.state,
            completeModalOpen: true
        });
    };

    closeCompleteModal = () => {
        this.setState({
            ...this.state,
            completeModalOpen: false,
            cancelModalOpen: false,
            processModalOpen: false,
            parentModalOpen: false
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
            completeModalOpen
        } = this.state;
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
                                    <br />
                                    (10/100)
                                </p>
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
                                    onClick={this.closeCancelModal}
                                >
                                    Yes
                                </button>
                                <button
                                    className="secondary form-type right-btn"
                                    onClick={this.openCompleteModal}
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
                                    onClick={this.closeCompleteModal}
                                >
                                    Save
                                </button>
                                <button
                                    className="secondary form-type right-btn"
                                    onClick={this.closeCompleteModal}
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
