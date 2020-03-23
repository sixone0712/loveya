import React from "react";
import { Row, Col } from "reactstrap";

const rowStyle = {
  marginLeft: "0px",
  marginRight: "0px"
};

const fontCommon = {
  fontWeight: "200",
  fontSize: "13px"
};

const footerStyle = {
  borderTop: "1px solid #dadada",
  backgroundColor: "#fbf7fb",
  paddingTop: "1rem",
  paddingBottom: "1rem"
};

export default function RSSfooter() {
  return (
    <footer style={footerStyle}>
      <Row style={rowStyle}>
        <Col>
          <ul className="list-unstyled">
            <li className="float-lg-left" style={fontCommon}>
              Copyright CANON INC. 2020
            </li>
            <li className="float-lg-right" style={fontCommon}>
              <a href="#">Back to top</a>
            </li>
          </ul>
        </Col>
      </Row>
    </footer>
  );
}
