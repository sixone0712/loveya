import React, {useState, useCallback, useEffect} from "react";
import { Col, FormGroup, ButtonToggle, Button, Input } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch, faExclamationCircle, faTrashAlt, faPencilAlt, faTimes } from "@fortawesome/free-solid-svg-icons";
import ReactTransitionGroup from "react-addons-css-transition-group";
import { connect } from "react-redux";
import { bindActionCreators } from "redux";
import * as commandActions from "../../../modules/command";
import * as Define from "../../../define";
import services from "../../../services";

const modalType = {
  NEW: 1,
  EDIT: 2
};

const RSSautoCommandList = ({ type, command, commandActions }) => {
    const commandList = command.get("lists").toJS().filter(command => command.cmd_type === type);
    const [query, setQuery] = useState("");
    const [itemsChecked, setItemsChecked] = useState(false);
    const [checkedCount, setCheckedCount] = useState(0);
    const [showSearch, setShowSearch] = useState(false);
    const [actionId, setActionId] = useState("");
    const [currentDataType, setCurrentDataType] = useState("");
    const [currentContext, setCurrentContext] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [isNewOpen, setIsNewOpen] = useState(false);
    const [isEditOpen, setIsEditOpen] = useState(false);
    const [isDeleteOpen, setIsDeleteOpen] = useState(false);
    const [isErrorOpen, setIsErrorOpen] = useState(false);
    const [openedModal, setOpenedModal] = useState("");

    const handleSearchToggle = useCallback(() => {
        setShowSearch(!showSearch);
        setQuery("");
    }, [showSearch]);

    const selectItem = useCallback(() => {
        commandActions.commandCheckAllList(!itemsChecked);
        setItemsChecked(!itemsChecked);
        setCheckedCount(!itemsChecked ? commandList.length : 0);
    }, [itemsChecked, commandList]);

    const handleCheckboxClick = useCallback(e => {
        const { id, checked } = e.target;
        const newCount = checked ? checkedCount + 1 : checkedCount - 1;
        commandActions.commandCheckList(parseInt(id));
        setItemsChecked(commandList.length === newCount);
        setCheckedCount(newCount);
    }, [commandList, checkedCount]);

    const handleSearch = useCallback(e => {
        setQuery(e.target.value);
    }, []);

    const addCommand = useCallback(async () => {
        if (invalidCheck(modalType.NEW)) {
            setIsNewOpen(false);
            setTimeout(() => {
                setIsErrorOpen(true);
            }, 500);
        } else {
            let currentCommand = "";

            if (type === Define.PLAN_TYPE_VFTP_COMPAT) {
                currentCommand = "%s-%s-" + currentContext;
            } else {
                currentCommand = currentContext.length > 0 ? currentDataType + "-%s-%s-" + currentContext : currentDataType + "-%s-%s";
            }

            const duplicateArray = commandList.filter(command => command.cmd_name.toLowerCase() === currentCommand.toLowerCase());

            if (duplicateArray.length !== 0) {
                setErrorMsg("This command is duplicate.");
                setOpenedModal(modalType.NEW);
                setIsNewOpen(false);
                setTimeout(() => {
                    setIsErrorOpen(true);
                }, 500);
            } else {
                const commandItem = { cmd_name: currentCommand, cmd_type: type };
                try {
                    const res = await services.axiosAPI.requestPost("/rss/api/vftp/command", commandItem);
                    console.log(res);
                } catch (e) {
                    console.log(e.message());
                }
                setItemsChecked(false);
                setIsNewOpen(false);
                setCurrentContext("");
                setCurrentDataType("");
                setErrorMsg("");
                setOpenedModal("");
                await commandActions.commandLoadList("/rss/api/vftp/command?type=" + type);
            }
        }
    }, [commandList, currentContext, currentDataType]);

    const saveCommand = useCallback(async () => {
        if (invalidCheck(modalType.EDIT)) {
            setIsEditOpen(false);
            setTimeout(() => {
                setIsErrorOpen(true);
            }, 500);
        } else {
            let currentCommand = "";

            if (type === Define.PLAN_TYPE_VFTP_COMPAT) {
                currentCommand = "%s-%s-" + currentContext;
            } else {
                currentCommand = currentContext.length > 0 ? currentDataType + "-%s-%s-" + currentContext : currentDataType + "-%s-%s";
            }

            const duplicateArray = commandList.filter(command => {
                if (command.index !== actionId) {
                    return command.cmd_name.toLowerCase() === currentCommand.toLowerCase();
                }
            });

            if (duplicateArray.length !== 0) {
                setErrorMsg("This command is duplicate.");
                setOpenedModal(modalType.EDIT);
                setIsEditOpen(false);
                setTimeout(() => {
                    setIsErrorOpen(true);
                }, 500);
            } else {
                const commandItem = { cmd_name: currentCommand };

                try {
                    const res = await services.axiosAPI.requestPut(`/rss/api/vftp/command/${actionId}`, commandItem);
                    console.log(res);
                } catch (e) {
                    console.log(e.message());
                }
                setIsEditOpen(false);
                setActionId("");
                setCurrentContext("");
                setCurrentDataType("");
                setErrorMsg("");
                setOpenedModal("");
                await commandActions.commandLoadList("/rss/api/vftp/command?type=" + type);
            }
        }
    }, [commandList, actionId, currentContext, currentDataType]);

    const deleteCommand = useCallback(async () => {
        const deleteItem = commandList.find(command => command.id === actionId);
        let newCount = checkedCount;

        try {
            const res = await services.axiosAPI.requestDelete(`/rss/api/vftp/command/${actionId}`);
            console.log(res);

            if (res.status === 200) {
                commandActions.commandDeleteItem(actionId);
                if (deleteItem.checked) {
                    newCount--;
                }
                setItemsChecked(commandList.length - 1 < 1 ? false : commandList.length - 1 === newCount);
                setCheckedCount(newCount);
            }
        } catch (e) {
            console.log(e.message());
        }
        setIsDeleteOpen(false);
        setActionId("");
        await commandActions.commandLoadList("/rss/api/vftp/command?type=" + type);
    }, [commandList, actionId, checkedCount]);

    const invalidCheck = useCallback((modal) => {
        if (type === Define.PLAN_TYPE_VFTP_SSS) {
            if (currentDataType === "") {
                setErrorMsg("Data type is invalid.");
                setOpenedModal(modal);
                return true;
            }
        } else {
            if (currentContext === "") {
                setErrorMsg("Context is invalid.");
                setOpenedModal(modal);
                return true;
            }
        }

        return false;
    }, [currentContext, currentDataType]);

    const onContextChange = useCallback(e => {
        setCurrentContext(e.target.value);
    }, []);

    const onDataTypeChange = useCallback(e=> {
        setCurrentDataType(e.target.value);
    }, []);

    const openAddModal = useCallback(() => {
        setIsNewOpen(true);
    }, []);

    const openEditModal = useCallback((id, value) => {
        setIsEditOpen(true);
        setActionId(id);
        if (type === Define.PLAN_TYPE_VFTP_COMPAT) {
            setCurrentContext(value.replace("%s-%s-", ""));
        } else {
            if (value.endsWith("%s")) {
                setCurrentDataType(value.replace("-%s-%s", ""));
            } else {
                setCurrentDataType(value.split("-%s-%s-")[0]);
                setCurrentContext(value.split("-%s-%s-")[1]);
            }
        }
    }, []);

    const openDeleteModal = useCallback(id => {
        setIsDeleteOpen(true);
        setActionId(id);
    }, []);

    const closeAddModal = useCallback(() => {
        setIsNewOpen(false);
        setCurrentDataType("");
        setCurrentContext("");
    }, []);

    const closeEditModal = useCallback(() => {
        setIsEditOpen(false);
        setActionId("");
        setCurrentDataType("");
        setCurrentContext("");
    }, []);

    const closeDeleteModal = useCallback(() => {
        setIsDeleteOpen(false);
        setActionId("");
    }, []);

    const closeErrorModal = useCallback(() => {
        setIsErrorOpen(false);
        setTimeout(() => {
            if (openedModal === modalType.NEW) {
                setIsNewOpen(true);
            } else {
                setIsEditOpen(true);
            }
        }, 500);
    }, [openedModal]);

    useEffect(() => {
        commandActions.commandInit();
        commandActions.commandLoadList("/rss/api/vftp/command?type=" + type);
    }, [])

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
                        checkHandler={handleCheckboxClick}
                        query={query}
                        editModal={openEditModal}
                        deleteModal={openDeleteModal}
                        type={type}
                    />
                </Col>
            </div>
            <CreateModal
                listType={type}
                dataType={currentDataType}
                context={currentContext}
                dataTypeChanger={onDataTypeChange}
                contextChanger={onContextChange}
                addOpen={isNewOpen}
                editOpen={isEditOpen}
                deleteOpen={isDeleteOpen}
                errorOpen={isErrorOpen}
                actionAdd={addCommand}
                actionEdit={saveCommand}
                actionDelete={deleteCommand}
                closeAdd={closeAddModal}
                closeEdit={closeEditModal}
                closeDelete={closeDeleteModal}
                closeError={closeErrorModal}
                msg={errorMsg}
            />
        </>
    );
};

const CreateModal = ({
    listType,
    dataType,
    context,
    dataTypeChanger,
    contextChanger,
    addOpen,
    editOpen,
    deleteOpen,
    errorOpen,
    actionAdd,
    actionEdit,
    actionDelete,
    closeAdd,
    closeEdit,
    closeDelete,
    closeError,
    msg
 }) => {
    if (addOpen) {
        return (
            <ReactTransitionGroup
                transitionName={"Custom-modal-anim"}
                transitionEnterTimeout={200}
                transitionLeaveTimeout={200}
            >
                <div className="Custom-modal-overlay" onClick={closeAdd} />
                <div className="Custom-modal auto-plan-confirm-modal command">
                    <p className="title">Add</p>
                    <div className="content-with-title">
                        <FormGroup className={"command-input-modal" + (listType === Define.PLAN_TYPE_VFTP_COMPAT ? " hidden" : "")}>
                            <label>Data type</label>
                            <Input
                                type="text"
                                placeholder="Enter data type"
                                value={dataType}
                                onChange={dataTypeChanger}
                            />
                        </FormGroup>
                        <FormGroup className="command-input-modal">
                            <label>Context</label>
                            <Input
                                type="text"
                                placeholder="Enter context"
                                value={context}
                                onChange={contextChanger}
                            />
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
                <div className="Custom-modal auto-plan-confirm-modal command">
                    <p className="title">Edit</p>
                    <div className="content-with-title">
                        <FormGroup className={"command-input-modal" + (listType === Define.PLAN_TYPE_VFTP_COMPAT ? " hidden" : "")}>
                            <label>Data type</label>
                            <Input
                                type="text"
                                placeholder="Enter data type"
                                value={dataType}
                                onChange={dataTypeChanger}
                            />
                        </FormGroup>
                        <FormGroup className="command-input-modal">
                            <label>Context</label>
                            <Input
                                type="text"
                                placeholder="Enter context"
                                value={context}
                                onChange={contextChanger}
                            />
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
                <div className="Custom-modal-overlay" onClick={closeDelete}/>
                <div className="Custom-modal">
                    <div className="content-without-title">
                        <p><FontAwesomeIcon icon={faTrashAlt} size="6x"/></p>
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
    } else if (errorOpen) {
        return (
            <ReactTransitionGroup
                transitionName={"Custom-modal-anim"}
                transitionEnterTimeout={200}
                transitionLeaveTimeout={200}
            >
                <div className="Custom-modal-overlay" />
                <div className="Custom-modal">
                    <div className="content-without-title">
                        <p><FontAwesomeIcon icon={faExclamationCircle} size="6x" /></p>
                        <p>{msg}</p>
                    </div>
                    <div className="button-wrap">
                        <button className="auto-plan alert-type" onClick={closeError}>
                            Close
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
    checkHandler,
    query,
    editModal,
    deleteModal,
    type
}) => {
        let filteredData = [];
        const regex = /[-|%s]/g;

        if (query.length > 0) {
            filteredData = commandList.filter(command => command.cmd_name.replace(regex, "").toLowerCase().includes(query.toLowerCase()));
        } else {
            filteredData = commandList;
        }

        return (
            <FormGroup className={"custom-scrollbar auto-plan-form-group pd-5 command-list" + (filteredData.length > 0 ? "" : " targetlist")}>
                {filteredData.length > 0 ? (
                    <ul>
                        {filteredData.map((command, index) => {
                            let displayCommand = "";
                            if (type === Define.PLAN_TYPE_VFTP_COMPAT) {
                                displayCommand = command.cmd_name.replace("%s-%s-", "");
                            } else {
                                if (command.cmd_name.endsWith("%s")) {
                                    displayCommand = command.cmd_name.replace("-%s-%s", "");
                                } else {
                                    displayCommand = command.cmd_name.replace("-%s-%s-", "-");
                                }
                            }
                            return (
                                <li className="custom-control custom-checkbox" key={index}>
                                    <input
                                        type="checkbox"
                                        className="custom-control-input"
                                        id={command.id}
                                        value={command.cmd_name}
                                        checked={command.checked}
                                        onChange={checkHandler}
                                    />
                                    <label
                                        className="custom-control-label form-check-label"
                                        htmlFor={command.id}
                                    >
                                        {displayCommand}
                                    </label>
                                    <span className="icon" onClick={() => deleteModal(command.id)}>
                                    <FontAwesomeIcon icon={faTimes}/>
                                    </span>
                                        <span className="icon" onClick={() => editModal(command.id, command.cmd_name)}>
                                        <FontAwesomeIcon icon={faPencilAlt}/>
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

export default connect(
    (state) => ({ command: state.command.get('command') }),
    (dispatch) => ({ commandActions: bindActionCreators(commandActions, dispatch) })
)(RSSautoCommandList);