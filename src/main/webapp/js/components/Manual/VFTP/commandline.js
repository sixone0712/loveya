import React, {useRef, useEffect, useState } from "react";
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
let isStopScreen = false;

const RSSCommandLine = ({type, string, modalMsglist, confirmfunc, processfunc, completeFunc, cancelFunc}) => {
    const [modalType, setModalType] = useState("ready");
    const modalTypeRef = useRef("ready");
    const [prevModal, setPrevModal] = useState("ready");
    const [modalMsg, setModalMsg] = useState("");
    const isCompleteRef = useRef(false);
    const isCancelRef = useRef(false);
    const [downloadFile, setDownloadFile] = useState(0);
    const [totalFiles, setTotalFiles] = useState(0);

    const element = useRef();
    const setModalOpen = (type) => {
        modalTypeRef.current = type;
        setPrevModal(modalType);
        setModalType(type);
        switch (type) {
            case "confirm":     isStopScreen = false;setModalMsg(modalMsglist.confirm); break;
            case "cancel":      isStopScreen = true; setModalMsg(modalMsglist.cancel);break;
            case "ready":       isStopScreen = false;setModalMsg("");break;
            case "process":     isStopScreen = false;setModalMsg(modalMsglist.process);break;
            case "complete":    isStopScreen = false;setModalMsg(modalMsglist.complete);break;
        }
    };

    const closeModal = () => {
        setModalOpen("ready") ;
    };

    const cancelModal = async (yesno, type) => {
        const {modalType} = type;
        if (type === "compat/optional") {
            console.log("[cancelModal]yesno: ",yesno);
            console.log("[cancelModal]modalType: ",modalType);
            if(yesno === "yes") {
                closeModal();
                cancelFunc();
            } else {
                (modalType !== "complete") ? setModalOpen(prevModal): closeModal();
            }
        } else {
            if (yesno === "yes") {
                if (!isCompleteRef.current) {
                    isCompleteRef.current = true;
                    cancelFunc();
                }
                closeModal();
            } else {
                if (!isCompleteRef.current) setModalOpen(prevModal);
                else closeModal();
            }
        }
    };

    const completeModal = async () => {
        if (type === "compat/optional") {
            let res = await completeFunc();
            if (res.result !== Define.RSS_SUCCESS) {
                setModalOpen("alert");
                setModalMsg(API.getErrorMsg(result.error));
            } else {
                closeModal();
            }
        }
    };

    const processSequence = () => {
        setTimeout(async () => {
            console.log("==============process==============");
            try {
                let result;
                result = await processfunc();
                if (result.status === "error" || result.status === "done") {
                    clearTimeout(processSequence);
                    if(result.status === "done"  ){
                        (isStopScreen !== true) ? setModalOpen("complete") : setPrevModal("complete");
                    } else {
                        setModalOpen("alert");
                        setModalMsg(API.getErrorMsg(result.error));
                    }
                } else {
                    setDownloadFile(result.downloadedFiles);
                    setTotalFiles(result.totalFiles);
                    processSequence();
                }
            }
            catch (e) {
                console.error(e);
                setModalOpen("alert");
                setModalMsg(API.getErrorMsg(Define.FILE_FAIL_SERVER_ERROR));
            }
        }, 1000);
    };

    const confirmLeftBtnFunc = async () => {
        isCancelRef.current = false;
        isCompleteRef.current = false;

        if(type === "compat/optional") {
            setModalOpen("process");
            let result = await confirmfunc();
            console.log("result ", result);
            if (result.error !== Define.RSS_SUCCESS) {
                setModalOpen("alert");
                setModalMsg(API.getErrorMsg(result.error));
            } else {
                processSequence();
            }
        } else {
            const result = confirmfunc();
            if (result === Define.RSS_SUCCESS) {
                setModalOpen("process");
                try {
                    const res = await processfunc();
                    if (res === Define.RSS_SUCCESS) {
                        if (modalTypeRef.current !== "cancel") setModalOpen("ready");
                    } else {
                        setModalOpen("alert");
                        setModalMsg(API.getErrorMsg(res));
                    }
                } catch (e) {
                    console.error(e);
                    if (!isCancelRef.current) {
                        setModalOpen("alert");
                        setModalMsg(API.getErrorMsg(Define.SEARCH_FAIL_SERVER_ERROR));
                    }
                } finally {
                    isCompleteRef.current = true;
                }
            } else {
                setModalOpen("alert");
                setModalMsg(API.getErrorMsg(result));
            }

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
                        { type === "compat/optional" ? "#get " : "#cd " }
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
                actionLeft={confirmLeftBtnFunc}
                actionRight={closeModal}
            />
            <ConfirmModal isOpen={modalType === "cancel"}
                          icon={faExclamationCircle}
                          message={modalMsg}
                          style={"secondary"}
                          leftBtn={"Yes"}
                          rightBtn={"No"}
                          actionBg={null}
                          actionLeft={()=>cancelModal("yes",{modalType})}
                          actionRight={()=>cancelModal("no",{modalType})}
            />
            <ConfirmModal isOpen={modalType === "complete"}
                          icon={faChevronCircleDown}
                          message={modalMsg}
                          leftBtn={"Save"}
                          rightBtn={"Cancel"}
                          style={"secondary"}
                          actionBg={null}
                          actionLeft={completeModal}
                          actionRight={()=> cancelModal("yes",{modalType})}
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
                            { type === "compat/optional" &&
                                <p>{downloadFile}/{totalFiles }</p>
                            }
                        </div>
                        <div className="button-wrap">
                            <button className="secondary alert-type" onClick={()=> setModalOpen("cancel")}>
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
