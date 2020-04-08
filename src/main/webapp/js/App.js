import React, {Component} from "react";
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux';
import * as viewListActions from './modules/viewList';
import * as genreListActions from './modules/genreList';
import * as searchListActions from './modules/searchList';
import * as loginActions from './modules/login';
import services from './services'
import { Map, List, fromJS } from 'immutable';
import * as API from "./api";
import Navbar from "./components/Navbar";
import Manual from "./components/Manual/Manual";
import Auto from "./components/Auto/Auto";
import Login from "./components/User/Login";
import {BrowserRouter, Route, Switch} from 'react-router-dom';
import * as Define from "./define";

class App extends Component {

    constructor(props) {
        super(props);
    }

    onMovePage = (url) => {
        this.props.history.push(url);
    };

    componentDidMount() {
        const isLoggedInStorage = window.sessionStorage.getItem('isLoggedIn');

        console.log("componentDidMount");
        console.log("this.props.isLoggedIn", this.props.isLoggedIn);
        console.log("isLoggedInStorage", isLoggedInStorage);

        if(isLoggedInStorage === null || isLoggedInStorage === false) {
            API.setLoginIsLoggedIn(this.props, false);
            //this.props.history.push("/rss/login");
            this.onMovePage(Define.PAGE_LOGIN);
        } else {
            API.setLoginIsLoggedIn(this.props, true);
            //this.props.history.push("/rss/manual");
            this.onMovePage(Define.PAGE_MANUAL);
        }
    }

    render() {
        const isLoggedIn = API.getLoginIsLoggedIn(this.props);
        console.log("isLoggedIn", isLoggedIn);
        console.log("this.props.history", this.props.history);
        return (
                <>
                    {isLoggedIn && <Navbar onMovePage={this.onMovePage}/>}
                    <Switch>
                        <Route path={Define.PAGE_LOGIN} component={Login} />
                        <Route path={Define.PAGE_MANUAL} component={Manual} />
                        <Route path={Define.PAGE_AUTO} component={Auto} />
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
        // bindActionCreators 는 액션함수들을 자동으로 바인딩해줍니다.
        viewListActions: bindActionCreators(viewListActions, dispatch),
        //selectListActions: bindActionCreators(selectListActions, dispatch),
        genreListActions: bindActionCreators(genreListActions, dispatch),
        searchListActions: bindActionCreators(searchListActions, dispatch),
        loginActions: bindActionCreators(loginActions, dispatch),
    })
)(App);