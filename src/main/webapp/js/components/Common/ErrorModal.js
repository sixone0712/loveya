import React from "react"
import '../../../css/modal.scss'
import ReactTransitionGroup from "react-addons-css-transition-group";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faExclamationCircle} from "@fortawesome/free-solid-svg-icons";

const ErrorModalOneButton = ({ isOpen, errorMsg,ActionCloseButton }) => {

    const buttonMsg = {
        btnMsg:'Close',
    };
    console.log("ErrorModalOneButton render");

    return <React.Fragment>
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
                                <FontAwesomeIcon icon={faExclamationCircle} size="6x" />
                            </p>
                            <p>{errorMsg}</p>
                        </div>
                        <div className="button-wrap">
                            <button className="gray alert-type"  onClick={ActionCloseButton}>{buttonMsg.btnMsg} </button>
                        </div>
                    </div>
                </ReactTransitionGroup>
            ):(
                <ReactTransitionGroup transitionName={'Modal-anim'} transitionEnterTimeout={200} transitionLeaveTimeout={200} />
            )
        }
    </React.Fragment>
}

export default ErrorModalOneButton;
