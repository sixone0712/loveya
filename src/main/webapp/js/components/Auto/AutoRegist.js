import React, {Component} from "react";
import { Row, Col } from "reactstrap";
import Wizard from "./PlanWizard";
import * as DEFINE from "../../define";

class RSSAutoRegist extends Component {

    constructor() {
        super();
        this.state = {
            isNew: true
        }
    }

    static getDerivedStateFromProps(nextProps, prevState) {
        let isNew = true;

        if(nextProps.location.pathname.includes(DEFINE.PAGE_AUTO_PLAN_EDIT)) {
            isNew = false;
        }

        return {
            ...prevState,
            isNew: isNew
        };
    }

    render() {
        const { isNew } = this.state;
        return (
        <>
            <Row className="pd-0">
                <Col>
                <Wizard isNew={isNew}/>
                </Col>
            </Row>
        </>
      );
    }
}

export default RSSAutoRegist;