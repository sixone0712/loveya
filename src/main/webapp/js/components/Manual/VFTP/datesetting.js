import React from "react";
import {Card, CardBody, Col, FormGroup, Input, Label} from "reactstrap";
import {DatetimePicker} from "rc-datetime-picker";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCalendarCheck} from "@fortawesome/free-regular-svg-icons";


const RSSdatesettings = ({from, FromDateChangehandler, to, ToDateChangehandler}) => {
    return (
        <Card className="ribbon-wrapper formlist-card">
            <CardBody className="custom-scrollbar manual-card-body">
                <div className="ribbon ribbon-clip ribbon-success">Date</div>
                <Col>
                    <FormGroup className="formlist-form-group">
                        <div className="datepicker-item-area">
                            <CreateDatetimePicker label={"From"} date={from} handleChange={date=> FromDateChangehandler(date)} />
                            <CreateDatetimePicker label={"To"} date={to} handleChange={date=> ToDateChangehandler(date)}/>
                        </div>
                    </FormGroup>
                </Col>
            </CardBody>
        </Card>
    );
};

export const CreateDatetimePicker = ({ label, date, handleChange}) => {
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
