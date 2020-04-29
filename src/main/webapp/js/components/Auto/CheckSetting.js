import React, { Component } from "react";
import { Col, FormGroup, Label } from "reactstrap";

class RSSautoplanchecksetting extends Component {
    render() {
        return (
            <div className="form-section checksetting">
                <Col className="pd-0">
                    <div className="form-header-section">
                        <div className="form-title-section">
                            Check Settings
                            <p>Check your plan settings.</p>
                        </div>
                    </div>
                    <div className="dis-flex align-center">
                        <FormGroup className="auto-plan-checklist-form-group">
                            <FormGroup>
                                <Label>Plan ID</Label>
                                <div className="setting-info">Plan 1</div>
                            </FormGroup>
                            <FormGroup>
                                <Label>Period</Label>
                                <div className="setting-info">
                                    2020-04-20 00:00 ~ 2020-04-27 23:59
                                </div>
                            </FormGroup>
                            <FormGroup>
                                <Label>Start</Label>
                                <div className="setting-info">2020-04-29 00:00</div>
                            </FormGroup>
                            <FormGroup>
                                <Label>Mode</Label>
                                <div className="setting-info">Continuous</div>
                            </FormGroup>
                            <FormGroup>
                                <Label>Machine</Label>
                                <div className="setting-info">3 Machines</div>
                            </FormGroup>
                            <FormGroup>
                                <Label>Target</Label>
                                <div className="setting-info">2 Targets</div>
                            </FormGroup>
                            <FormGroup>
                                <Label>Description</Label>
                                <div className="setting-info">Default collection plan</div>
                            </FormGroup>
                        </FormGroup>
                    </div>
                </Col>
            </div>
        );
    }
}

export default RSSautoplanchecksetting;
