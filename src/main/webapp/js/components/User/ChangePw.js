import React, {Component} from "react"
import '../../../css/user.css'
import ReactTransitionGroup from 'react-addons-css-transition-group';
import * as API from "../../api";
import ModalTwoButton from "../Common/ModalTwoButton";
import ErrorModalOneButton from "../Common/ErrorModal";
import * as Define from "../../define";
import md5 from "md5-hash";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as loginActions from "../../modules/login";


class ChangePwModal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isModalOpen : false,
            oldPw: '',
            newPw: '',
            confirmPw: '',
            errors: {
                oldPw: '',
                newPw: '',
                confirmPw: '',
                ModalMsg:''
            }
        };
    }

    data = {
        titleMsg:'Change the password',
        OldPwMsg:'Current Password',
        NewPwMsg:'New Password',
        ConfirmPwMsg:'New Confirm Password',
    };
    buttonMsg = {
        leftMsg:'Cancel',
        rightMsg:'Save',
    };
    handleSubmit = (event) => {
        let originalPw = window.sessionStorage.getItem('password');
        return  ((this.state.oldPw.length > 0 && this.state.newPw.length > 0 && this.state.confirmPw.length > 0)
                ? (this.state.oldPw === originalPw ? (this.state.newPw === this.state.confirmPw
                        ? 0 : Define.CHANGE_PW_FAIL_NOT_MATCH_NEW_PASSWORD)
                    : Define.CHANGE_PW_FAIL_INCCORECT_CURRNET_PASSWORD)
                : Define.CHANGE_PW_FAIL_EMPTY_PASSWORD)
    }
    changePwProcess = (e) => {
        console.log("changePwProcess");
        let errCode = this.handleSubmit(e);
        console.log("ErrorCode",errCode);
        if(!errCode)
        {
            let username = window.sessionStorage.getItem('username');
           API.changePassword(this.props, `/user/changePw?username=${username}&password=${md5(this.state.newPw)}`);
               window.sessionStorage.setItem('password', this.state.newPw);
            API.setLoginPassword(this.props, this.state.newPw);
           this.props.right(); //pw change modal Close
        }else
        {
            console.log("loginProcess errCode : " + errCode);
            const  ModalMsg = API.getErrorMsg(errCode);
            if(ModalMsg.length>0)
            {
                this.state.errors.ModalMsg = ModalMsg;
                this.openModal();
            }
        }
    }

    openModal = () => {
        this.setState(() => ({isModalOpen: true}));
    }
    closeModal = () => {
        this.setState(() => ({isModalOpen: false}));

    }

    changeHandler = e => {
        const { name, value } = e.target;
        let errors = this.state.errors;

        switch (name) {
            case 'oldPw':
                errors.oldPw =
                    value.length < 1
                        ? 'Please enter your password'
                        : '';
                break;
            case 'newPw':
                errors.newPw =
                    value.length < 1
                        ? 'Please enter new password'
                        : '';
                break;
            case 'confirmPw':
                errors.confirmPw =
                    value.length < 1
                        ? 'Please enter confirm password'
                        : '';
                break;
            default:
                break;
        }
        this.setState({errors, [name]: value});
    };

    render() {
        const { isOpen, right } = this.props;
        const {errors} = this.state;

        console.log("isOpen ", this.props.isOpen);
        return (
            <React.Fragment>
                {
                    {isOpen} ? (
                        <ReactTransitionGroup
                            transitionName={'Modal-anim'}
                            transitionEnterTimeout={200}
                            transitionLeaveTimeout={200} >
                            <div className="Custom-modal-overlay" onClick={close} />
                            <div className="Custom-modal">
                                <p className="title">{this.data.titleMsg}</p>
                                <div className="border border-solid border-gray-900"/>
                                <p className="content">
                                    <div className="relative w-full mb-3">
                                        <label
                                            className="block text-gray-700 text-s font-bold mb-2"
                                            htmlFor="grid-password"
                                        >
                                            {this.data.OldPwMsg}
                                        </label>
                                        <input
                                            type = "password"
                                            name = "oldPw"
                                            className="px-3 py-3 placeholder-gray-400 text-gray-700 border-solid bg-gray-200 rounded text-sm shadow focus:shadow-outline w-full"
                                            placeholder="Enter old password"
                                            style={{ transition: "all .15s ease" }}
                                            onChange={this.changeHandler} noValidate
                                            autoComplete="off"
                                        />
                                        {errors.oldPw.length > 0 &&
                                        <span className="text-red-700 font-bold text-xxs">{errors.oldPw}</span>}
                                    </div>
                                    <div className="relative w-full mb-3">
                                        <label
                                            className="block text-gray-700 text-s font-bold mb-2"
                                            htmlFor="grid-password"
                                        >
                                            {this.data.NewPwMsg}
                                        </label>
                                        <input
                                            type = "password"
                                            name = "newPw"
                                            className="px-3 py-3 placeholder-gray-400 text-gray-700 border-solid bg-gray-200 rounded text-sm shadow focus:shadow-outline w-full"
                                            placeholder="Enter new password"
                                            style={{ transition: "all .15s ease" }}
                                            onChange={this.changeHandler} noValidate
                                            autoComplete="off"
                                        />
                                        {errors.newPw.length > 0 &&
                                        <span className="text-red-700 font-bold text-xxs">{errors.newPw}</span>}
                                    </div>
                                    <div className="relative w-full mb-3">
                                        <label
                                            className="block text-gray-700 text-s font-bold mb-2"
                                            htmlFor="grid-password"
                                        >
                                            {this.data.ConfirmPwMsg}
                                        </label>
                                        <input
                                            type = "password"
                                            name = "confirmPw"
                                            className="px-3 py-3 placeholder-gray-400 text-gray-700 bg-gray-200 rounded text-sm shadow focus:shadow-outline w-full"
                                            placeholder="Enter confirm password"
                                            style={{ transition: "all .15s ease" }}
                                            onChange={this.changeHandler} noValidate
                                            autoComplete="off"
                                        />
                                        {errors.confirmPw.length > 0 &&
                                        <span className="text-red-700 font-bold text-xxs">{errors.confirmPw}</span>}
                                    </div>
                                </p>
                                <ModalTwoButton data={this.buttonMsg} actionLeftFunc={this.changePwProcess} actionRightFunc={right} />
                            </div>
                            {this.state.isModalOpen &&
                                <ErrorModalOneButton isOpen={this.state.isModalOpen} errorMsg={this.state.errors.ModalMsg}
                                                 ActionCloseButton={this.closeModal}/>
                            }
                        </ReactTransitionGroup>
                    ):(
                        <ReactTransitionGroup transitionName={'Modal-anim'} transitionEnterTimeout={200} transitionLeaveTimeout={200} />
                    )
                }
            </React.Fragment>
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