import React from "react";
import { Container, Row, Col } from "reactstrap";

import Machinelist from "./machinelist";
import Categorylist from "./categorylist";
import Formlist from "./formlist";
import Filelist from "./filelist";
import Footer from "./footer";

const containerStyle = {
  paddingTop: "4rem",
  paddingBottom: "1rem",
  backgroundColor: "#f6f7fb"
};

const rowStyle = {
  paddingLeft: "10px",
  paddingRight: "10px"
};

export default function Manual() {
  return (
    <>
      <Container style={containerStyle} fluid={true}>
        <Row style={rowStyle}>
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
