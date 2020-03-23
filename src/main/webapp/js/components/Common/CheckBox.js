import React, { Component } from "react";

class CheckBox extends Component {
    constructor() {
        super();
    }

  render() {
    const { index, name, isChecked, labelClass, handleCheckboxClick } = this.props;

    let labelName = name;
    if(labelClass === "filelist-label"){
        labelName ="";
    }

    let setChecked = false;
    if(isChecked) {
        setChecked = true;
    }

    return (
      <div className="custom-control custom-checkbox">
        <input
          type="checkbox"
          className="custom-control-input"
          id={name+"_"+index}
          value={name || ""}
          checked={setChecked}
          onChange={handleCheckboxClick}
        />

        <label
            className={"custom-control-label " + labelClass}
            htmlFor={name + "_" + index}
        >
            {labelName}
        </label>

      </div>
    );
  }
}

export default CheckBox;
