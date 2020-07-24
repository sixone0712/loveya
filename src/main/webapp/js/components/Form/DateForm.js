import React, {Component} from "react";
import moment from "moment";
import {DatetimePicker} from "rc-datetime-picker";
import {Input, Label} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCalendarCheck} from "@fortawesome/free-regular-svg-icons";
import PropTypes from 'prop-types';

export const CreateDatetimePicker = (props) => {
    const {label, date, onChangeDate} = props;

    return (
        <>
            <Label>
                <FontAwesomeIcon icon={faCalendarCheck} size="lg"/> {label}
            </Label>
            <Input
                type="text"
                value={moment(date).format("YYYY-MM-DD HH:mm")}
                className="input-datepicker"
                readOnly
            />
            <DatetimePicker moment={date} onChange={onChangeDate}/>
        </>
    );
};


class DateForm extends Component {

    render() {
        const { startDate, endDate }  = this.props;
        const {sDateChanageFunc, eDateChanageFunc}= this.props;
        return (
            <div className="datepicker-item-area">
                <div className="datepicker-item">
                    <CreateDatetimePicker
                        label={"From"}
                        date={startDate}
                        onChangeDate={sDateChanageFunc}
                    />
                </div>
                <div className="datepicker-item">
                    <CreateDatetimePicker
                        label={"To"}
                        date={endDate}
                        onChangeDate={eDateChanageFunc}
                    />
                </div>
            </div>
        );
    }
}
DateForm.propTypes = {
    startDate: PropTypes.object.isRequired,
    endDate: PropTypes.object.isRequired,
    sDateChanageFunc:PropTypes.func.isRequired,
    eDateChanageFunc:PropTypes.func.isRequired,
};

export default DateForm;