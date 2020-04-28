import React from "react"
import '../../../css/modal.scss'
import ReactTransitionGroup from "react-addons-css-transition-group";
import {faSignOutAlt} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import ModalTwoButton from "../Common/ModalTwoButton";

const LogOutModal = ({ isOpen, left,right }) => {

    const buttonMsg = {
        leftMsg:'No',
        rightMsg:'Yes',
    };
    const data = {
        bodyMsg:'Log out ?',
    };

    return (
        <React.Fragment>
        {
            isOpen ? (
            <ReactTransitionGroup
                transitionName={'Modal-anim'}
                transitionEnterTimeout={200}
                transitionLeaveTimeout={200} >
                <div className="Custom-modal-overlay" onClick={close} />
                <div className="Custom-modal">
                    <div className="content-without-title">
                        <p>
                            <FontAwesomeIcon icon={faSignOutAlt} size="6x" />
                        </p>
                        <p>{data.bodyMsg}</p>
                    </div>
                    <ModalTwoButton data={buttonMsg} actionRightFunc={right} actionLeftFunc={left}/>
                </div>
            </ReactTransitionGroup>
            ):(
            <ReactTransitionGroup transitionName={'Modal-anim'} transitionEnterTimeout={200} transitionLeaveTimeout={200} />
            )
        }
        </React.Fragment>
    )
}
export default LogOutModal;