import React from "react";
import ReactTransitionGroup from "react-addons-css-transition-group";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

export default function ConfirmModal(isOpen, icon, message, closer, selectedPlanId) {
    return (
        <>
            {isOpen ? (
                <ReactTransitionGroup
                    transitionName={"Custom-modal-anim"}
                    transitionEnterTimeout={200}
                    transitionLeaveTimeout={200}
                >
                    <div className="Custom-modal-overlay" onClick={closer} />
                    <div className="Custom-modal">
                        <div className="content-without-title">
                            <p>
                                <FontAwesomeIcon icon={icon} size="6x" />
                            </p>
                            <p>{message}</p>
                        </div>
                        <div className="button-wrap">
                            <button className="auto-plan form-type left-btn" onClick={ () => closer(true, selectedPlanId) }>
                                OK
                            </button>
                            <button
                                className="auto-plan form-type right-btn"
                                onClick={ () => closer(false, selectedPlanId) }
                            >
                                Cancel
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
