import React, { useRef, useEffect, useState } from "react";
import ReactTransitionGroup from "react-addons-css-transition-group";
import { Card, CardHeader, CardBody, Button } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {faPlay, faExclamationCircle, faTrashAlt, faChevronCircleDown} from "@fortawesome/free-solid-svg-icons";
import ConfirmModal from "../../Common/ConfirmModal";
import AlertModal from "../../Common/AlertModal";
import * as Define from "../../../define";
import {ScaleLoader} from "react-spinners";
import * as API from "../../../api";

const commandWriter = (element, string) => {
    const command = string;
    const typeSpeed = 10;
    let cursorPosition = 0;
    let tempTypeSpeed = 0;

    element.innerHTML = "";

    const type = () => {
        tempTypeSpeed = Math.random() * typeSpeed + 30;
        element.innerHTML += command[cursorPosition];
        cursorPosition++;

        if (cursorPosition < command.length) {
            setTimeout(type, tempTypeSpeed);
        }
    };

    return {
        type: type
    };
};

const RSSCommandLine = ({type, string, func, modalMsglist}) => {
    const [modalType, setModalType] = useState("ready");
    const [prevModal, setPrevModal] = useState("ready");
    const [modalMsg, setModalMsg] = useState("");
    const element = useRef();
    const setModalOpen = (type) => {
        setPrevModal(modalType);
        setModalType(type);
        switch (type) {
            case "confirm":     setModalMsg(modalMsglist.confirm); break;
            case "cancel":      setModalMsg(modalMsglist.cancel);break;
            case "ready":       setModalMsg("");break;
            case "process":     setModalMsg(modalMsglist.process);break;
            case "complete":    setModalMsg(modalMsglist.complete);break;
        }
    };
    const closeModal = () => {
        setModalOpen("ready") ;
    };
    const cancelModal = () => {
        setModalOpen("cancel");
        //
    };
    const complete = isave => {
        closeModal();
        if(isave == "save")
        {
            //
        }
    };

    const leftActionfunc = () => {
        let result = 0;
        result  = func();
        console.log("result ", result);
        if(result != Define.RSS_SUCCESS) {
            setModalOpen("alert");
            setModalMsg(API.getErrorMsg(result));
        } else {
            setModalOpen("process");
        }
    };

    useEffect(() => {
        console.log("===RSSCommandLine useEffect===");
        console.log(string);
        setTimeout(() => {
            commandWriter(element.current, string).type();
        }, 500);
        return ()=>{console.log("===cleanup===");}
    }, [string]);
    console.log("[RSSCommandLine]modalType",modalType);
    return (
        <>
            <Card className="ribbon-wrapper command-line-card">
                <div className="ribbon ribbon-clip ribbon-command-line">
                    Current Command
                </div>
                <CardHeader>
                    <p>The following command will be executed.</p>
                </CardHeader>
                <CardBody>
                    <div className="command-line">
                        Rapid Collector
                        { type == "compat/optional" ? "#get " : "#cd " }
                        <span className="command" ref={element} />
                    </div>
                    <Button color="info" outline onClick={()=> setModalOpen("confirm")}>
                        <FontAwesomeIcon icon={faPlay} />
                    </Button>
                </CardBody>
            </Card>
            <ConfirmModal
                isOpen={modalType === "confirm"}
                icon={faExclamationCircle}
                message={modalMsg}
                leftBtn={"Execute"}
                rightBtn={"Cancel"}
                style={"administrator"}
                actionBg={closeModal}
                actionLeft={leftActionfunc}
                actionRight={closeModal}
            />
            <ConfirmModal isOpen={modalType === "cancel"}
                          icon={faExclamationCircle}
                          message={modalMsg}
                          style={"green"}
                          leftBtn={"Yes"}
                          rightBtn={"No"}
                          actionBg={null}
                          actionLeft={closeModal}
                          actionRight={() => setModalOpen(prevModal)}
            />
            <ConfirmModal isOpen={modalType === "complete"}
                          icon={faChevronCircleDown}
                          message={modalMsg}
                          leftBtn={"Save"}
                          rightBtn={"Cancel"}
                          style={"secondary"}
                          actionBg={null}
                          actionLeft={() => complete("save")}
                          actionRight={() => complete("cancel")}
            />
            <AlertModal isOpen={modalType === "alert"} icon={faExclamationCircle} message={modalMsg} style={"green"} closer={closeModal} />
            {modalType === "process" ? (
                <ReactTransitionGroup
                    transitionName={"Custom-modal-anim"}
                    transitionEnterTimeout={200}
                    transitionLeaveTimeout={200}
                >
                    <div className="Custom-modal-overlay" />
                    <div className="Custom-modal">
                        <div className="content-without-title">
                            <div className="spinner-area">
                                <ScaleLoader
                                    loading={true}
                                    height={45}
                                    width={16}
                                    radius={30}
                                    margin={5}
                                />
                            </div>
                            <p>{modalMsg}</p>
                        </div>
                        <div className="button-wrap">
                            <button className="alert-type green" onClick={cancelModal}>
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
};

export default RSSCommandLine;
