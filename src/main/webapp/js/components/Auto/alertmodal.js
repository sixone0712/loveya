import React from "react";
import ReactTransitionGroup from "react-addons-css-transition-group";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

export default function AlertModal(isOpen, icon, message, closer) {
    return (
        <>
            {isOpen ? (
                <ReactTransitionGroup
                    transitionName={"Custom-modal-anim"}
                    transitionEnterTimeout={200}
                    transitionLeaveTimeout={200}
                >
                    <div className="Custom-modal-overlay" />
                    <div className="Custom-modal auto-plan-alert-modal">
                        <div className="content-without-title">
                            <p>
                                <FontAwesomeIcon icon={icon} size="6x" />
                            </p>
                            <p>{message}</p>
                        </div>
                        <div className="button-wrap">
                            <button className="auto-plan alert-type" onClick={closer}>
                                Close
                            </button>
                        </div>
                    </div>
                </ReactTransitionGroup>
            ) : (
                <ReactTransitionGroup
                    transitionName={"Custom-modal-anim"}
                    transitionEnterTimeout={200}
                    transitionLeaveTimeout={200}
                />
            )}
        </>
    );
}
