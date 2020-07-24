import React, {Component} from "react";
import moment from "moment";
import {DatetimePicker} from "rc-datetime-picker";
import {Input, Label} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCalendarCheck} from "@fortawesome/free-regular-svg-icons";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as searchListActions from "../../../modules/searchList";
import * as API from "../../../api";

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


class DatePicker extends Component {

    constructor() {
        super();
    }

    onStartDateChanage = startDate => {
        API.setStartDate(this.props, startDate)
    };

    onEndDateChanage = endDate => {
        API.setEndDate(this.props, endDate)
    };

    render() {

        const { startDate, endDate }  = this.props;

        return (
            <div className="datepicker-item-area">
                <div className="datepicker-item">
                    <CreateDatetimePicker
                        label={"From"}
                        date={startDate}
                        onChangeDate={this.onStartDateChanage}
                    />
                </div>
                <div className="datepicker-item">
                    <CreateDatetimePicker
                        label={"To"}
                        date={endDate}
                        onChangeDate={this.onEndDateChanage}
                    />
                </div>
            </div>
        );
    }
}

export default connect(
    (state) => ({
        startDate: state.searchList.get('startDate'),
        endDate: state.searchList.get('endDate'),
    }),
    (dispatch) => ({
        // bindActionCreators 는 액션함수들을 자동으로 바인딩해줍니다.
        searchListActions: bindActionCreators(searchListActions, dispatch)
    })
)(DatePicker);