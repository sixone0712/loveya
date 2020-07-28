import React, {Component} from 'react';
import queryString from "query-string";
import {NavLink, Route, Switch} from 'react-router-dom';
import ScrollToTop from "react-scroll-up";
import * as Define from "../../define";
import AutoPlanAdd from "./AutoRegistAdd";
import AutoPlanEdit from "./AutoRegistEdit";
import AutoStatus from "./AutoPlanStatus";
import AutoDownload from "./DownloadList"
import Footer from "../Common/Footer";
import {Breadcrumb, BreadcrumbItem, Container} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleDoubleUp} from "@fortawesome/free-solid-svg-icons";

const scrollStyle = {
    backgroundColor: "#343a40",
    width: "40px",
    height: "40px",
    textAlign: "center",
    borderRadius: "3px",
    zIndex: "101"
};

class Auto extends Component {
    constructor() {
        super();
        this.state = {
            page: Define.AUTO_CUR_PAGE_INIT,
            planInfo: {
                type: Define.PLAN_TYPE_FTP,
                message: Define.PLAN_TYPE_MSG_FTP
            }
        }
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        let page = Define.AUTO_CUR_PAGE_ADD
        const { location } = nextProps;
        const { type } = queryString.parse(location.search);
        const newType = (type && type !== "null") ? type : Define.PLAN_TYPE_FTP;

        if(location.pathname.includes(Define.PAGE_AUTO_STATUS)) {
            page = Define.AUTO_CUR_PAGE_STATUS;
        } else if(location.pathname.includes(Define.PAGE_AUTO_DOWNLOAD)) {
            page = Define.AUTO_CUR_PAGE_DOWNLOAD;
        } else if(location.pathname.includes(Define.PAGE_AUTO_PLAN_EDIT)) {
            page = Define.AUTO_CUR_PAGE_EDIT;
        }

        return {
            page: page,
            planInfo: {
                type: newType,
                message: writePlanMessage(newType)
            }
        };
    }

    render() {
        const { page, planInfo } = this.state;

        return (
            <>
                <Container className="rss-container" fluid={true}>
                    <CreateBreadCrumb page={page} message={planInfo.message}/>
                    <Switch>
                        <Route path={Define.PAGE_AUTO_PLAN_ADD} render={() => <AutoPlanAdd history={this.props.history} location={this.props.location} type={planInfo.type} />}/>
                        <Route path={Define.PAGE_AUTO_PLAN_EDIT} render={() => <AutoPlanEdit history={this.props.history} location={this.props.location} type={planInfo.type} />}/>
                        <Route path={Define.PAGE_AUTO_STATUS} component={AutoStatus}/>
                        <Route path={Define.PAGE_AUTO_DOWNLOAD} component={AutoDownload}/>
                    </Switch>
                </Container>
                <Footer />
                <ScrollToTop showUnder={160} style={scrollStyle}>
                    <span className="scroll-up-icon"><FontAwesomeIcon icon={faAngleDoubleUp} size="lg"/></span>
                </ScrollToTop>
            </>
        );
    }
}

const writePlanMessage = (type) => {
    switch(type) {
        case Define.PLAN_TYPE_FTP:
            return Define.PLAN_TYPE_MSG_FTP;

        case Define.PLAN_TYPE_VFTP_COMPAT:
            return Define.PLAN_TYPE_MSG_VFTP_COMPAT;

        case Define.PLAN_TYPE_VFTP_SSS:
            return Define.PLAN_TYPE_MSG_VFTP_SSS;

        default:
            return null;
    }
};

export default Auto;

export const CreateBreadCrumb = props => {
    const { page, message } = props;

    switch (page) {
        case Define.AUTO_CUR_PAGE_ADD:
            return (
                <Breadcrumb className="topic-path">
                    <BreadcrumbItem>Auto Download</BreadcrumbItem>
                    <BreadcrumbItem active>Add New Plan {message}</BreadcrumbItem>
                </Breadcrumb>
            )

        case Define.AUTO_CUR_PAGE_STATUS:
            return (
                <Breadcrumb className="topic-path">
                    <BreadcrumbItem>Auto Download</BreadcrumbItem>
                    <BreadcrumbItem active>Plan Status</BreadcrumbItem>
                </Breadcrumb>
            );

        case Define.AUTO_CUR_PAGE_EDIT:
            return (
                <Breadcrumb className="topic-path">
                    <BreadcrumbItem>Auto Download</BreadcrumbItem>
                    <BreadcrumbItem>
                        <NavLink to={Define.PAGE_REFRESH_AUTO_STATUS} className="link">
                            Plan Status
                        </NavLink>
                    </BreadcrumbItem>
                    <BreadcrumbItem active>Edit Plan {message}</BreadcrumbItem>
                </Breadcrumb>
            );

        case Define.AUTO_CUR_PAGE_DOWNLOAD:
            return (
                <>
                    <Breadcrumb className="topic-path">
                        <BreadcrumbItem>Auto Download</BreadcrumbItem>
                        <BreadcrumbItem>
                            <NavLink to={Define.PAGE_REFRESH_AUTO_STATUS} className="link">
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