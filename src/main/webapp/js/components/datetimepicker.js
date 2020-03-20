import React, { Component } from "react";
import moment from "moment";
import { DatetimePicker } from "rc-datetime-picker";
import { Input, Label } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCalendarCheck } from "@fortawesome/free-regular-svg-icons";

const pickerFlex = {
  display: "flex",
  alignItems: "center",
  justifyContent: "space-between"
};

const labelStyle = {
  fontSize: "14px",
  fontWeight: "300"
};

const inputStyle = {
  fontSize: "14px",
  marginBottom: "0.5rem"
};

class CreateDatetimePicker extends Component {
  constructor(props) {
    super(props);
    const { label, moment } = this.props;
    this.state = {
      label,
      moment
    };
  }

  handleChange = moment => {
    this.setState({
      moment: moment
    });
  };

  render() {
    const { label, moment } = this.state;

    return (
      <>
        <Label style={labelStyle}>
          <FontAwesomeIcon icon={faCalendarCheck} size="lg" /> {label}
        </Label>
        <Input
          type="text"
          style={inputStyle}
          value={moment.format("YYYY-MM-DD HH:mm")}
          className="input-datepicker"
          readOnly
        />
        <DatetimePicker moment={moment} onChange={this.handleChange} />
      </>
    );
  }
}

function RSSdatetimePicker() {
  return (
    <div style={pickerFlex}>
      <div>
        <CreateDatetimePicker
          label={"From"}
          moment={moment()
            .hour(0)
            .minute(0)}
        />
      </div>
      <div>
        <CreateDatetimePicker
          label={"To"}
          moment={moment()
            .hour(23)
            .minute(59)}
        />
      </div>
    </div>
  );
}

export default RSSdatetimePicker;
