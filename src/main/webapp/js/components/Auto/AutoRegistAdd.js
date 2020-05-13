import React, {Component} from "react";
import { Row, Col } from "reactstrap";
import Wizard from "./PlanWizard";

class RSSAutoRegistAdd extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isNew: true,
            editId: ""
        }
     }

    render() {
        const { isNew, editId } = this.state;
        return (
        <>
            <Row className="pd-0">
                <Col>
                <Wizard isNew={isNew} editId={editId} history={this.props.history}/>
                </Col>
            </Row>
        </>
      );
    }
}

export default RSSAutoRegistAdd;
