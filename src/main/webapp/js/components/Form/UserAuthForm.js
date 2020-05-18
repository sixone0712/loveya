import React from "react"
import PropTypes from "prop-types";


export default function UserAuthFrom({ sValue, changeFunc}){
    const data = {
        Auth_10_Msg: '10',
        Auth_10_Detail: 'Only file collection available',
        Auth_20_Msg: '20',
        Auth_20_Detail: 'Only EE data viewer is available',
        Auth_50_Msg: '50',
        Auth_50_Detail: 'Both file collection and EE data viewer available',
        Auth_100_Msg: '100',
        Auth_100_Detail: 'Administrators only',
    };

    return(
        <>
            <div className="Custom-ratio">
                <input type="radio" id="auth_10" value="10" checked={sValue=== '10'}  onChange={(e) => {const value = e.target.value; changeFunc(value)}}/>
                <label htmlFor="auth_10">
                    <h2>{data.Auth_10_Msg}</h2>
                    <div>{data.Auth_10_Detail}</div>
                </label>
                <input type="radio" id="auth_20" value="20" checked={sValue === '20'} onChange={(e) => {const value = e.target.value; changeFunc(value)}}/>
                <label htmlFor="auth_20">
                    <h2>{data.Auth_20_Msg}</h2>
                    <div>{data.Auth_20_Detail}</div>
                </label>
                <input type="radio" id="auth_50" value="50" checked={sValue  === '50'} onChange={(e) => {const value = e.target.value; changeFunc(value)}}/>
                <label htmlFor="auth_50">
                    <h2>{data.Auth_50_Msg}</h2>
                    <div>{data.Auth_50_Detail}</div>
                </label>
                <input type="radio" id="auth_100" name="select" value="100" checked={sValue  === '100'}  onChange={(e) => {const value = e.target.value; changeFunc(value)}}/>
                <label htmlFor="auth_100">
                    <h2>{data.Auth_100_Msg}</h2>
                    <div>{data.Auth_100_Detail}</div>
                </label>
            </div>
        </>
    );
}
UserAuthFrom.propTypes = {
    sValue: PropTypes.string.isRequired,
    changeFunc:PropTypes.func.isRequired,
};
