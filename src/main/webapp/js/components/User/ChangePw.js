import React, {Component} from "react"
import ReactTransitionGroup from 'react-addons-css-transition-group';
import * as API from "../../api";
import * as Define from "../../define";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as loginActions from "../../modules/login";
import {PopoverBody, PopoverHeader, UncontrolledPopover} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faExclamation} from "@fortawesome/free-solid-svg-icons";

class ChangePwModal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            oldPw: '',
            newPw: '',
            confirmPw: '',
            errors: {
                oldPw: '',
                newPw: ''
            }
        };
    }

    handleSubmit = () => {
        const { oldPw,newPw, confirmPw } = this.state;
        const passwordRegex = /[0-9a-zA-Z]{6,30}/g;
        if (oldPw.length === 0) {
            this.setState({
                errors: {
                    oldPw: "Please enter the current password.",
                }
            });
            return true;
        }
        else if (newPw.length === 0 || !passwordRegex.test(newPw)) {
            this.setState({
                errors: {
                    newPw: "New password is invalid."
                }
            });
            return true;
        } else if (newPw !== confirmPw) {
            this.setState({
                errors: {
                    newPw: API.getErrorMsg(Define.CHANGE_PW_FAIL_NOT_MATCH_NEW_PASSWORD)
                }
            });
            return true;
        } else {
            this.setState({
                errors: {
                    oldPw: "",
                    newPw: ""
                }
            });
            return false;
        }
    }

    changePwProcess = async () => {
        console.log("changePwProcess");
        const isError = this.handleSubmit();
        console.log("isError: " + isError);

        if (!isError) {
            try {
                await API.changePassword(this.props, this.state);
            } catch (e) {
                console.error(e);
            }
            let  errCode = API.getErrCode(this.props);
            if(errCode)
            {
                this.setState(() => (
                    {  ...this.state,
                        errors: {
                            ...this.state.errors,
                            oldPw: API.getErrorMsg(errCode)
                        }
                    })
                );
            }
            else {
                this.closeModal(); //pw change modal Close
                this.props.alertOpen("password");
            }
        }
    }

    changeHandler = (e) => {
        const { name, value } = e.target;
        this.setState({ [name]: value });
    };

    closeModal = () => {
        this.setState({
            oldPw: '',
            newPw: '',
            confirmPw: '',
            errors: {
                oldPw: '',
                newPw: ''
            }
        });
        this.props.right();
    };

    render() {
        const { isOpen } = this.props;
        const { errors } = this.state;

        return (
            <>
                {isOpen ? (
                    <ReactTransitionGroup
                        transitionName={"Custom-modal-anim"}
                        transitionEnterTimeout={200}
                        transitionLeaveTimeout={200}
                    >
                        <div className="Custom-modal-overlay" onClick={this.closeModal} />
                        <div className="Custom-modal">
                            <p className="title font-lg">
                                Change Password
                            </p>
                            <div className="content-with-title user-modal">
                                <div className="password-input-area">
                                    <label>Current Password</label>
                                    <input
                                        type="password"
                                        name="oldPw"
                                        placeholder={"Enter current password."}
                                        autoComplete="off"
                                        onChange={this.changeHandler}
                                    />
                                    <span className={"error" + (typeof(errors.oldPw) === "undefined" ? "" : errors.oldPw.length > 0 ? " .active" : "")}>{errors.oldPw}</span>
                                </div>
                                <div className="password-input-area">
                                    <label>New Password</label>
                                    <input
                                        type="password"
                                        name="newPw"
                                        id="newPw"
                                        placeholder={"Enter new password."}
                                        autoComplete="off"
                                        onChange={this.changeHandler}
                                    />
                                    <UncontrolledPopover
                                        placement="top-end"
                                        target="newPw"
                                        trigger="hover"
                                        delay={{ show: 300, hide: 0 }}
                                    >
                                        <PopoverHeader>Password</PopoverHeader>
                                        <PopoverBody>
                                            <p>
                                                <FontAwesomeIcon icon={faExclamation} />{" "}
                                                Characters that can be entered: alphabet, number.
                                            </p>
                                            <p>
                                                <FontAwesomeIcon icon={faExclamation} />{" "}
                                                Allowed to be at least 6 characters long and up to 30 characters long.
                                            </p>
                                        </PopoverBody>
                                    </UncontrolledPopover>
                                    <span className={"error" + (typeof(errors.newPw) === "undefined" ? "" : errors.newPw.length > 0 ? " .active" : "")}>{errors.newPw}</span>
                                </div>
                                <div className="password-input-area">
                                    <label>Confirm Password</label>
                                    <input
                                        type="password"
                                        name="confirmPw"
                                        id="confirmPw"
                                        placeholder={"Enter confirm password."}
                                        autoComplete="off"
                                        onChange={this.changeHandler}
                                    />
                                </div>
                            </div>
                            <div className="button-wrap">
                                <button className="gray form-type left-btn" onClick={this.changePwProcess}>
                                    Save
                                </button>
                                <button className="gray form-type right-btn" onClick={this.closeModal}>
                                    Cancel
                                </button>
                            </div>
                        </div>
                    </ReactTransitionGroup>
                    ): (
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
        loginInfo : state.login.get('loginInfo'),
    }),
    (dispatch) => ({
        loginActions: bindActionCreators(loginActions, dispatch),
    })
)(ChangePwModal);