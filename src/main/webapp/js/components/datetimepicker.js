import React, { Component } from "react";
import moment from "moment";
import { DatetimePicker } from "rc-datetime-picker";
import { Input, Label } from "reactstrap";

const pickerFlex = {
  display: "flex",
  alignItems: "center",
  justifyContent: "space-between"
};

const labelStyle = {
  fontSize: "14px"
};

const inputStyle = {
  fontSize: "14px",
  marginBottom: "0.5rem"
};

class CreateDatetimePicker extends Component {
  constructor(props) {
    super(props);
    this.state = {
      moment: moment()
        .hour(0)
        .minute(0)
    };
  }

  handleChange = moment => {
    this.setState({
      moment
    });
  };

  render() {
    const { label } = this.props;

    return (
      <>
        <Label style={labelStyle}>{label}</Label>
        <Input
          type="text"
          style={inputStyle}
          value={this.state.moment.format("YYYY-MM-DD HH:mm")}
          className="input-datepicker"
          readOnly
        />
        <DatetimePicker
          moment={this.state.moment}
          onChange={this.handleChange}
        />
      </>
    );
  }
}

function RSSdatetimePicker() {
  return (
    <div style={pickerFlex}>
      <div>
        <CreateDatetimePicker label={"From"} />
      </div>
      <div>
        <CreateDatetimePicker label={"To"} />
      </div>
    </div>
  );
}

export default RSSdatetimePicker;
