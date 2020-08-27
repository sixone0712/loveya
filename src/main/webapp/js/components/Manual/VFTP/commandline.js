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
import Typewriter from 'typewriter-effect';

const RSSCommandLine = ({type, string, modalMsglist, confirmfunc, processfunc, completeFunc, cancelFunc}) => {
    const [modalType, setModalType] = useState("ready");
    const modalTypeRef = useRef("ready");
    const [prevModal, setPrevModal] = useState("ready");
    const [modalMsg, setModalMsg] = useState("");
    const isCompleteRef = useRef(false);
    const isCancelRef = useRef(false);
    const [downloadFile, setDownloadFile] = useState(0);
    const [totalFiles, setTotalFiles] = useState(0);

    const setModalOpen = (type) => {
        modalTypeRef.current = type;
        setPrevModal(modalType);
        setModalType(type);
        switch (type) {
            case "confirm": setModalMsg(modalMsglist.confirm); break;
            case "cancel": setModalMsg(modalMsglist.cancel);break;
            case "ready": setModalMsg("");break;
            case "process": setModalMsg(modalMsglist.process);break;
            case "complete": setModalMsg(modalMsglist.complete);break;
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
            setDownloadFile(0);
            setTotalFiles(0);
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
                        (modalTypeRef.current !== "cancel") ? setModalOpen("complete") : setPrevModal("complete");
                    } else {
                        setModalOpen("alert");
                        setModalMsg(API.getErrorMsg(result.error));
                    }
                } else {
                    setDownloadFile(result.downloadedFiles);
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
            let result = await confirmfunc();
            console.log("result ", result);
            if (result.error !== Define.RSS_SUCCESS) {
                setModalOpen("alert");
                setModalMsg(API.getErrorMsg(result.error));
            } else {
                setDownloadFile(0);
                setTotalFiles(result.totalFiles);
                setModalOpen("process");
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
                        /*
                        setModalOpen("alert");
                        setModalMsg(API.getErrorMsg(res));
                        */
                    }
                } catch (e) {
                    console.error(e);
                    /*
                    if (!isCancelRef.current) {
                        setModalOpen("alert");
                        setModalMsg(API.getErrorMsg(Define.SEARCH_FAIL_SERVER_ERROR));
                    }
                    */
                } finally {
                    isCompleteRef.current = true;
                }
            } else {
                setModalOpen("alert");
                setModalMsg(API.getErrorMsg(result));
            }

        }
    };

    return (
        <>
            <Card className="ribbon-wrapper command-line-card">
                <div className="ribbon ribbon-clip ribbon-command-line">
                    Current Command
                </div>
                <CardHeader>
                    <p>The following command will be executed.</p>
                    <div className="execute-btn-area">
                        <Button color="info" outline onClick={()=> setModalOpen("confirm")}>
                            <FontAwesomeIcon icon={faPlay} />
                        </Button>
                    </div>
                </CardHeader>
                <CardBody>
                    <div className="command-line">
                        Rapid Collector
                        { type === "compat/optional" ? "# get " : "# cd " }
                        { string }
                    </div>
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
                          style={"administrator"}
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
                          style={"administrator"}
                          actionBg={null}
                          actionLeft={completeModal}
                          actionRight={()=> cancelModal("yes",{modalType})}
            />
            <AlertModal isOpen={modalType === "alert"} icon={faExclamationCircle} message={modalMsg} style={"administrator"} closer={closeModal} />
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
                            <p style={{ marginBottom: 0 }}>{modalMsg}</p>
                            { type === "compat/optional" &&
                                <p>{downloadFile}/{totalFiles}</p>
                            }
                        </div>
                        <div className="button-wrap">
                            <button className="administrator alert-type" onClick={()=> setModalOpen("cancel")}>
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
