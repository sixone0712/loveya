import React, {Component} from 'react';
import { connect } from 'react-redux'
import {bindActionCreators} from "redux";
import * as loginActions from '../../modules/login';
import * as API from "../../api";
import * as Define from "../../define";

class Login extends Component {
    render() {
        console.log(this.props.history);
        return (
            <>
                <div style={{textAlign: "center"}}>
                    <div style={{fontsize: 40, marginTop: 400}}>Login Page</div>
                    <button
                        onClick={() => {
                            API.setLoginIsLoggedIn(this.props,true);
                            window.sessionStorage.setItem('isLoggedIn', true);
                            this.props.history.push(Define.PAGE_MANUAL);
                        }}
                    >
                    Login</button>
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