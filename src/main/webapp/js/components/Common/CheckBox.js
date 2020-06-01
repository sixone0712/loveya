import React from "react";

const CheckBox = ({index, name, isChecked, labelClass, handleCheckboxClick}) => {
    if (name === null || index === null) {
      return null;
    } else {
        const labelName = labelClass === "filelist-label" ? "" : name;
        const setChecked = isChecked === true;

        return (
            <>
              <input
                  type="checkbox"
                  className="custom-control-input"
                  id={name + "_{#div#}_" + index}
                  value={name}
                  checked={setChecked}
                  onChange={handleCheckboxClick}
              />
              <label
                  className={"custom-control-label " + labelClass}
                  htmlFor={name + "_{#div#}_" + index}
              >
                  {labelName}
              </label>
          </>
        );
    }
}

export default CheckBox;
