import React, { Component } from "react";
import {Button, FormGroup, Input} from "reactstrap";
import ReactTransitionGroup from "react-addons-css-transition-group";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faExclamationCircle} from "@fortawesome/free-solid-svg-icons";
import * as Define from '../../../define';
import * as API from "../../../api";

class InputModal extends Component {
    constructor(props) {
        super(props);
        this.errRef = React.createRef();
        this.state = {
            inputOpen: false,
            alertOpen: false,
            CompleteOpen: false,
            errMsg: "",
            alertMsg: ""
        };
    }

    openInputModal = () => {
        this.setState({
            ...this.state,
            inputOpen: true
        });
    };

    closeInputModal = () => {
        this.setState({
            ...this.state,
            inputOpen: false
        });
    };

    openAlertModal = (val) => {
        let msg = "";

        switch(val) {
            case Define.GENRE_SET_FAIL_NO_ITEM: msg = "Please choose a category."; break;
            case Define.GENRE_SET_FAIL_NOT_SELECT_GENRE: msg = "Please choose a genre."; break;
            default: msg="what's error : " + error; break;
        }

        this.setState({
            ...this.state,
            alertMsg: msg,
            alertOpen: true
        });
    };

    closeAlertModal = () => {
        this.setState({
            ...this.state,
            alertOpen: false
        });
    };

    canOpenModal = (openbtn) => {
        console.log("canOpenModal");
        console.log("openbtn", openbtn);
        console.log("this.props.logInfoListCheckCnt", this.props.logInfoListCheckCnt);
        if(openbtn === "Create") {
            if(this.props.logInfoListCheckCnt <= 0){
                this.props.setErrorStatus(Define.GENRE_SET_FAIL_NO_ITEM);
                //API.dispAlert(Define.GENRE_SET_FAIL_NO_ITEM);
                //return;
                this.openAlertModal(Define.GENRE_SET_FAIL_NO_ITEM);
                return;
            }
            this.props.onChangeGenreName("");
        } else if(openbtn === "Edit") {
            console.log("#############################");
            console.log("this.props.selectedKeyName", this.props.selectedKeyName );
            if(this.props.selectedKeyName === "selectGenre") {
                this.props.setErrorStatus(Define.GENRE_SET_FAIL_NOT_SELECT_GENRE);
                //API.dispAlert(Define.GENRE_SET_FAIL_NOT_SELECT_GENRE);
                this.openAlertModal(Define.GENRE_SET_FAIL_NOT_SELECT_GENRE);
                return;
            }
        }
        this.openInputModal();
    };

    actionFunc = async (openbtn) => {
        let selectedKeyName = "";

        if(openbtn === "Create"){
            selectedKeyName = this.props.genreName;
        } else if(openbtn === "Edit"){
            selectedKeyName = this.props.selectedKeyName;
        }

        //call async function
        const result = await this.props.confirmFunc(this.props.genreName, selectedKeyName);
        if(result === Define.RSS_SUCCESS){
            this.props.setErrorStatus(Define.RSS_SUCCESS);
            this.closeInputModal();
            this.props.handleSelectBoxChange(this.props.genreName);
        } else {
            let msg = "";

            this.props.setErrorStatus(result);

            switch (result) {
                case Define.GENRE_SET_FAIL_SAME_NAME: msg = "The genre name is duplicated."; break;
                case Define.GENRE_SET_FAIL_EMPTY_NAME: msg = "Please input genre name"; break;
                case Define.GENRE_SET_FAIL_SEVER_ERROR: msg = "Network connection error"; break;
                default: msg="what's error : " + error; break;
            }

            this.setState({
                ...this.state,
                errMsg: msg
            });

            this.errRef.current.classList.remove('modal-err-msg-hidden');

            //API.dispAlert(result);
        }
    };

    render() {
        const {
            title,
            openbtn,
            inputname,
            inputpholder,
            leftbtn,
            rightbtn
        } = this.props;
        const { inputOpen, alertOpen, errMsg, alertMsg } = this.state;

        return (
            <>
                <Button
                    outline
                    size="sm"
                    color="info"
                    className="catlist-btn"
                    onClick={() => this.canOpenModal(openbtn)}
                >
                    {openbtn}
                </Button>
                {inputOpen ? (
                    <ReactTransitionGroup
                        transitionName={"Custom-modal-anim"}
                        transitionEnterTimeout={200}
                        transitionLeaveTimeout={200}
                    >
                        <div className="Custom-modal-overlay" onClick={this.closeInputModal} />
                        <div className="Custom-modal">
                            <p className="title">{title}</p>
                            <div className="content-with-title">
                                <FormGroup>
                                    <Input
                                        type="text"
                                        name={inputname}
                                        value={this.props.genreName}
                                        placeholder={inputpholder}
                                        className="catlist-input"
                                        onChange={(e) => this.props.onChangeGenreName(e.target.value)}
                                    />
                                    <p className="modal-err-msg modal-err-msg-hidden" ref={this.errRef}>{errMsg}</p>
                                </FormGroup>
                            </div>
                            <div className="button-wrap">
                                <button
                                    className="primary form-type left-btn"
                                    onClick={() => this.actionFunc(openbtn)}
                                >
                                    {leftbtn}
                                </button>
                                <button
                                    className="primary form-type right-btn"
                                    onClick={this.closeInputModal}
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
                                <p>{alertMsg}</p>
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

export default InputModal;