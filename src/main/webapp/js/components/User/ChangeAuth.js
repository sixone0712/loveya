import React, {Component} from "react"
import ReactTransitionGroup from 'react-addons-css-transition-group';
import * as API from "../../api";
import ErrorModalOneButton from "../Common/ErrorModal";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as loginActions from "../../modules/login";

class ChangeAuthModal extends Component {
    constructor(props) {
        super(props);
        this.handleRadio = this.handleRadio.bind(this);
        this.state = {
            isModalOpen : false,
            selectedValue: '10',
            errors: {
                ModalMsg:''
            }
        };
        this.state.selectedValue = window.sessionStorage.getItem("auth");

    }

    data = {
        titleMsg:'Change the Permission',
        Auth_10_Msg:'10',
        Auth_10_Detail:'Only file collection available',
        Auth_20_Msg:'20',
        Auth_20_Detail:'Only EE data viewer is available',
        Auth_50_Msg:'50',
        Auth_50_Detail:'Both file collection and EE data viewer available',
        Auth_100_Msg:'100',
        Auth_100_Detail:'Administrators only',
    };

    changePermissionProcess = async(e) => {
        console.log("changePermission");
        let username = window.sessionStorage.getItem("username");
        await API.changePermission(this.props, `/user/changeAuth?username=${username}&permission=${(this.state.selectedValue)}`);
        const err = API.getErrCode(this.props);
        console.log("changePermission err: ", err);
        if(!err)
        {
            window.sessionStorage.setItem('auth', this.state.selectedValue);
            API.setLoginAuth(this.props, this.state.selectedValue);
            this.props.right(); //pw change modal Close
            this.props.alertOpen("permission");
        }
        else
        {
            const msg = API.getErrorMsg(err);
            console.log("changePermission msg: ", msg);
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
    closeModal = () => {
        this.setState(() => ({isModalOpen: false}));

    }
    handleRadio(event) {
        this.setState({selectedValue: event.target.value});
    }
    render() {
        const { isOpen, right } = this.props;
        return (
            <>
                {
                    isOpen ? (
                        <ReactTransitionGroup
                            transitionName={'Custom-modal-anim'}
                            transitionEnterTimeout={200}
                            transitionLeaveTimeout={200} >
                            <div className="Custom-modal-overlay" onClick={right} />
                            <div className="Custom-modal">
                                <p className="title">{this.data.titleMsg}</p>
                                <div className="content-with-title user-modal">
                                    <div className="Custom-ratio">
                                        <input type="radio" id="auth_10" value="10" checked={this.state.selectedValue === '10'} onChange={this.handleRadio} />
                                        <label htmlFor="auth_10">
                                            <h2>{this.data.Auth_10_Msg}</h2>
                                            <div>{this.data.Auth_10_Detail}</div>
                                        </label>
                                        <input type="radio" id="auth_20" value="20" checked={this.state.selectedValue === '20'} onChange={this.handleRadio} />
                                        <label htmlFor="auth_20">
                                            <h2>{this.data.Auth_20_Msg}</h2>
                                            <div>{this.data.Auth_20_Detail}</div>
                                        </label>
                                        <input type="radio" id="auth_50" value="50" checked={this.state.selectedValue === '50'} onChange={this.handleRadio} />
                                        <label htmlFor="auth_50">
                                            <h2>{this.data.Auth_50_Msg}</h2>
                                            <div>{this.data.Auth_50_Detail}</div>
                                        </label>
                                        <input type="radio" id="auth_100" name="select" value="100" checked={this.state.selectedValue === '100'}  onChange={this.handleRadio} />
                                        <label htmlFor="auth_100">
                                            <h2>{this.data.Auth_100_Msg}</h2>
                                            <div>{this.data.Auth_100_Detail}</div>
                                        </label>
                                    </div>
                                </div>
                                <div className="button-wrap no-margin">
                                    <button className="gray form-type left-btn" onClick={this.changePermissionProcess}>
                                        Save
                                    </button>
                                    <button className="gray form-type right-btn" onClick={right}>
                                        Cancel
                                    </button>
                                </div>
                            </div>
                            {
                                (this.state.isModalOpen === true) &&
                                <ErrorModalOneButton  isOpen={this.state.isModalOpen } errorMsg={this.state.errors.ModalMsg} ActionCloseButton={this.closeModal}/>
                            }
                        </ReactTransitionGroup>
                    ):(
                        <ReactTransitionGroup transitionName={'Custom-modal-anim'} transitionEnterTimeout={200} transitionLeaveTimeout={200} />
                    )
                }
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
)(ChangeAuthModal);