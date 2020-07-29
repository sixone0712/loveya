import React, {Component} from 'react';
import queryString from "query-string";
import * as Define from "../../define";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../modules/viewList";
import * as autoPlanActions from "../../modules/autoPlan";

class MoveRefreshPage extends Component {
    async componentDidMount() {
        console.log("[MoveRefreshPage] componentDidMount");
        const {history, location} = this.props;
        const query = queryString.parse(location.search);
        const { target } = query;

        console.log("[MoveRefreshPage]query", query);
        console.log("[MoveRefreshPage]target", target);

        if (target.includes(Define.PAGE_AUTO_PLAN_EDIT)) {
            console.log("PAGE_AUTO_PLAN_EDIT");
            const { editId, type } = query;
            console.log("editId", editId);
            history.replace(`${Define.PAGE_AUTO_PLAN_EDIT}?editId=${editId}&type=${type}`);
        } else if (target.includes(Define.PAGE_AUTO_PLAN_ADD)) {
            console.log("PAGE_AUTO_PLAN_ADD");
            const {viewListActions, autoPlanActions} = this.props;
            const { type } = query;
            await viewListActions.viewCheckAllToolList(false);
            await viewListActions.viewCheckAllLogTypeList(false)
            await autoPlanActions.autoPlanInit();
            history.replace(Define.PAGE_AUTO_PLAN_ADD + "?type=" + type);
        } else if (target.includes(Define.PAGE_MANUAL_FTP)) {
            console.log("PAGE_MANUAL_FTP");
            history.replace(Define.PAGE_MANUAL_FTP);
        } else if (target.includes(Define.PAGE_MANUAL_VFTP_COMPAT)) {
            console.log("PAGE_MANUAL_VFTP_COMPAT");
            history.replace(Define.PAGE_MANUAL_VFTP_COMPAT);
        } else if (target.includes(Define.PAGE_MANUAL_VFTP_SSS)) {
            console.log("PAGE_MANUAL_VFTP_SSS");
            history.replace(Define.PAGE_MANUAL_VFTP_SSS);
        } else if (target.includes(Define.PAGE_AUTO_STATUS)) {
            console.log("PAGE_AUTO_STATUS");
            history.replace(Define.PAGE_AUTO_STATUS);
        } else if (target.includes(Define.PAGE_ADMIN_ACCOUNT)) {
            console.log("PAGE_ADMIN_ACCOUNT");
            history.replace(Define.PAGE_ADMIN_ACCOUNT);
        } else if (target.includes(Define.PAGE_ADMIN_DL_HISTORY)) {
            console.log("PAGE_ADMIN_ACCOUNT");
            history.replace(Define.PAGE_ADMIN_DL_HISTORY);
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
