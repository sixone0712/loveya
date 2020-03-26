import React, {Component} from "react";
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux';
import * as viewListActions from './modules/viewList';
import * as genreListActions from './modules/genreList';
import * as searchListActions from './modules/searchList';
import services from './services'
import { Map, List, fromJS } from 'immutable';
import * as API from "./api";
import Navbar from "./components/Navbar";
import Manual from "./components/Manual";

class App extends Component {

    constructor(props) {
        super(props);
    }

    componentDidMount() {
        console.log("componentDidMount");
        const { viewListActions } = this.props;
        const { genreListActions } = this.props;
        viewListActions.viewLoadToolInfoList("/api/createToolList");
        viewListActions.viewLoadLogTypeList("/api/createFileTypeList");
        genreListActions.genreLoadList("/api/getGenre");
    }

    render() {

        const { logTypeSuccess, toolInfoSuccess } = this.props;
        const isSuccess = logTypeSuccess && true && toolInfoSuccess;
        console.log("isSuccess", isSuccess);
        return (
                <>
                    { isSuccess && true &&
                        (
                            <>
                            <Navbar />
                            <Manual />
                            </>
                        )
                    }
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
        toolInfoSuccess: state.pender.success['viewList/VIEW_LOAD_LOGTYPE_LIST']
    }),
    (dispatch) => ({
        // bindActionCreators 는 액션함수들을 자동으로 바인딩해줍니다.
        viewListActions: bindActionCreators(viewListActions, dispatch),
        //selectListActions: bindActionCreators(selectListActions, dispatch),
        genreListActions: bindActionCreators(genreListActions, dispatch),
        searchListActions: bindActionCreators(searchListActions, dispatch)
    })
)(App);