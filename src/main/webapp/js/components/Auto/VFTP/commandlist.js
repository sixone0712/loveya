import React, { useState, useCallback } from "react";
import { Col, FormGroup, ButtonToggle, Button, Input } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch, faExclamationCircle, faTrashAlt, faPencilAlt, faTimes } from "@fortawesome/free-solid-svg-icons";
import ReactTransitionGroup from "react-addons-css-transition-group";
import Checkbox from "../../Common/CheckBox";

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

const RSSautoCommandList = () => {
    const [commandList, setCommandList] = useState(initialCommandList);
    const [checkedList, setCheckedList] = useState([]);
    const [query, setQuery] = useState("");
    const [itemsChecked, setItemsChecked] = useState(false);
    const [showSearch, setShowSearch] = useState(false);
    const [actionId, setActionId] = useState("");
    const [currentCommand, setCurrentCommand] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [isNewOpen, setIsNewOpen] = useState(false);
    const [isEditOpen, setIsEditOpen] = useState(false);
    const [isDeleteOpen, setIsDeleteOpen] = useState(false);

    const handleSearchToggle = useCallback(() => {
        setShowSearch(!showSearch);
        setQuery("");
    }, [showSearch]);

    const selectItem = useCallback(() => {
        const collection = [];

        if (!itemsChecked) {
            commandList.map(command => collection.push(command.id));
        }

        setCheckedList(collection);
        setItemsChecked(!itemsChecked);
    }, [commandList, itemsChecked]);

    const handleCheckboxClick = useCallback(e => {
        const { id, checked } = e.target;
        let newList = [];

        if (checked) {
            newList = [...checkedList, id];
        } else {
            newList = checkedList.filter(item => item !== id);
        }

        setCheckedList(newList);
        setItemsChecked(commandList.length === newList.length);
    }, [commandList, checkedList]);

    const handleSearch = useCallback(e => {
        setQuery(e.target.value);
    }, []);

    const addCommand = useCallback(() => {
        const duplicateArray = commandList.filter(command => command.value.toLowerCase() === currentCommand.toLowerCase());

        if (duplicateArray.length !== 0) {
            setErrorMsg("This command is duplicate.");
        } else {
            const commandItem = { id: "command_" + commandList.length + 1, value: currentCommand };
            setCommandList(commandList.concat(commandItem));
            setItemsChecked(false);
            setIsNewOpen(false);
            setCurrentCommand("");
            setErrorMsg("");
        }
    }, [commandList, currentCommand]);

    const saveCommand = useCallback(() => {
        const duplicateArray = commandList.filter(command => command.value.toLowerCase() === currentCommand.toLowerCase());

        if (duplicateArray.length !== 0) {
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
        const newCommandList = commandList.filter(command => command.id !== actionId);
        const newCheckedList = checkedList.filter(item => item !== actionId);

        setCommandList(newCommandList);
        setCheckedList(newCheckedList);
        setItemsChecked(newCommandList.length === newCheckedList.length);
        setIsDeleteOpen(false);
        setActionId("");
    }, [commandList, checkedList, actionId]);

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

    const onTextChange = useCallback(e => {
        setCurrentCommand(e.target.value);
        setErrorMsg("");
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

    return (
        <>
            <div className="form-section targetlist">
                <Col className="pdl-10 pdr-0">
                    <div className="form-header-section">
                        <div className="form-title-section">
                            Command List
                            <p>Select a command from the list.</p>
                        </div>
                        <CreateButtonArea
                            isOpen={showSearch}
                            searchToggler={handleSearchToggle}
                            searchText={query}
                            textChanger={handleSearch}
                            isChecked={itemsChecked}
                            itemToggler={selectItem}
                            openModal={openAddModal}
                        />
                    </div>
                    <CreateCommandList
                        commandList={commandList}
                        checkedList={checkedList}
                        checkHandler={handleCheckboxClick}
                        query={query}
                        editModal={openEditModal}
                        deleteModal={openDeleteModal}
                    />
                </Col>
            </div>
            <CreateModal
                value={currentCommand}
                message={errorMsg}
                textChanger={onTextChange}
                addOpen={isNewOpen}
                editOpen={isEditOpen}
                deleteOpen={isDeleteOpen}
                actionAdd={addCommand}
                actionEdit={saveCommand}
                actionDelete={deleteCommand}
                closeAdd={closeAddModal}
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
     addOpen,
     editOpen,
     deleteOpen,
     actionAdd,
     actionEdit,
     actionDelete,
     closeAdd,
     closeEdit,
     closeDelete
 }) => {
    if (addOpen) {
        return (
            <ReactTransitionGroup
                transitionName={"Custom-modal-anim"}
                transitionEnterTimeout={200}
                transitionLeaveTimeout={200}
            >
                <div className="Custom-modal-overlay" onClick={closeAdd} />
                <div className="Custom-modal auto-plan-confirm-modal">
                    <p className="title">Add</p>
                    <div className="content-with-title">
                        <FormGroup>
                            <Input
                                type="text"
                                placeholder="Enter command"
                                className="command-modal-input"
                                value={value}
                                onChange={textChanger}
                            />
                            <span className={"error" + (message.length > 0 ? " active" : "")}>
                                {message}
                            </span>
                        </FormGroup>
                    </div>
                    <div className="button-wrap">
                        <button className="auto-plan form-type left-btn" onClick={actionAdd}>
                            Add
                        </button>
                        <button className="auto-plan form-type right-btn" onClick={closeAdd}>
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
                <div className="Custom-modal auto-plan-confirm-modal">
                    <p className="title">Edit</p>
                    <div className="content-with-title">
                        <FormGroup>
                            <Input
                                type="text"
                                placeholder="Enter command"
                                className="command-modal-input"
                                value={value}
                                onChange={textChanger}
                            />
                            <span className={"error" + (message.length > 0 ? " active" : "")}>
                                {message}
                            </span>
                        </FormGroup>
                    </div>
                    <div className="button-wrap">
                        <button className="auto-plan form-type left-btn" onClick={actionEdit}>
                            Save
                        </button>
                        <button className="auto-plan form-type right-btn" onClick={closeEdit}>
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
                        <p><FontAwesomeIcon icon={faTrashAlt} size="6x" /></p>
                        <p>Do you want to delete command?</p>
                    </div>
                    <div className="button-wrap">
                        <button className="auto-plan form-type left-btn" onClick={actionDelete}>
                            Delete
                        </button>
                        <button className="auto-plan form-type right-btn" onClick={closeDelete}>
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

const CreateButtonArea = React.memo(({
     isOpen,
     searchToggler,
     searchText,
     textChanger,
     isChecked,
     itemToggler,
     openModal
 }) => {
        return (
            <div className="form-btn-section dis-flex">
                <div className={"search-btn-area" + (isOpen ? " active" : "")}>
                    <ButtonToggle
                        outline
                        size="sm"
                        color="info"
                        className={"form-btn" + (isOpen ? " active" : "")}
                        onClick={searchToggler}
                    >
                        <FontAwesomeIcon icon={faSearch} />
                    </ButtonToggle>
                    <FormGroup>
                        <Input
                            type="text"
                            className="form-search-input"
                            placeholder="Enter the command to search."
                            value={searchText}
                            onChange={textChanger}
                        />
                    </FormGroup>
                </div>
                <ButtonToggle
                    outline
                    size="sm"
                    className={"form-btn toggle-all" + (isChecked ? " active" : "")}
                    onClick={itemToggler}
                >
                    All
                </ButtonToggle>
                <Button outline size="sm" className="form-btn" onClick={openModal}>
                    Add
                </Button>
            </div>
        );
    }
);

const CreateCommandList = React.memo(({
    commandList,
    checkedList,
    checkHandler,
    query,
    editModal,
    deleteModal
}) => {
        let filteredData = [];

        if (query.length > 0) {
            filteredData = commandList.filter(command =>
                command.value.toLowerCase().includes(query.toLowerCase())
            );
        } else {
            filteredData = commandList;
        }

        console.log(checkedList);

        return (
            <FormGroup className={"custom-scrollbar auto-plan-form-group pd-5 command-list" + (filteredData.length > 0 ? "" : " targetlist")}>
                {filteredData.length > 0 ? (
                    <ul>
                        {commandList.map((command, index) => {
                            return (
                                <li className="custom-control custom-checkbox" key={index}>
                                    <input
                                        type="checkbox"
                                        className="custom-control-input"
                                        id={command.id}
                                        value={command.value}
                                        checked={checkedList.includes(command.id)}
                                        onChange={checkHandler}
                                    />
                                    <label
                                        className="custom-control-label form-check-label"
                                        htmlFor={command.id}
                                    >
                                        {command.value}
                                    </label>
                                    <span className="icon" onClick={() => deleteModal(command.id)}>
                                        <FontAwesomeIcon icon={faTimes} />
                                    </span>
                                    <span className="icon" onClick={() => editModal(command.id, command.value)}>
                                        <FontAwesomeIcon icon={faPencilAlt} />
                                    </span>
                                </li>
                            );
                        })}
                    </ul>
                ) : (
                    <div style={{ alignSelf: "center", textAlign: "center", margin: "auto" }}>
                        <p><FontAwesomeIcon icon={faExclamationCircle} size="8x" /></p>
                        <p>Command not found.</p>
                    </div>
                )}
            </FormGroup>
        );
    }
);

export default RSSautoCommandList;