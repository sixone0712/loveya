import React from "react";
import { Container, Row, Col } from "reactstrap";

import Machinelist from "./Machine/machinelist";
import Categorylist from "./Category/categorylist";
import Formlist from "./Search/formlist";
import Filelist from "./File/filelist";
import Footer from "./footer";
import ScrollToTop from "react-scroll-up";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleDoubleUp} from "@fortawesome/free-solid-svg-icons";

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
      <ScrollToTop showUnder={160} style={{
          position: 'fixed',
          bottom: 50,
          right: 30,
          cursor: 'pointer',
          transitionDuration: '0.2s',
          transitionTimingFunction: 'linear',
          transitionDelay: '0s',
          width: "40px",
          height: "40px",
          textAlign: "center",
          backgroundColor: "rgb(52, 58, 64)",
          borderRadius: "3px"
      }}>
          <span className="scroll-up-icon"><FontAwesomeIcon icon={faAngleDoubleUp} size="lg"/></span>
      </ScrollToTop>
    </>
  );
}