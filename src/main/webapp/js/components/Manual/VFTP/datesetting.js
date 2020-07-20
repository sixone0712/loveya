import React, { useState } from "react";
import { Card, CardBody, Col, FormGroup, Input, Label } from "reactstrap";
import { DatetimePicker } from "rc-datetime-picker";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCalendarCheck } from "@fortawesome/free-regular-svg-icons";
import moment from "moment";

const RSSdatesettings = () => {
    return (
        <Card className="ribbon-wrapper formlist-card">
            <CardBody className="custom-scrollbar manual-card-body">
                <div className="ribbon ribbon-clip ribbon-success">Date</div>
                <Col>
                    <FormGroup className="formlist-form-group">
                        <div className="datepicker-item-area">
                            <CreateDatetimePicker label={"From"} moment={moment().startOf("day")} />
                            <CreateDatetimePicker label={"To"} moment={moment().endOf("day")} />
                        </div>
                    </FormGroup>
                </Col>
            </CardBody>
        </Card>
    );
};

const CreateDatetimePicker = ({ label, moment }) => {
    const [date, setDate] = useState(moment);
    const handleChange = date => { setDate(date); };

    return (
        <div className="datepicker-item">
            <Label>
                <FontAwesomeIcon icon={faCalendarCheck} size="lg" /> {label}
            </Label>
            <Input type="text" value={date.format("YYYY-MM-DD HH:mm")} readOnly />
            <DatetimePicker moment={date} onChange={handleChange} />
        </div>
    );
};

export default RSSdatesettings;
