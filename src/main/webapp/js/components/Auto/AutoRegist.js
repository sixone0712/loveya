import React from "react";
import { Container, Row, Col, Breadcrumb, BreadcrumbItem } from "reactstrap";
import Wizard from "./PlanWizard";

export default function RSSAutoRegist() {
  return (
      <>
        <Container className="rss-container" fluid={true}>
          <Breadcrumb className="auto-plan-topic-path">
            <BreadcrumbItem>Auto Download</BreadcrumbItem>
            <BreadcrumbItem active>Add New Plan</BreadcrumbItem>
          </Breadcrumb>
          <Row className="pd-0">
            <Col>
              <Wizard isNew={true}/>
            </Col>
          </Row>
        </Container>
      </>
  );
}