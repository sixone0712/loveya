import React, { Component } from "react";
import {Button, FormGroup, Input} from "reactstrap";
import ReactTransitionGroup from "react-addons-css-transition-group";
import * as Define from '../../define';
import * as API from "../../api";

class InputModal extends Component {
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

    canOpenModal = (openbtn) => {
        console.log("canOpenModal");
        console.log("openbtn", openbtn);
        console.log("this.props.logInfoListCheckCnt", this.props.logInfoListCheckCnt);
        if(openbtn === "Create") {
            if(this.props.logInfoListCheckCnt <= 0){
                this.props.setErrorStatus(Define.GENRE_SET_FAIL_NO_ITEM);
                API.dispAlert(Define.GENRE_SET_FAIL_NO_ITEM);
                return;
            }
            this.props.onChangeGenreName("");
        } else if(openbtn === "Edit") {
            console.log("#############################");
            console.log("this.props.selectedKeyName", this.props.selectedKeyName );
            if(this.props.selectedKeyName === "selectGenre") {
                this.props.setErrorStatus(Define.GENRE_SET_FAIL_NOT_SELECT_GENRE);
                API.dispAlert(Define.GENRE_SET_FAIL_NOT_SELECT_GENRE);
                return;
            }
        }
        this.openModal();
    };

    actionFunc = async (openbtn) => {
        let selectedKeyName = "";

        if(openbtn === "Create"){
            selectedKeyName = this.props.genreName;
        } else if(openbtn === "Edit"){
            selectedKeyName = this.props.selectedKeyName;
        }

        //call async functionn
        const result = await this.props.confirmFunc(this.props.genreName, selectedKeyName);
        if(result === Define.RSS_SUCCESS){
            this.props.setErrorStatus(Define.RSS_SUCCESS);
            this.closeModal();
            this.props.handleSelectBoxChange(this.props.genreName);
        } else {
            this.props.setErrorStatus(result);
            API.dispAlert(result);

        }
    };

    render() {
        const {
            title,
            openbtn,
            inputname,
            inputpholder,
            leftbtn,
            rightbtn,
        } = this.props;
        const { isOpen } = this.state;

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
                {isOpen ? (
                    <ReactTransitionGroup
                        transitionName={"Custom-modal-anim"}
                        transitionEnterTimeout={200}
                        transitionLeaveTimeout={200}
                    >
                        <div className="Custom-modal-overlay" onClick={this.closeModal} />
                        <div className="Custom-modal">
                            <p className="title">{title}</p>
                            <div className="content-with-title">
                                <FormGroup style={{ marginTop: "1rem" }}>
                                    <Input
                                        type="text"
                                        name={inputname}
                                        value={this.props.genreName}
                                        placeholder={inputpholder}
                                        className="catlist-input"
                                        onChange={(e) => this.props.onChangeGenreName(e.target.value)}
                                    />
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

export default InputModal;