import React, { Component } from "react";
import {Button} from "reactstrap";
import ReactTransitionGroup from "react-addons-css-transition-group";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTrashAlt} from "@fortawesome/free-solid-svg-icons";
import * as API from "../../api";
import * as Define from "../../define";

class ConfirmModal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isOpen: false,
        };
    }

    openModal = () => {
        this.setState({
            ...this.state,
            isOpen: true
        });
    };

    closeModal = () => {
        this.setState({
            ...this.state,
            isOpen: false
        });
    };

    canOpenModal = () => {
        if(this.props.selectedKeyName === "selectGenre") {
            this.props.setErrorStatus(Define.GENRE_SET_FAIL_NOT_SELECT_GENRE);
            API.dispAlert(Define.GENRE_SET_FAIL_NOT_SELECT_GENRE);
            return;
        }

        this.openModal();
    };

    actionFunc = async () => {
        console.log("###actionFuncStart");
        //call async functionn
        const result = await this.props.confirmFunc(this.props.selectedKeyName);
        console.log("actionFunc=>confirmFunc=>result", result);
        if(result === Define.RSS_SUCCESS){
            this.props.setErrorStatus(Define.RSS_SUCCESS);
            this.closeModal();
            this.props.handleSelectBoxChange("selectGenre");
        } else {
            this.props.setErrorStatus(result);
            API.dispAlert(result);
        }
        console.log("###actionFuncEnd");
    };

    render() {
        const { openbtn, message, leftbtn, rightbtn } = this.props;
        const { isOpen } = this.state;
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
                {isOpen ? (
                    <ReactTransitionGroup
                        transitionName={"Custom-modal-anim"}
                        transitionEnterTimeout={200}
                        transitionLeaveTimeout={200}
                    >
                        <div className="Custom-modal-overlay" onClick={this.closeModal} />
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
                                    onClick={this.closeModal}
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
            </>
        );
    }
}

export default ConfirmModal;