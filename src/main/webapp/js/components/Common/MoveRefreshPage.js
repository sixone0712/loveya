import React, {Component} from 'react';
import queryString from "query-string";
import * as Define from "../../define";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../modules/viewList";
import * as autoPlanActions from "../../modules/autoPlan";
import * as genreListActions from "../../modules/genreList";
import * as searchListActions from "../../modules/searchList";
import * as commandActions from "../../modules/command";
import * as CompatActions from "../../modules/vftpCompat";
import * as sssActions from "../../modules/vftpSss";

class MoveRefreshPage extends Component {

    // first initialized ftp manual page
    // (When login is completed, F5 (refresh) is pressed, Rapid Controller is clicked)
    firstInit = async () => {
        const { viewListActions, searchListActions, genreListActions } = this.props;
        await viewListActions.viewInitAllList();
        await searchListActions.searchSetInitAllList();
        await genreListActions.genreInitAllList();

        await viewListActions.viewInitAllList();
        await viewListActions.viewLoadToolInfoList(Define.REST_INFOS_GET_MACHINES);
        await viewListActions.viewLoadLogTypeList(Define.REST_INFOS_GET_CATEGORIES);
        await genreListActions.genreLoadDbList(Define.REST_API_URL + "/genre/get");
    }

    manualInit = async (type) => {
        const {
            viewListActions,
            searchListActions,
            genreListActions,
            commandActions,
            CompatActions,
            sssActions,
        } = this.props;

        await viewListActions.viewCheckAllToolList(false);

        if (type === Define.PLAN_TYPE_FTP) {
            await viewListActions.viewCheckAllLogTypeList(false);
            await searchListActions.searchSetInitAllList();
            await genreListActions.genreInitAllList();
            await genreListActions.genreLoadDbList(Define.REST_API_URL + "/genre/get");
        } else if (type === Define.PLAN_TYPE_VFTP_COMPAT) {
            await commandActions.commandInit();
            await CompatActions.vftpCompatInitAll();
            await commandActions.commandLoadList("/rss/api/vftp/command?type=vftp_compat");
        } else {
            await commandActions.commandInit();
            await sssActions.vftpSssInitAll();
            await commandActions.commandLoadList("/rss/api/vftp/command?type=vftp_sss");
        }
    }

    autoAddInit = async (type) => {
        const {
            viewListActions,
            commandActions,
            CompatActions,
            sssActions,
            autoPlanActions
        } = this.props;

        await autoPlanActions.autoPlanInit();
        await viewListActions.viewCheckAllToolList(false);
        if (type === Define.PLAN_TYPE_FTP) {
            await viewListActions.viewCheckAllLogTypeList(false);
        } else if (type === Define.PLAN_TYPE_VFTP_COMPAT) {
            await commandActions.commandInit();
            await CompatActions.vftpCompatInitAll();
            await commandActions.commandLoadList("/rss/api/vftp/command?type=vftp_compat");
        } else {
            await commandActions.commandInit();
            await sssActions.vftpSssInitAll();
            await commandActions.commandLoadList("/rss/api/vftp/command?type=vftp_sss");
        }
    }

    async componentDidMount() {
        console.log("[MoveRefreshPage] componentDidMount");
        const {history, location} = this.props;
        const query = queryString.parse(location.search);
        const { target } = query;

        console.log("[MoveRefreshPage]query", query);
        console.log("[MoveRefreshPage]target", target);

        if (target.includes(Define.PAGE_DEFAULT)) {
            console.log("[MoveRefreshPage]PAGE_DEFAULT");
            await this.firstInit();
            history.replace(Define.PAGE_MANUAL_FTP);
        } else if (target.includes(Define.PAGE_AUTO_PLAN_EDIT)) {
            console.log("[MoveRefreshPage]PAGE_AUTO_PLAN_EDIT");
            const { editId, type } = query;
            console.log("[MoveRefreshPage]editId", editId, "type", type);
            history.replace(`${Define.PAGE_AUTO_PLAN_EDIT}?editId=${editId}&type=${type}`);
        } else if (target.includes(Define.PAGE_AUTO_PLAN_ADD)) {
            console.log("[MoveRefreshPage]PAGE_AUTO_PLAN_ADD");
            const { type } = query;
            console.log("[MoveRefreshPage]type", type);
            await this.autoAddInit(type);
            history.replace(Define.PAGE_AUTO_PLAN_ADD + "?type=" + type);
        } else if (target.includes(Define.PAGE_MANUAL_FTP)) {
            console.log("[MoveRefreshPage]PAGE_MANUAL_FTP");
            await this.manualInit(Define.PLAN_TYPE_FTP)
            history.replace(Define.PAGE_MANUAL_FTP);
        } else if (target.includes(Define.PAGE_MANUAL_VFTP_COMPAT)) {
            console.log("[MoveRefreshPage]PAGE_MANUAL_VFTP_COMPAT");
            await this.manualInit(Define.PLAN_TYPE_VFTP_COMPAT)
            history.replace(Define.PAGE_MANUAL_VFTP_COMPAT);
        } else if (target.includes(Define.PAGE_MANUAL_VFTP_SSS)) {
            console.log("[MoveRefreshPage]PAGE_MANUAL_VFTP_SSS");
            await this.manualInit(Define.PLAN_TYPE_VFTP_SSS)
            history.replace(Define.PAGE_MANUAL_VFTP_SSS);
        } else if (target.includes(Define.PAGE_AUTO_STATUS)) {
            console.log("[MoveRefreshPage]PAGE_AUTO_STATUS");
            history.replace(Define.PAGE_AUTO_STATUS);
        } else if (target.includes(Define.PAGE_ADMIN_ACCOUNT)) {
            console.log("[MoveRefreshPage]PAGE_ADMIN_ACCOUNT");
            history.replace(Define.PAGE_ADMIN_ACCOUNT);
        } else if (target.includes(Define.PAGE_ADMIN_DL_HISTORY)) {
            console.log("[MoveRefreshPage]PAGE_ADMIN_DL_HISTORY");
            history.replace(Define.PAGE_ADMIN_DL_HISTORY);
        }
    }

    render() {
        return null;
    }
}
export default connect(
    (state) => ({ }),
    (dispatch) => ({
        viewListActions: bindActionCreators(viewListActions, dispatch),
        autoPlanActions: bindActionCreators(autoPlanActions, dispatch),
        genreListActions: bindActionCreators(genreListActions, dispatch),
        searchListActions: bindActionCreators(searchListActions, dispatch),
        commandActions: bindActionCreators(commandActions, dispatch),
        CompatActions: bindActionCreators(CompatActions, dispatch),
        sssActions: bindActionCreators(sssActions, dispatch),
    })
)(MoveRefreshPage);
