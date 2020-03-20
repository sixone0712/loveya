import React from "react";
import { Card, CardBody, Col, FormGroup, Button } from "reactstrap";
import DatePicker from "./datetimepicker";

const formGroupStyle = {
  marginBottom: "0px"
};

const buttonPosition = {
  position: "absolute",
  top: "17px",
  right: "20px"
};

function RSSformlist() {
  return (
    <Card className="ribbon-wrapper formlist-custom">
      <CardBody className="custom-scrollbar card-body-custom card-body-formlist">
        <div className="ribbon ribbon-clip ribbon-success">Date</div>
        <Col>
          <FormGroup style={formGroupStyle}>
            <DatePicker />
          </FormGroup>
        </Col>
        <div style={buttonPosition}>
          <Button outline size="sm" color="info" className="formlist-btn">
            Search
          </Button>
        </div>
      </CardBody>
    </Card>
  );
}

export default RSSformlist;
