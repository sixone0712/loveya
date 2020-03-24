import React from "react";
import { Row, Col } from "reactstrap";

export default function RSSfooter() {
  return (
    <footer className="footer-container">
      <Row>
        <Col>
          <ul className="list-unstyled">
            <li className="float-lg-left">Copyright CANON INC. 2020</li>
            <li className="float-lg-right">
              <a href="#">Back to top</a>
            </li>
          </ul>
        </Col>
      </Row>
    </footer>
  );
}