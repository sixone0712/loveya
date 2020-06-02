import React, { Component } from "react";
import {Button, Input, UncontrolledPopover, PopoverHeader, PopoverBody } from "reactstrap";
import ReactTransitionGroup from "react-addons-css-transition-group";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faExclamationCircle, faExclamation} from "@fortawesome/free-solid-svg-icons";
import * as Define from '../../../define';
import * as API from "../../../api";
import services from '../../../services'
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as genreListActions from "../../../modules/genreList";

class InputModal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            inputOpen: false,
            alertOpen: false,
            errMsg: "",
            alertMsg: "",
            genreName: ""
        };
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        console.log("getDerivedStateFromProps");
        const { genreList } = nextProps;
        const { isServerErr } = genreList.toJS();
        console.log("isServerErr", isServerErr);
        console.log("nextProps.nowAction", nextProps.nowAction);
        if(isServerErr && nextProps.nowAction === nextProps.openbtn) {
            return {
                ...prevState,
                inputOpen: false,
                alertOpen: true,
                errMsg: "",
                alertMsg: API.convertErrMsg(Define.GENRE_SET_FAIL_SEVER_ERROR)
            }
        }
        return prevState;
    }

    openInputModal = () => {
        this.setState({
            ...this.state,
            inputOpen: true
        });
    };

    closeInputModal = () => {
        this.setState({
            inputOpen: false,
            alertOpen: false,
            errMsg: "",
            alertMsg: "",
            genreName: ""
        });
    };

    openAlertModal = async (val) => {
        await this.setState({
            ...this.state,
            alertOpen: true
        });
    };

    closeAlertModal = async () => {
        const { genreListActions, handleSelectBoxChange } = this.props;
        handleSelectBoxChange(0);
        await genreListActions.genreInitServerError();
        await this.setState({
            ...this.state,
            inputOpen: false,
            alertOpen: false
        });
    };

    canOpenModal = async (openbtn) => {
        console.log("canOpenModal");
        await this.props.setNowAction(openbtn)

        if(openbtn === "Create") {
            if(this.props.logInfoListCheckCnt <= 0){
                await this.setState({
                    ...this.state,
                    alertMsg: API.convertErrMsg(Define.GENRE_SET_FAIL_NO_ITEM)
                });
                this.openAlertModal();
                return;
            }

            await this.setState({
                genreName: ""
            })
        } else if(openbtn === "Edit") {
            if(this.props.selectedGenre === 0) {
                await this.setState({
                    ...this.state,
                    alertMsg: API.convertErrMsg(Define.GENRE_SET_FAIL_NOT_SELECT_GENRE)
                });
                this.openAlertModal();
                return;
            }

            if (this.props.logInfoListCheckCnt <= 0){
                await this.setState({
                    ...this.state,
                    alertMsg: API.convertErrMsg(Define.GENRE_SET_FAIL_NO_ITEM)
                });
                this.openAlertModal();
                return;
            }

            await this.setState({
                genreName: this.props.selectedGenreName
            })
        }
        this.openInputModal();
    };

    actionFunc = async (openbtn) => {
        const genreName = this.state.genreName;
        const nameRegex = /^([\p{L}0-9]).{1,18}([\p{L}0-9]$)/gu;

        if(genreName.length === 0) {
            this.setState({
                errMsg: "Please input genre name."
            });
            return;
        }

        if(!nameRegex.test(genreName)) {
            this.setState({
                errMsg: "Genre name is invalid. Please re-enter."
            });
            return;
        }

        //call async function
        const selectedId = this.props.selectedGenre;
        const result = await this.props.confirmFunc(selectedId, genreName);
        let alertMsg = "";
        if(result === Define.RSS_SUCCESS){
            this.closeInputModal();
            const reflectingID = this.props.getSelectedIdByName(genreName);
            this.props.handleSelectBoxChange(reflectingID);
        } else {
            await this.setState({
                ...this.state,
                errMsg: API.convertErrMsg(result),
                alertMsg: API.convertErrMsg(result),
            });

            if(result === Define.GENRE_SET_FAIL_NOT_EXIST_GENRE) {
                const { genreListActions } = this.props;
                await genreListActions.genreGetDbList(Define.REST_API_URL + "/genre/get");
                setTimeout(() => {
                    this.openAlertModal();
                }, 400);
            }
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
                                <div className="genre-name-input-area">
                                    <Input
                                        type="text"
                                        id={inputname}
                                        value={this.state.genreName}
                                        placeholder={inputpholder}
                                        maxLength="20"
                                        className="catlist-modal-input"
                                        //onChange={(e) => this.props.onChangeGenreName(e.target.value)}
                                        onChange={(e) => this.setState({...this.state, genreName: e.target.value})}
                                    />
                                    <UncontrolledPopover
                                        placement="top-end"
                                        target={inputname}
                                        className="catlist"
                                        trigger="hover"
                                        delay={{ show: 300, hide: 0 }}
                                    >
                                        <PopoverHeader>Genre Name</PopoverHeader>
                                        <PopoverBody>
                                            <p>
                                                <FontAwesomeIcon icon={faExclamation} />{" "}
                                                There are no restrictions on the types of characters that can be entered.
                                            </p>
                                            <p>
                                                <FontAwesomeIcon icon={faExclamation} />{" "}
                                                Special characters cannot be entered at the beginning or end.
                                            </p>
                                            <p>
                                                <FontAwesomeIcon icon={faExclamation} />{" "}
                                                Allowed to be at least 3 characters long and up to 20 characters long.
                                            </p>
                                        </PopoverBody>
                                    </UncontrolledPopover>
                                    <span className="error">{errMsg}</span>
                                </div>
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
export default connect(
    (state) => ({
        genreList: state.genreList.get('genreList'),
    }),
    (dispatch) => ({
        genreListActions: bindActionCreators(genreListActions, dispatch),
    })
)(InputModal);