import React, {Component} from "react";
import {connect} from 'react-redux'
import {bindActionCreators} from 'redux';
import * as viewListActions from './modules/viewList';
import * as genreListActions from './modules/genreList';
import * as searchListActions from './modules/searchList';
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

class App extends Component {

    onMovePage = (url) => {
        this.props.history.push(url);
    };

    componentDidMount() {
        console.log("[App][componentDidMount]");
        /*
        const isLoggedInStorage = window.sessionStorage.getItem('isLoggedIn');
        console.log("this.props.isLoggedIn", this.props.isLoggedIn);
        console.log("isLoggedInStorage", isLoggedInStorage);

        if(isLoggedInStorage === null || isLoggedInStorage === false) {
            API.setLoginIsLoggedIn(this.props, false);
            this.onMovePage(Define.PAGE_LOGIN);
        } else {
            API.setLoginIsLoggedIn(this.props, true);
            this.onMovePage(Define.PAGE_MANUAL);
        }
        */

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
                      this.onMovePage(Define.PAGE_MANUAL_FTP);
                  } else {
                      this.onMovePage(Define.PAGE_LOGIN);
                  }
              } else {
                  this.onMovePage(Define.PAGE_LOGIN);
              }
          } catch (e) {
            console.log(e);
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
                        {/*<Redirect to={Define.PAGE_MANUAL} component={Manual} />*/}

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
        equipmentList: state.viewList.get('equipmentList'),
        toolInfoList: state.viewList.get('toolInfoList'),
        logInfoList: state.viewList.get('logInfoList'),
        genreList: state.genreList.get('genreList'),
        genreCnt: state.genreList.get('genreCnt'),
        requestList: state.searchList.get('requestList'),
        responseList: state.searchList.get('responseList'),
        startDate: state.searchList.get('startDate'),
        endDate: state.searchList.get('endDate'),
        logTypeSuccess: state.pender.success['viewList/VIEW_LOAD_TOOLINFO_LIST'],
        toolInfoSuccess: state.pender.success['viewList/VIEW_LOAD_LOGTYPE_LIST'],
        loginInfo : state.login.get('loginInfo'),
    }),
    (dispatch) => ({
        // bindActionCreators automatically bind action functions.
        viewListActions: bindActionCreators(viewListActions, dispatch),
        genreListActions: bindActionCreators(genreListActions, dispatch),
        searchListActions: bindActionCreators(searchListActions, dispatch),
        loginActions: bindActionCreators(loginActions, dispatch),
    })
)(App);