import React from "react";
import { Container, Row, Col, Breadcrumb, BreadcrumbItem } from "reactstrap";
import Wizard from "./PlanWizard";

export default function RSSAutoRegist() {
  return (
      <>
        <Container className="rss-container" fluid={true}>
          <Breadcrumb className="auto-plan-wizard topic-path auto-plan-box-shadow">
            <BreadcrumbItem>Auto Download</BreadcrumbItem>
            <BreadcrumbItem active>Add New Plan</BreadcrumbItem>
          </Breadcrumb>
          <Row className="pd-0">
            <Col>
              <Wizard />
            </Col>
          </Row>
        </Container>
      </>
  );
}