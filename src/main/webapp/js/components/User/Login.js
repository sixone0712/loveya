import React, {Component} from 'react';
import { connect } from 'react-redux'
import {bindActionCreators} from "redux";
import * as loginActions from '../../modules/login';
import * as API from "../../api";
import * as Define from "../../define";
import "../../../css/user.css";
import md5 from 'md5-hash'
import AlertModal from "../Common/AlertModal";
import { faExclamationCircle } from "@fortawesome/free-solid-svg-icons";


class Login extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isModalOpen : false,
            username: '',
            password: '',
            errors: {
                username: '',
                password: '',
                ModalMsg: '',
            }
        };

        this.loginProcess = this.loginProcess.bind(this);
    }
    openModal = () => {
        this.setState(() => ({isModalOpen: true}));
    }
    closeModal = () => {
        this.setState(() => ({isModalOpen: false}));
    }
    handleSubmit = () => {
        return ((this.state.username.length > 0 && this.state.password.length > 0)
                ? 0 : Define.LOGIN_FAIL_EMPTY_USER_PASSWORD);
    }

    handleEnter = (e) => {
        const { isModalOpen } = this.state;

        if (!isModalOpen) {
            if (e.key === "Enter") {
                e.target.blur();
                e.preventDefault();
                e.stopPropagation();
                this.loginProcess();
            }
        }
    }

    loginProcess =async(e) => {
        let errCode = this.handleSubmit();
        if (!errCode)
        {
            await API.startLoginAuth(this.props, `${Define.REST_API_URL}/user/login?user=${this.state.username}&password=${md5(this.state.password)}`);
            const isLoggedIn = API.getLoginIsLoggedIn(this.props);
            const errCode = API.getErrCode(this.props);
            console.log("isLoggedIn", isLoggedIn);
            console.log("errCode", errCode);
            if (isLoggedIn) {
                this.props.history.replace(Define.PAGE_MANUAL);
            } else {
                const msg = API.getErrorMsg(errCode);
                if (msg.length > 0) {
                    this.setState({
                        ...this.state,
                        isModalOpen: true,
                        username: '',
                        password: '',
                        errors: {
                            ...this.state.errors,
                            ModalMsg: msg
                        }
                    })
                }

            }
        } else {
            const msg = API.getErrorMsg(errCode);
            if (msg.length > 0) {
                this.setState({
                    ...this.state,
                    isModalOpen: true,
                    errors: {
                        ...this.state.errors,
                        ModalMsg: msg
                    }
                })
            }
        }
    }

    handleChange = e => {
        const { name, value } = e.target;
        let nState = this.state;

        switch (name) {
            case 'username':
                nState.username =value;
                nState.errors.username =
                    value.length < 1
                        ? 'Please enter your name'
                        : '';
                break;
            case 'password':
                nState.password = value;
                nState.errors.password =
                    value.length < 1
                        ? 'Please enter your password'
                        : '';
                break;
            default:
                break;
        }
        this.setState({ ...nState});
    };
    render() {
        const {errors} = this.state;
        return (
            <>
                <div>
                    <section className="absolute w-full h-full">
                        <div
                            className="absolute top-0 w-full h-full bg-gray-900"
                            style={{
                                backgroundSize: "100%",
                                background: "linear-gradient(135deg, #b3cae5 12%, #dbdde4 46%, #e4e3e4 70%, #f7ddbb 94%, #efcab2 100%)",
                                backgroundRepeat: "no-repeat",
                            }}
                        />
                        <div className="container mx-auto px-4 h-full">
                            <div className="flex content-center items-center justify-center h-full">

                                <div className="w-full lg:w-4/12 px-4">
                                    <div className="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-gray-300 border-0">
                                        <div className="flex-auto px-4 lg:px-10 py-10 pt-10">
                                            <div className="text-gray-700 text-xl text-center font-semibold">
                                                Log-in to your account
                                            </div>
                                            <hr className="mt-6 border-b-1 border-gray-400" />
                                            <form >
                                                <div className="relative w-full mb-3">
                                                    <label
                                                        className="block uppercase text-gray-700 text-xs font-bold mb-2"
                                                        htmlFor="grid-password"
                                                    >
                                                        UserName
                                                    </label>
                                                    <input
                                                        type = "text"
                                                        name="username"
                                                        id="username"
                                                        value={this.state.username}
                                                        className="px-3 py-3 placeholder-gray-400 text-gray-700 bg-white rounded text-sm shadow focus:shadow-outline w-full"
                                                        placeholder="Enter your name"
                                                        autoComplete="off"
                                                        style={{ transition: "all .15s ease" }}
                                                        onChange={this.handleChange} noValidate
                                                    />
                                                    {errors.username.length > 0 &&
                                                    <span className="text-red-700 uppercase font-bold text-xxs">{errors.username}</span>}
                                                </div>

                                                <div className="relative w-full mb-3">
                                                    <label
                                                        className="block uppercase text-gray-700 text-xs font-bold mb-2"
                                                        htmlFor="grid-password"
                                                    >
                                                        Password

                                                    </label>
                                                    <input
                                                        type = "password"
                                                        name = "password"
                                                        id="password"
                                                        value={this.state.password}
                                                        className="px-3 py-3 placeholder-gray-400 text-gray-700 bg-white rounded text-sm shadow focus:shadow-outline w-full"
                                                        placeholder="Enter your password"
                                                        style={{ transition: "all .15s ease" }}
                                                        onKeyDown={this.handleEnter}
                                                        autoComplete="off"
                                                        onChange={this.handleChange} noValidate
                                                    />
                                                    {errors.password.length > 0 &&
                                                    <span className="text-red-700 uppercase font-bold text-xxs">{errors.password}</span>}
                                                </div>
                                                <div className="text-center mt-6">
                                                    <button
                                                        className="bg-gray-900 text-white active:bg-gray-700 text-sm font-bold uppercase px-6 py-3 rounded shadow hover:shadow-lg outline-none focus:outline-none mr-1 mb-1 w-full"
                                                        type="button"
                                                        style={{ transition: "all .15s ease" }}
                                                        onClick={this.loginProcess}
                                                    >
                                                        Sign In
                                                    </button>
                                                </div>
                                                <AlertModal
                                                    isOpen={this.state.isModalOpen}
                                                    icon={faExclamationCircle}
                                                    message={this.state.errors.ModalMsg}
                                                    style={"gray"}
                                                    closer={this.closeModal}
                                                />
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                </div>
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
)(Login);