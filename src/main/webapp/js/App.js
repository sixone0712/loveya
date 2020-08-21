import React, {Component} from "react";
import {connect} from 'react-redux'
import {bindActionCreators} from 'redux';
import * as loginActions from './modules/login';
import services from './services'
import * as API from "./api";
import Navbar from "./components/common/Navbar";
import Manual from "./components/Manual/Manual";
import Manual2 from "./components/Manual/ManualVftpCompat";
import Manual3 from "./components/Manual/ManualVftpSss";
import AccountList from "./components/User/UserList";
import DlHistory from "./components/User/DownloadHistory";
import Auto from "./components/Auto/Auto";
import Login from "./components/User/Login";
import MoveRefreshPage from "./components/Common/MoveRefreshPage";
import {Route, Switch} from 'react-router-dom';
import * as Define from "./define";
import NetworkError from "./components/Common/NetworkError";

export let appHistory = null;

class App extends Component {

    onMovePage = (url) => {
        this.props.history.push(url);
    };

    componentDidMount() {
        console.log("[App][componentDidMount]");

        const checkConnection = async () => {
          try {
              if(sessionStorage.getItem("accessToken")) {
                  const res = await services.axiosAPI.requestGet(Define.REST_AUTHS_GET_ME);
                  const { status } = res;
                  console.log("[App][componentDidMount]status", status);
                  if (status === Define.OK) {
                      const {userName, userId, permission} = res.data;
                      await API.setLoginIsLoggedIn(this.props, true);
                      await API.setLoginUserName(this.props, userName);
                      await API.setLoginAuth(this.props, permission);
                      this.onMovePage(Define.PAGE_REFRESH_DEFAULT);     // move to first initialized ftp manual page
                  } else {
                      this.onMovePage(Define.PAGE_LOGIN);
                  }
              } else {
                  this.onMovePage(Define.PAGE_LOGIN);
              }
          } catch (e) {
            console.error(e);
            this.onMovePage(Define.PAGE_LOGIN);
          }
        }
        checkConnection().then(r => r).catch(e => e);
    }

    render() {
        const isLoggedIn = API.getLoginIsLoggedIn(this.props);
        console.log("[App][render]");
        console.log("[App][render]isLoggedIn", isLoggedIn);
        //console.log("[App][render]this.props.history", this.props.history);
        appHistory = this.props.history;    // for the network error page
        return (
                <>
                    {isLoggedIn && <Navbar onMovePage={this.onMovePage}/>}
                    <Switch>
                        <Route path={Define.PAGE_REFRESH} component={MoveRefreshPage}/>
                        <Route path={Define.PAGE_LOGIN} component={Login}/>
                        <Route path={Define.PAGE_MANUAL_FTP} component={Manual}/>
                        <Route path={Define.PAGE_MANUAL_VFTP_COMPAT} component={Manual2}/>
                        <Route path={Define.PAGE_MANUAL_VFTP_SSS} component={Manual3} />
                        <Route path={Define.PAGE_AUTO} component={Auto}/>
                        <Route path={Define.PAGE_ADMIN_ACCOUNT} component={AccountList} />
                        <Route path={Define.PAGE_ADMIN_DL_HISTORY} component={DlHistory} />
                        <Route path={Define.PAGE_NEWORK_ERROR} component={NetworkError} />

                        {/* How to pass props */}
                        {/*
                        <Route path={Define.PAGE_LOGIN} render={() => <Login {...this.props} />} />
                        <Route path={Define.PAGE_MANUAL} render={() => <Manual {...this.props} />} />
                        <Route path={Define.PAGE_AUTO} render={() => <Auto {...this.props} />} />
                        */}
                    </Switch>
                </>
        );
    }
}

export default connect(
    (state) => ({
        loginInfo : state.login.get('loginInfo'),
    }),
    (dispatch) => ({
        // bindActionCreators automatically bind action functions.
        loginActions: bindActionCreators(loginActions, dispatch),
    })
)(App);