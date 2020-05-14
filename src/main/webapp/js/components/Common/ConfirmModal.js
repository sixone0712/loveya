import React from "react";
import ReactTransitionGroup from "react-addons-css-transition-group";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

export default function ConfirmModal(isOpen, icon, message, style, actionBg, actionLeft, actionRight) {
    return (
        <>
            {isOpen ? (
                <ReactTransitionGroup
                    transitionName={"Custom-modal-anim"}
                    transitionEnterTimeout={200}
                    transitionLeaveTimeout={200}
                >
                    <div className="Custom-modal-overlay" onClick={actionBg} />
                    <div className="Custom-modal">
                        <div className="content-without-title">
                            <p>
                                <FontAwesomeIcon icon={icon} size="6x" />
                            </p>
                            <p>{message}</p>
                        </div>
                        <div className="button-wrap">
                            <button className={"form-type left-btn " + style} onClick={actionLeft}>
                                OK
                            </button>
                            <button
                                className={"form-type right-btn " + style}
                                onClick={actionRight}
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
