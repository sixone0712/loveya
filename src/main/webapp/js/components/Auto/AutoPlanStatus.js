import React from "react";
import { Container, Row, Col, Breadcrumb, BreadcrumbItem } from "reactstrap";
import PlanList from "./PlanList";

export default function AutoPlanStatus() {
    return (
        <>
            <Container className="rss-container" fluid={true}>
                <Breadcrumb className="auto-plan-wizard topic-path auto-plan-box-shadow">
                    <BreadcrumbItem>Auto Download</BreadcrumbItem>
                    <BreadcrumbItem active>Plan Status</BreadcrumbItem>
                </Breadcrumb>
                <Row className="pd-0">
                    <Col>
                        <PlanList />
                    </Col>
                </Row>
            </Container>
        </>
    );
}
