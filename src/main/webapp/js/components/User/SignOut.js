import React, {Component} from 'react';
import { connect } from 'react-redux'
import * as API from "../../api";
import * as Define from "../../define";
import ErrorModalOneButton from "../Common/ErrorModal";
import ReactTransitionGroup from "react-addons-css-transition-group";
import UserAuthFrom from "../Form/UserAuthForm";
import InputForm from "../Form/InputForm";
import {bindActionCreators} from "redux";
import * as userActions from "../../modules/User";


class SignOut extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isModalOpen: false,
            uInfo:{
                name:'',
                pwd:'',
                cfpwd:'',
                authValue:'100',
            },
            errors: {
                name: '',
                pwd: '',
                cfpwd: '',
                ModalMsg: '',
            },
        };
        this.handleRadio = this.handleRadio.bind(this);
        this.handleInput = this.handleInput.bind(this);
        this.SignOutProcess = this.SignOutProcess.bind(this);
    }

    openModal = () => {
        this.setState(() => ({isModalOpen: true}));
    }
    closeModal = () => {
        this.setState(() => ({isModalOpen: false}));
    }

    SignOutProcess = async (e) => {
        const {uInfo} = this.state;

        this.handleInput("name", uInfo.name);
        this.handleInput("pwd", uInfo.pwd);
        this.handleInput("cfpwd", uInfo.cfpwd);

        if (uInfo.name.length === 0 || uInfo.pwd.length === 0 || uInfo.cfpwd.length === 0) {
            console.log();
        } else if (uInfo.pwd !== uInfo.cfpwd) {
            this.setState(() => (
                {
                    ...this.state,
                    errors: {
                        ...this.state.errors,
                        pwd: API.getErrorMsg(Define.CHANGE_PW_FAIL_NOT_MATCH_NEW_PASSWORD)
                    }
                })
            );
        } else {
            let result = 0;
            await API.createUser(this.props, uInfo);
            result = API.getUserInfoErrorCode(this.props);
            console.log("result:" + result);
            if(result !== 0)
            {
                this.setState(() => (
                    {
                        ...this.state,
                        errors: {
                            ...this.state.errors,
                            pwd: API.getErrorMsg(result)
                        }
                    })
                );
            }
            else
            {
                await API.getDBUserList(this.props);
                this.close(); //create modal Close
                this.props.alertOpen("create");
            }
        }
    }
    handleRadio = (value) => {
        this.setState(() => (
            {...this.state,
                uInfo : {
                ...this.state.uInfo,
                authValue : value
                }
            })
        );
    }
    handleInput = (name,value) => {
        let nState = this.state;

        switch (name) {
            case 'name':
                nState.uInfo.name = value;
                nState.errors.name =
                    value.length < 1
                        ? 'Please enter your name'
                        : '';
                break;
            case 'pwd':
                nState.uInfo.pwd = value;
                nState.errors.pwd =
                    value.length < 1
                        ? 'Please enter the password'
                        : '';
                break;
            case 'cfpwd':
                nState.uInfo.cfpwd = value;
                nState.errors.cfpwd =
                    value.length < 1
                        ? 'Please enter the confirm password'
                        : '';
                break;
            default:
                break;
        }
        this.setState({...nState});
    };
    data = {
        titleMsg:'Create Account'
    };
    close = () => {
        this.setState(() => (
            {...this.state,
                error:{},
            })
        );
        this.props.right();
    }
    render() {
        const {errors, uInfo} = this.state;
        const {isOpen, right} = this.props;
        {
            return (
                <>
                    {
                        isOpen ? (
                            <ReactTransitionGroup
                                transitionName={'Custom-modal-anim'}
                                transitionEnterTimeout={200}
                                transitionLeaveTimeout={200}>
                                <div className="Custom-modal-overlay" onClick={right} />
                                <div className="Custom-modal">
                                    <p className="title font-lg">{this.data.titleMsg}</p>
                                    <div className="content-with-title user-modal">
                                        <InputForm iType ={"text"}
                                                   iName={"name"}
                                                   iLabel={"YOUR NAME"}
                                                   iPlaceholder ={"Enter the name"}
                                                   changeFunc={this.handleInput}
                                                   iErrMsg ={errors.name}
                                        />

                                        <InputForm iType ={"password"}
                                                   iName={"pwd"}
                                                   iLabel={"PASSWORD"}
                                                   iPlaceholder ={"Enter the password."}
                                                   changeFunc={this.handleInput}
                                                   iErrMsg ={errors.pwd}
                                         />
                                        <InputForm iType ={"password"}
                                                   iName={"cfpwd"}
                                                   iLabel={"CONFIRM PASSWORD"}
                                                   changeFunc={this.handleInput}
                                                   iPlaceholder ={"Enter the confirm password."}
                                                   iErrMsg ={errors.cfpwd}
                                        />
                                        <UserAuthFrom  sValue={uInfo.authValue}
                                                       changeFunc={this.handleRadio}/>

                                    </div>
                                    <div className="button-wrap no-margin">
                                        <button className="gray form-type left-btn" onClick={this.SignOutProcess}>
                                            Save
                                        </button>
                                        <button className="gray form-type right-btn" onClick={this.close}>
                                            Cancel
                                        </button>
                                    </div>
                                </div>

                                {
                                    (this.state.isModalOpen === true) &&
                                    <ErrorModalOneButton isOpen={this.state.isModalOpen}
                                                         errorMsg={this.state.errors.ModalMsg}
                                                         ActionCloseButton={this.closeModal}/>
                                }
                            </ReactTransitionGroup>
                        ) : (
                            <ReactTransitionGroup transitionName={'Custom-modal-anim'} transitionEnterTimeout={200}
                                                  transitionLeaveTimeout={200}/>
                        )
                    }
                </>
            );
        }
    }
}

export default connect(

    (state) => ({
        userInfo : state.user.get('UserInfo'),
    }),
    (dispatch) => ({
        userActions: bindActionCreators(userActions, dispatch),
    })
)(SignOut);