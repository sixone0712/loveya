import React, { Component } from "react";
import {Button} from "reactstrap";
import ReactTransitionGroup from "react-addons-css-transition-group";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faExclamationCircle, faTrashAlt} from "@fortawesome/free-solid-svg-icons";
import * as API from "../../api";
import * as Define from "../../define";

class ConfirmModal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            confirmOpen: false,
            alertOpen: false,
        };
    }

    openConfirmModal = () => {
        this.setState({
            ...this.state,
            confirmOpen: true
        });
    };

    closeConfirmModal = () => {
        this.setState({
            ...this.state,
            confirmOpen: false
        });
    };

    openAlertModal = () => {
        this.setState({
            ...this.state,
            alertOpen: true,
        });
    };

    closeAlertModal = () => {
        this.setState({
            ...this.state,
            alertOpen: false
        });
    };

    canOpenModal = () => {
        if(this.props.selectedKeyName === "selectGenre") {
            this.props.setErrorStatus(Define.GENRE_SET_FAIL_NOT_SELECT_GENRE);
            //API.dispAlert(Define.GENRE_SET_FAIL_NOT_SELECT_GENRE);
            this.openAlertModal();
            return;
        }

        this.openConfirmModal();
    };

    actionFunc = async () => {
        console.log("###actionFuncStart");
        //call async functionn
        const result = await this.props.confirmFunc(this.props.selectedKeyName);
        console.log("actionFunc=>confirmFunc=>result", result);
        if(result === Define.RSS_SUCCESS){
            this.props.setErrorStatus(Define.RSS_SUCCESS);
            this.closeConfirmModal();
            this.props.handleSelectBoxChange("selectGenre");
        } else {
            this.props.setErrorStatus(result);
            API.dispAlert(result);
        }
        console.log("###actionFuncEnd");
    };

    render() {
        const { openbtn, message, leftbtn, rightbtn } = this.props;
        const { confirmOpen, alertOpen } = this.state;
        return (
            <>
                <Button
                    outline
                    size="sm"
                    color="info"
                    className="catlist-btn"
                    onClick={this.canOpenModal}
                >
                    {openbtn}
                </Button>
                {confirmOpen ? (
                    <ReactTransitionGroup
                        transitionName={"Custom-modal-anim"}
                        transitionEnterTimeout={200}
                        transitionLeaveTimeout={200}
                    >
                        <div className="Custom-modal-overlay" onClick={this.closeConfirmModal} />
                        <div className="Custom-modal">
                            <div className="content-without-title">
                                <p>
                                    <FontAwesomeIcon icon={faTrashAlt} size="6x" />
                                </p>
                                <p>{message}</p>
                            </div>
                            <div className="button-wrap">
                                <button
                                    className="primary form-type left-btn"
                                    onClick={this.actionFunc}
                                >
                                    {leftbtn}
                                </button>
                                <button
                                    className="primary form-type right-btn"
                                    onClick={this.closeConfirmModal}
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
                {alertOpen ? (
                    <ReactTransitionGroup
                        transitionName={"Custom-modal-anim"}
                        transitionEnterTimeout={200}
                        transitionLeaveTimeout={200}
                    >
                        <div className="Custom-modal-overlay" onClick={this.closeAlertModal} />
                        <div className="Custom-modal">
                            <div className="content-without-title">
                                <p>
                                    <FontAwesomeIcon icon={faExclamationCircle} size="6x" />
                                </p>
                                <p>Please choose a genre.</p>
                            </div>
                            <div className="button-wrap">
                                <button
                                    className="primary alert-type"
                                    onClick={this.closeAlertModal}
                                >
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

export default ConfirmModal;