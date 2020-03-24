import React from "react";
import { Container, Row, Col } from "reactstrap";

import Machinelist from "./Machine/machinelist";
import Categorylist from "./Category/categorylist";
import Formlist from "./Search/formlist";
import Filelist from "./File/filelist";
import Footer from "./footer";

export default function Manual() {
  return (
    <>
      <Container className="manual-container" fluid={true}>
        <Row>
          <Col>
            <Machinelist />
          </Col>
          <Col>
            <Categorylist />
          </Col>
          <Col>
            <Formlist />
          </Col>
        </Row>
        <Filelist />
      </Container>
      <Footer />
    </>
  );
}