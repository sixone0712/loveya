import React, {Component} from 'react';
import queryString from "query-string";
import * as Define from "../../define";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../modules/viewList";
import * as autoPlanActions from "../../modules/autoPlan";
import {PAGE_AUTO_PLAN_ADD} from "../../define";

class MoveRefreshPage extends Component {
    async componentDidMount() {
        console.log("[MoveRefreshPage] componentDidMount");
        const {history, location} = this.props;
        const query = queryString.parse(location.search);
        const { target } = query;

        console.log("query", query);
        console.log("target", target);

        if (target.includes(Define.PAGE_AUTO_PLAN_EDIT)) {
            console.log("PAGE_AUTO_PLAN_EDIT");
            const { editId } = query;
            console.log("editId", editId);
            history.replace(Define.PAGE_AUTO_PLAN_EDIT + "?editId=" + editId);
        } else if (target.includes(Define.PAGE_AUTO_PLAN_ADD)) {
            console.log("PAGE_AUTO_PLAN_ADD");
            const {viewListActions} = this.props;
            const {autoPlanActions} = this.props;
            await viewListActions.viewCheckAllToolList(false);
            await viewListActions.viewCheckAllLogTypeList(false)
            await autoPlanActions.autoPlanInit();
            history.replace(Define.PAGE_AUTO_PLAN_ADD);
        } else if (target.includes(Define.PAGE_MANUAL)) {
            console.log("PAGE_MANUAL");
            history.replace(Define.PAGE_MANUAL);
        } else if (target.includes(Define.PAGE_AUTO_STATUS)) {
            console.log("PAGE_AUTO_STATUS");
            history.replace(Define.PAGE_AUTO_STATUS);
        }
    }

    render() {
        return null;
    }
}
export default connect(
    (state) => ({
        toolInfoList: state.viewList.get('toolInfoList'),
        logInfoList: state.viewList.get('logInfoList'),
        autoPlan: state.autoPlan.get('autoPlan'),
    }),
    (dispatch) => ({
        viewListActions: bindActionCreators(viewListActions, dispatch),
        autoPlanActions: bindActionCreators(autoPlanActions, dispatch),
    })
)(MoveRefreshPage);
