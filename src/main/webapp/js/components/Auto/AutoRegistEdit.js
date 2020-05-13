import React, {Component} from "react";
import { Row, Col } from "reactstrap";
import Wizard from "./PlanWizard";
import queryString from 'query-string';

class RSSAutoRegistEdit extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isNew: false,
            editId: ""
        }
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        const query = queryString.parse(nextProps.location.search);
        const { editId } = query;
        if(editId !== undefined) {
            return {
                ...prevState,
                editId: editId
            }
        }

        return null;
    }

    render() {
        const { isNew, editId } = this.state;
        return (
        <>
            <Row className="pd-0">
                <Col>
                <Wizard isNew={isNew} editId={editId}/>
                </Col>
            </Row>
        </>
      );
    }
}

export default RSSAutoRegistEdit;