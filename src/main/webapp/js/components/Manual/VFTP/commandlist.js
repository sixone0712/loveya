import React, { useState, useCallback } from "react";
import { Card, CardBody, Col, FormGroup, Button, Input, CustomInput } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTrashAlt, faPencilAlt, faTimes } from "@fortawesome/free-solid-svg-icons";
import ReactTransitionGroup from "react-addons-css-transition-group";

const DEFAULT_COMMAND_ID = "command_0";
const UNIQUE_COMMAND = "not use.";
const initialCommandList = [
    {
        id: "command_1",
        value: "TEST_COMMAND_1234"
    },
    {
        id: "command_2",
        value: "TEST_COMMAND_12345"
    },
    {
        id: "command_3",
        value: "TEST_COMMAND_123456"
    }
];

const RSScommandlist = () => {
    const [commandList, setCommandList] = useState(initialCommandList);
    const [selectCommand, setSelectCommand] = useState(DEFAULT_COMMAND_ID);
    const [actionId, setActionId] = useState("");
    const [currentCommand, setCurrentCommand] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [isNewOpen, setIsNewOpen] = useState(false);
    const [isEditOpen, setIsEditOpen] = useState(false);
    const [isDeleteOpen, setIsDeleteOpen] = useState(false);
    /* const [isAlertOpen, setIsAlertOpen] = useState(false); */

    const handleCommandChange = useCallback(id => {
        setSelectCommand(id);
    }, []);

    const openAddModal = useCallback(() => {
        setIsNewOpen(true);
    }, []);

    const openEditModal = useCallback((id, value) => {
        setIsEditOpen(true);
        setActionId(id);
        setCurrentCommand(value);
    }, []);

    const openDeleteModal = useCallback(id => {
        setIsDeleteOpen(true);
        setActionId(id);
    }, []);

    const closeAddModal = useCallback(() => {
        setIsNewOpen(false);
        setCurrentCommand("");
        setErrorMsg("");
    }, []);

    const closeEditModal = useCallback(() => {
        setIsEditOpen(false);
        setActionId("");
        setCurrentCommand("");
        setErrorMsg("");
    }, []);

    const closeDeleteModal = useCallback(() => {
        setIsDeleteOpen(false);
        setActionId("");
    }, []);

    const onTextChange = useCallback(e => {
        setCurrentCommand(e.target.value);
        setErrorMsg("");
    }, []);

    const addCommand = useCallback(() => {
        const lowerCommand = currentCommand.toLowerCase();
        const duplicateArray = commandList.filter(command => command.value.toLowerCase() === lowerCommand);

        if (duplicateArray.length !== 0 || lowerCommand === UNIQUE_COMMAND) {
            setErrorMsg("This command is duplicate.");
        } else {
            const commandItem = { id: "command_" + commandList.length + 1, value: currentCommand };
            setCommandList(commandList => commandList.concat(commandItem));
            setIsNewOpen(false);
            setCurrentCommand("");
            setErrorMsg("");
        }
    }, [commandList, currentCommand]);

    const saveCommand = useCallback(() => {
        const lowerCommand = currentCommand.toLowerCase();
        const duplicateArray = commandList.filter(command => command.value.toLowerCase() === lowerCommand);

        if ((duplicateArray.length !== 0 && duplicateArray[0].id !== actionId) || lowerCommand === UNIQUE_COMMAND) {
            setErrorMsg("This command is duplicate.");
        } else {
            const result = commandList.filter(command => command.id !== actionId);
            const commandItem = { id: actionId, value: currentCommand };
            result.push(commandItem);
            result.sort((a, b) => a.id.localeCompare(b.id, "en", { numeric: true }));
            setCommandList(result);
            setIsEditOpen(false);
            setActionId("");
            setCurrentCommand("");
            setErrorMsg("");
        }
    }, [commandList, actionId, currentCommand]);

    const deleteCommand = useCallback(() => {
        setCommandList(commandList => commandList.filter(command => command.id !== actionId));
        setSelectCommand(selectCommand === actionId ? DEFAULT_COMMAND_ID : selectCommand);
        setIsDeleteOpen(false);
        setActionId("");
    }, [actionId]);

    return (
        <>
            <Card className="ribbon-wrapper catlist-card command-list manual">
                <CardBody className="custom-scrollbar manual-card-body">
                    <div className="ribbon ribbon-clip ribbon-secondary">Command</div>
                    <Col>
                        <FormGroup className="catlist-form-group">
                            <ul>
                                <li>
                                    <CustomInput
                                        type="radio"
                                        id="command_0"
                                        name="command"
                                        label="not use."
                                        checked={selectCommand === DEFAULT_COMMAND_ID}
                                        onChange={() => handleCommandChange(DEFAULT_COMMAND_ID)}
                                    />
                                </li>
                                <CreateCommandList
                                    commandList={commandList}
                                    selectCommand={selectCommand}
                                    commandChanger={handleCommandChange}
                                    editModal={openEditModal}
                                    deleteModal={openDeleteModal}
                                />
                            </ul>
                        </FormGroup>
                    </Col>
                    <div className="card-btn-area">
                        <Button
                            outline
                            size="sm"
                            color="info"
                            className="catlist-btn"
                            onClick={openAddModal}
                        >
                            Add
                        </Button>
                    </div>
                </CardBody>
            </Card>
            <CreateModal
                value={currentCommand}
                message={errorMsg}
                textChanger={onTextChange}
                newOpen={isNewOpen}
                editOpen={isEditOpen}
                deleteOpen={isDeleteOpen}
                actionNew={addCommand}
                actionEdit={saveCommand}
                actionDelete={deleteCommand}
                closeNew={closeAddModal}
                closeEdit={closeEditModal}
                closeDelete={closeDeleteModal}
            />
        </>
    );
};

const CreateModal = ({
     value,
     message,
     textChanger,
     newOpen,
     editOpen,
     deleteOpen,
     actionNew,
     actionEdit,
     actionDelete,
     closeNew,
     closeEdit,
     closeDelete
 }) => {
    if (newOpen) {
        return (
            <ReactTransitionGroup
                transitionName={"Custom-modal-anim"}
                transitionEnterTimeout={200}
                transitionLeaveTimeout={200}
            >
                <div className="Custom-modal-overlay" onClick={closeNew} />
                <div className="Custom-modal">
                    <p className="title">Add</p>
                    <div className="content-with-title">
                        <FormGroup>
                            <Input
                                type="text"
                                placeholder="Enter command"
                                className="catlist-modal-input"
                                value={value}
                                onChange={textChanger}
                            />
                            <span className={"error" + (message.length > 0 ? " active" : "")}>
                                {message}
                            </span>
                        </FormGroup>
                    </div>
                    <div className="button-wrap">
                        <button className="primary form-type left-btn" onClick={actionNew}>
                            Add
                        </button>
                        <button className="primary form-type right-btn" onClick={closeNew}>
                            Cancel
                        </button>
                    </div>
                </div>
            </ReactTransitionGroup>
        );
    } else if (editOpen) {
        return (
            <ReactTransitionGroup
                transitionName={"Custom-modal-anim"}
                transitionEnterTimeout={200}
                transitionLeaveTimeout={200}
            >
                <div className="Custom-modal-overlay" onClick={closeEdit} />
                <div className="Custom-modal">
                    <p className="title">Edit</p>
                    <div className="content-with-title">
                        <FormGroup>
                            <Input
                                type="text"
                                placeholder="Enter command"
                                className="catlist-modal-input"
                                value={value}
                                onChange={textChanger}
                            />
                            <span className={"error" + (message.length > 0 ? " active" : "")}>
                                {message}
                            </span>
                        </FormGroup>
                    </div>
                    <div className="button-wrap">
                        <button className="primary form-type left-btn" onClick={actionEdit}>
                            Save
                        </button>
                        <button className="primary form-type right-btn" onClick={closeEdit}>
                            Cancel
                        </button>
                    </div>
                </div>
            </ReactTransitionGroup>
        );
    } else if (deleteOpen) {
        return (
            <ReactTransitionGroup
                transitionName={"Custom-modal-anim"}
                transitionEnterTimeout={200}
                transitionLeaveTimeout={200}
            >
                <div className="Custom-modal-overlay" onClick={closeDelete} />
                <div className="Custom-modal">
                    <div className="content-without-title">
                        <p>
                            <FontAwesomeIcon icon={faTrashAlt} size="6x" />
                        </p>
                        <p>Do you want to delete this command?</p>
                    </div>
                    <div className="button-wrap">
                        <button
                            className="primary form-type left-btn"
                            onClick={actionDelete}
                        >
                            Delete
                        </button>
                        <button
                            className="primary form-type right-btn"
                            onClick={closeDelete}
                        >
                            Cancel
                        </button>
                    </div>
                </div>
            </ReactTransitionGroup>
        );
    } else {
        return (
            <ReactTransitionGroup
                transitionName={"Custom-modal-anim"}
                transitionEnterTimeout={200}
                transitionLeaveTimeout={200}
            />
        );
    }
};

const CreateCommandList = React.memo(({ commandList, selectCommand, commandChanger, editModal, deleteModal }) => {
    return (
        <>
            {commandList.map((command, index) => {
                return (
                    <li key={index}>
                        <CustomInput
                            type="radio"
                            id={command.id}
                            name={command.value}
                            label={command.value}
                            checked={selectCommand === command.id}
                            onChange={() => commandChanger(command.id)}
                        />
                        <span className="icon" onClick={() => deleteModal(command.id)}>
                            <FontAwesomeIcon icon={faTimes} />
                        </span>
                        <span className="icon" onClick={() => editModal(command.id, command.value)}>
                            <FontAwesomeIcon icon={faPencilAlt} />
                        </span>
                    </li>
                );
            })}
        </>
    );
});

export default RSScommandlist;
