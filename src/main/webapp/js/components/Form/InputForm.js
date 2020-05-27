import React from "react";
import PropTypes from "prop-types";

export default function InputForm({iType, iLabel, iName, iId, iPlaceholder, changeFunc, iErrMsg, maxLength}){
    return (
        <div className="password-input-area">
            <label>{iLabel}</label>
            <input
                type={iType}
                name={iName}
                id={iId}
                placeholder={iPlaceholder}
                autoComplete='off'
                maxLength={maxLength}
                onChange={changeFunc}
            />
            <span className="error">{iErrMsg}</span>
        </div>
    );
}

InputForm.propTypes = {
    iType: PropTypes.string.isRequired,
    iLabel: PropTypes.string.isRequired,
    iName: PropTypes.string.isRequired,
    iId: PropTypes.string,
    iPlaceholder: PropTypes.string.isRequired,
    changeFunc:PropTypes.func.isRequired,
    iErrMsg:PropTypes.string,
    maxLength: PropTypes.number
};
