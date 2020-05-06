import React, { Component } from "react";
import { Container, Row, Col, Breadcrumb, BreadcrumbItem } from "reactstrap";
import PlanList from "./PlanList";
import Wizard from "./PlanWizard";
import Download from "./DownloadList";

const PAGE_STATUS = 1;
const PAGE_EDIT = 2;
const PAGE_DOWNLOAD = 3;

class AutoPlanStatus extends Component {
    constructor(props) {
        super(props);
        this.state = {
            page: PAGE_STATUS
        };
    }

    changeRenderPage = page => {
        this.setState({
            page: page
        });
    };

    render() {
        const { page } = this.state;
        let renderPage = "";

        switch (page) {
            case PAGE_STATUS:
            default:
                renderPage = <PlanList pageChanger={this.changeRenderPage} />;
                break;

            case PAGE_EDIT:
                renderPage = <Wizard isNew={false} />;
                break;

            case PAGE_DOWNLOAD:
                renderPage = <Download />;
                break;
        }

        return (
            <Container className="rss-container" fluid={true}>
                <Breadcrumb className="auto-plan-topic-path">
                    <BreadcrumbItem>Auto Download</BreadcrumbItem>
                    <CreateBreadCrumb page={page} pageChanger={this.changeRenderPage} />
                </Breadcrumb>
                <Row className="pd-0">
                    <Col>{renderPage}</Col>
                </Row>
            </Container>
        );
    }
}

const CreateBreadCrumb = props => {
    const { page, pageChanger } = props;

    switch (page) {
        case PAGE_STATUS:
        default:
            return <BreadcrumbItem active>Plan Status</BreadcrumbItem>;

        case PAGE_EDIT:
            return (
                <>
                    <BreadcrumbItem onClick={() => pageChanger(PAGE_STATUS)}>
                        <div className="link">Plan Status</div>
                    </BreadcrumbItem>
                    <BreadcrumbItem active>Edit Plan</BreadcrumbItem>
                </>
            );

        case PAGE_DOWNLOAD:
            return (
                <>
                    <BreadcrumbItem onClick={() => pageChanger(PAGE_STATUS)}>
                        <div className="link">Plan Status</div>
                    </BreadcrumbItem>
                    <BreadcrumbItem active>Download List</BreadcrumbItem>
                </>
            );
    }
};

export default AutoPlanStatus;