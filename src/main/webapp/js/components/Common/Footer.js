import React from "react";
import {Col, NavbarBrand, Row} from "reactstrap";

const RSSfooter = () => {
  return (
    <footer className="footer-container">
      <Row>
        <Col>
          <ul className="list-unstyled">
            {/*<li className="float-lg-left">{process.env.RSS_VERSION}</li>*/}
            <li className="float-lg-right">Copyright CANON INC. 2020</li>
          </ul>
        </Col>
      </Row>
    </footer>
  );
}

export default RSSfooter;