import React from "react"
import '../../../css/modal.scss'
import ReactTransitionGroup from "react-addons-css-transition-group";
import {faSignOutAlt} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

const LogOutModal = ({ isOpen, left, right }) => {
    return (
        <>
        {
            isOpen ? (
            <ReactTransitionGroup
                transitionName={'Custom-modal-anim'}
                transitionEnterTimeout={200}
                transitionLeaveTimeout={200} >
                <div className="Custom-modal-overlay" onClick={right} />
                <div className="Custom-modal">
                    <div className="content-without-title">
                        <p>
                            <FontAwesomeIcon icon={faSignOutAlt} size="6x" />
                        </p>
                        <p>Are you sure you want to log out?</p>
                    </div>
                    <div className="button-wrap">
                        <button className="gray form-type left-btn" onClick={left}>
                            Yes
                        </button>
                        <button className="gray form-type right-btn" onClick={right}>
                            No
                        </button>
                    </div>
                </div>
            </ReactTransitionGroup>
            ):(
            <ReactTransitionGroup transitionName={'Custom-modal-anim'} transitionEnterTimeout={200} transitionLeaveTimeout={200} />
            )
        }
        </>
    )
}
export default LogOutModal;