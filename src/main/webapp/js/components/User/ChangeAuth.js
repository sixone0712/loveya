import React, {Component} from "react"
import ReactTransitionGroup from 'react-addons-css-transition-group';
import * as API from "../../api";
import ErrorModalOneButton from "../Common/ErrorModal";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as loginActions from "../../modules/login";
import UserAuthFrom from "../Form/UserAuthForm";
import * as userActions from "../../modules/User";

class ChangeAuthModal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isModalOpen : false,
            selectedValue: "",
            errors: {
                ModalMsg:''
            }
        };
        this.handleRadio = this.handleRadio.bind(this);
    }

    changePermissionProcess = async id => {
        console.log("changePermission");
        await API.changePermission(this.props, `/user/changeAuth?id=${id}&permission=${(this.state.selectedValue)}`);
        const err = API.getErrCode(this.props);
        console.log("changePermission err: ", err);
        if (!err) {
            await API.getDBUserList(this.props);//user list refresh
            this.props.right(); //pw change modal Close
            this.props.alertOpen("permission");
        } else {
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
        this.setState(() => ({...this.state,isModalOpen: false}));
    }
    handleRadio = (value) => {
        this.setState(() => ({...this.state, selectedValue : value}));
    }

    data = {
        titleMsg:'Change the Permission'
    };
    render() {
        const { isOpen, right, userID } = this.props;
        const selected = (this.state.selectedValue ==='') ? API.getUserAuth(this.props,userID) : this.state.selectedValue;

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
                                    <UserAuthFrom  sValue={selected}
                                                   changeFunc={this.handleRadio}/>
                                </div>
                                <div className="button-wrap no-margin">
                                    <button className="gray form-type left-btn" onClick={()=>this.changePermissionProcess(userID)}>
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
        loginInfo: state.login.get('loginInfo'),
        UserList : state.user.get('UserList'),
        userInfo: state.user.get('UserInfo'),
    }),
    (dispatch) => ({
        loginActions: bindActionCreators(loginActions, dispatch),
        userActions: bindActionCreators(userActions, dispatch),

    })
)(ChangeAuthModal);