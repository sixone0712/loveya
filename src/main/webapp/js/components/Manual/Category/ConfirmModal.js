import React, { Component } from "react";
import {Button} from "reactstrap";
import ReactTransitionGroup from "react-addons-css-transition-group";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faExclamationCircle, faTrashAlt} from "@fortawesome/free-solid-svg-icons";
import * as API from "../../../api";
import * as Define from "../../../define";
import * as genreListActions from "../../../modules/genreList";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import AlertModal from "../../Common/AlertModal";

class ConfirmModal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            confirmOpen: false,
            alertOpen: false,
            errMsg: "",
        };
      }

    static getDerivedStateFromProps(nextProps, prevState) {
        console.log("getDerivedStateFromProps");
        const { genreList } = nextProps;
        const { isServerErr } = genreList.toJS();
        console.log("isServerErr", isServerErr);
        console.log("nextProps.nowAction", nextProps.nowAction);
        if(isServerErr && nextProps.nowAction == nextProps.openbtn) {
            return {
                confirmOpen: false,
                alertOpen: true,
                errMsg: API.convertErrMsg(Define.GENRE_SET_FAIL_SEVER_ERROR),
              }
        }
        return prevState;
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

    closeAlertModal = async () => {
        console.log("closeAlertModal Start", this.state.alertOpen);
        const { genreListActions, handleSelectBoxChange } = this.props;
        handleSelectBoxChange(0);
        await genreListActions.genreInitServerError();
        await this.setState({
            ...this.state,
            alertOpen: false
        });
        console.log("closeAlertModal end", this.state.alertOpen);
    };

    canOpenModal = async (openbtn) => {
        await this.props.setNowAction(openbtn);
        if(this.props.selectedGenre === 0) {
            await this.setState({
                errMsg : API.convertErrMsg(Define.GENRE_SET_FAIL_NOT_SELECT_GENRE)
            })
            this.openAlertModal();
            return;
        }
        this.openConfirmModal();
    };

    actionFunc = async () => {
        console.log("[ConfirmModel.js] actionFunc");
        //call async functionn
        const result = await this.props.confirmFunc(this.props.selectedGenre);
        console.log("result", result);

        if(result === Define.RSS_SUCCESS){
            this.closeConfirmModal();
            this.props.handleSelectBoxChange(0);
        } else {
            await this.setState({
                errMsg : API.convertErrMsg(result)
            })

            if(result === Define.GENRE_SET_FAIL_NOT_EXIST_GENRE) {
                const { genreListActions } = this.props;
                await genreListActions.genreGetDbList(Define.REST_API_URL + "/genre/get");
            }
            this.closeConfirmModal();
            setTimeout(() => {
                this.openAlertModal();
            }, 400);
            //this.props.handleSelectBoxChange(0);
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
                    onClick={() => this.canOpenModal(openbtn)}
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
                <AlertModal isOpen={alertOpen} icon={faExclamationCircle} message={this.state.errMsg} style={"primary"} closer={this.closeAlertModal} />
            </>
        );
    }
}

export default connect(
    (state) => ({
        genreList: state.genreList.get('genreList'),
    }),
    (dispatch) => ({
        genreListActions: bindActionCreators(genreListActions, dispatch),
    })
)(ConfirmModal);