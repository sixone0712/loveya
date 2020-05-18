import React from "react";
import PropTypes from "prop-types";

export default function InputForm({iType,iLabel, iName, iPlaceholder, changeFunc, iErrMsg}){
    return (
        <div className="password-input-area">
            <label>{iLabel}</label>
            <input
                type={iType}
                name={iName}
                placeholder={iPlaceholder}
                autoComplete='off'
                onChange={(e) => {const {name,value} = e.target; changeFunc(name,value)}}
            />
            <span className="error">{iErrMsg}</span>
        </div>
    );
}

InputForm.propTypes = {
    iType: PropTypes.string.isRequired,
    iLabel: PropTypes.string.isRequired,
    iName: PropTypes.string.isRequired,
    iPlaceholder: PropTypes.string.isRequired,
    changeFunc:PropTypes.func.isRequired,
    iErrMsg:PropTypes.string.isRequired,
};
