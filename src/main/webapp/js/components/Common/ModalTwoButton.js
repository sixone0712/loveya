import React from "react"
import '../../../css/modal.scss'
import PropTypes from "prop-types";

const ModalTwoButton = ({ data, actionLeftFunc,actionRightFunc}) => {
    return (
        <div className="button-wrap">
            <button className="gray form-type left-btn"  onClick={actionLeftFunc}>{data.rightMsg} </button>
            <button className="gray form-type right-btn"  onClick={actionRightFunc}>{data.leftMsg} </button>
        </div>
    )
}

export default ModalTwoButton;

ModalTwoButton.propTypes = {
    data: PropTypes.object.isRequired,
    actionRightFunc: PropTypes.func.isRequired,
    actionLeftFunc: PropTypes.func.isRequired,
};
