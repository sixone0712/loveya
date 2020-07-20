import React, { useRef, useEffect, useState } from "react";
import { Card, CardHeader, CardBody, Button } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {faPlay, faExclamationCircle, faTrashAlt} from "@fortawesome/free-solid-svg-icons";
import ConfirmModal from "../../Common/ConfirmModal";

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

const RSSCommandLine = () => {
    const [isConfirmOpen, setIsConfirmOpen] = useState(false);
    const element = useRef();
    const string = "get 20110811_000000_20110811_235959.log";

    const openModal = () => {
        setIsConfirmOpen(true);
    };

    const closeModal = () => {
        setIsConfirmOpen(false);
    };

    useEffect(() => {
        setTimeout(() => {
            commandWriter(element.current, string).type();
        }, 500);
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
                        Rapid Collector#
                        <span className="command" ref={element} />
                    </div>
                    <Button color="info" outline onClick={openModal}>
                        <FontAwesomeIcon icon={faPlay} />
                    </Button>
                </CardBody>
            </Card>
            <ConfirmModal
                isOpen={isConfirmOpen}
                icon={faExclamationCircle}
                message={"Do you want to execute the command?"}
                leftBtn={"Execute"}
                rightBtn={"Cancel"}
                style={"administrator"}
                actionBg={closeModal}
                actionLeft={closeModal}
                actionRight={closeModal}
            />
        </>
    );
};

export default RSSCommandLine;
