import React from "react";
import { Col, FormGroup, Input, Label, CustomInput } from "reactstrap";
import { Select } from "antd";

const { Option } = Select;

export default function RSSautoformlist() {
  return (
      <div className="form-section optionlist">
        <Col className="pd-0">
          <div className="form-section-header">
            <div className="form-section-title">
              Option Settings
              <p>Set detail options.</p>
            </div>
          </div>
          <div className="dis-flex align-center">
            <FormGroup className="auto-plan-optionlist-form-group">
              <FormGroup>
                <Label for="plan_no" className="input-label">
                  Plan No.
                </Label>
                <Input
                    type="text"
                    id="plan_no"
                    bsSize="sm"
                    className="half-width"
                />
              </FormGroup>
              <FormGroup>
                <Label for="plan_period" className="input-label">
                  Period
                </Label>
                <FormGroup className="period-section">
                  <Input type="text" id="period_start" bsSize="sm" />
                  <Input type="text" id="period_end" bsSize="sm" />
                </FormGroup>
              </FormGroup>
              <FormGroup>
                <Label for="plan_start" className="input-label">
                  Start
                </Label>
                <Input
                    type="text"
                    id="plan_start"
                    bsSize="sm"
                    className="half-width"
                />
              </FormGroup>
              <FormGroup>
                <Label for="plan_mode" className="input-label">
                  Mode
                </Label>
                <FormGroup className="dis-flex mode-section">
                  <CustomInput
                      type="radio"
                      id="mode_continue"
                      name="collection_mode"
                      label="Continuous"
                      defaultChecked
                  />
                  <CustomInput
                      type="radio"
                      id="mode_cycle"
                      name="collection_mode"
                      label="Cycle"
                      className="enable-margin"
                  />
                  <Select defaultValue="10" style={{ width: "120px" }}>
                    <Option value="10">10</Option>
                    <Option value="20">20</Option>
                    <Option value="30">30</Option>
                  </Select>
                </FormGroup>
              </FormGroup>
              <FormGroup>
                <Label for="plan_desc" className="input-label">
                  Description
                </Label>
                <Input type="text" id="plan_desc" bsSize="sm" />
              </FormGroup>
            </FormGroup>
          </div>
        </Col>
      </div>
  );
}