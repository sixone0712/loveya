import React, {Component} from "react";
import {Col, Row} from "reactstrap";
import PlanList from "./PlanList";

class AutoPlanStatus extends Component {
    render() {
        return (
                 <Row className="pd-0">
                    <Col>
                        <PlanList history={this.props.history}/>
                    </Col>
                </Row>
        );
    }
}

export default AutoPlanStatus;