import React, {Component} from "react"
import ReactTransitionGroup from 'react-addons-css-transition-group';
import * as API from "../../api";
import * as Define from "../../define";
import md5 from "md5-hash";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as loginActions from "../../modules/login";

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
        const { newPw, confirmPw } = this.state;
        if (newPw.length < 1 ) {
            this.setState({
                errors: {
                    newPw: API.getErrorMsg(Define.CHANGE_PW_FAIL_EMPTY_PASSWORD)
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
            await API.changePassword(this.props, this.state);
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
                                    <span className="error">{errors.oldPw}</span>
                                </div>
                                <div className="password-input-area">
                                    <label>New Password</label>
                                    <input
                                        type="password"
                                        name="newPw"
                                        placeholder={"Enter new password."}
                                        autoComplete="off"
                                        onChange={this.changeHandler}
                                    />
                                    <span className="error">{errors.newPw}</span>
                                </div>
                                <div className="password-input-area">
                                    <label>Confirm Password</label>
                                    <input
                                        type="password"
                                        name="confirmPw"
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