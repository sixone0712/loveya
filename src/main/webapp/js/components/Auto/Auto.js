import React, {Component} from 'react';
import { Route, Switch } from 'react-router-dom';
import { NavLink } from "react-router-dom";
import * as DEFINE from "../../define";
import AutoPlanAdd from "./AutoRegistAdd";
import AutoPlanEdit from "./AutoRegistEdit";
import AutoStatus from "./AutoPlanStatus";
import AutoDownload from "./DownloadList"
import {Breadcrumb, BreadcrumbItem, Container } from "reactstrap";

class Auto extends Component {

    constructor() {
        super();
        this.state = {
            page: DEFINE.AUTO_CUR_PAGE_INIT
        }
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        let page = DEFINE.AUTO_CUR_PAGE_ADD
        if(nextProps.location.pathname.includes(DEFINE.PAGE_AUTO_STATUS)) {
            page = DEFINE.AUTO_CUR_PAGE_STATUS;
        } else if(nextProps.location.pathname.includes(DEFINE.PAGE_AUTO_DOWNLOAD)) {
            page = DEFINE.AUTO_CUR_PAGE_DOWNLOAD;
        } else if(nextProps.location.pathname.includes(DEFINE.PAGE_AUTO_PLAN_EDIT)) {
            page = DEFINE.AUTO_CUR_PAGE_EDIT;
        }

        return {
            ...prevState,
            page: page
        };
    }

    render() {
        const { page } = this.state;

        return (
            <>
                <Container className="rss-container" fluid={true}>
                    <CreateBreadCrumb page={page}/>
                    <Switch>
                        <Route path={DEFINE.PAGE_AUTO_PLAN_ADD} component={AutoPlanAdd}/>
                        <Route path={DEFINE.PAGE_AUTO_PLAN_EDIT} component={AutoPlanEdit}/>
                        <Route path={DEFINE.PAGE_AUTO_STATUS} component={AutoStatus}/>
                        <Route path={DEFINE.PAGE_AUTO_DOWNLOAD} component={AutoDownload}/>
                    </Switch>
                </Container>
            </>
        );
    }
};

export default Auto;

const CreateBreadCrumb = props => {
    const { page } = props;

    switch (page) {
        case DEFINE.AUTO_CUR_PAGE_ADD:
            return (
                <Breadcrumb className="auto-plan-topic-path">
                    <BreadcrumbItem>Auto Download</BreadcrumbItem>
                    <BreadcrumbItem active>Add New Plan</BreadcrumbItem>
                </Breadcrumb>
            )

        case DEFINE.AUTO_CUR_PAGE_STATUS:
            return (
                <Breadcrumb className="auto-plan-topic-path">
                    <BreadcrumbItem>Auto Download</BreadcrumbItem>
                    <BreadcrumbItem active>Plan Status</BreadcrumbItem>
                </Breadcrumb>
            );

        case DEFINE.AUTO_CUR_PAGE_EDIT:
            return (
                <Breadcrumb className="auto-plan-topic-path">
                    <BreadcrumbItem>
                        <NavLink to={DEFINE.PAGE_REFRESH_AUTO_STATUS} className="link">
                            Plan Status
                        </NavLink>
                    </BreadcrumbItem>
                    <BreadcrumbItem active>Edit Plan</BreadcrumbItem>
                </Breadcrumb>
            );

        case DEFINE.AUTO_CUR_PAGE_DOWNLOAD:
            return (
                <>
                    <Breadcrumb className="auto-plan-topic-path">
                        <BreadcrumbItem>
                            <NavLink to={DEFINE.PAGE_REFRESH_AUTO_STATUS} className="link">
                                Plan Status
                            </NavLink>
                        </BreadcrumbItem>
                        <BreadcrumbItem active>Download List</BreadcrumbItem>
                    </Breadcrumb>
                </>
            );

        default:
            return null;
    }
}