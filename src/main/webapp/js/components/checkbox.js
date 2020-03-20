import React, { Component } from "react";

class Checkbox extends Component {
  render() {
    const { item, isChecked, labelClass, handleCheckboxClick } = this.props;

    return (
      <div className="custom-control custom-checkbox">
        <input
          type="checkbox"
          className="custom-control-input"
          id={item.id}
          value={item.value}
          checked={isChecked}
          onChange={handleCheckboxClick}
        />
        <label
          className={"custom-control-label " + labelClass}
          htmlFor={item.id}
        >
          {item.name}
        </label>
      </div>
    );
  }
}

export default Checkbox;
