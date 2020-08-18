import React, {useState, useCallback} from "react";
import { Card, CardBody, Col, FormGroup, Button, Input, CustomInput } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {faTrashAlt, faPencilAlt, faTimes, faExclamationCircle} from "@fortawesome/free-solid-svg-icons";
import ReactTransitionGroup from "react-addons-css-transition-group";
import { connect } from "react-redux";
import {bindActionCreators} from "redux";
import { propsCompare, stringBytes } from "../../Common/CommonFunction";
import * as commandActions from "../../../modules/command";
import services from "../../../services";
import * as API from "../../../api";
import * as Define from "../../../define";

const UNIQUE_COMMAND = "not use.";
const MAX_STRING_BYTES = 980;
const regex = /(-)|(%s)/g;
const modalType = { NEW: 1, EDIT: 2 };

const RSScommandlist = ({ cmdType, dbCommand, commandActions }) => {
    const commandList = API.vftpConvertDBCommand(dbCommand.get("lists").toJS());
    const [selectCommand, setSelectCommand] = useState(-1);
    const [actionId, setActionId] = useState(-1);
    const [currentDataType, setCurrentDataType] = useState("");
    const [currentContext, setCurrentContext] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [isNewOpen, setIsNewOpen] = useState(false);
    const [isEditOpen, setIsEditOpen] = useState(false);
    const [isDeleteOpen, setIsDeleteOpen] = useState(false);
    const [isErrorOpen, setIsErrorOpen] = useState(false);
    const [openedModal, setOpenedModal] = useState("");

    const handleCommandChange = useCallback(id => {
        if(id === -1) {
            commandActions.commandCheckAllList(false);
        } else {
            commandActions.commandCheckOnlyOneList(id);
        }
        setSelectCommand(id);
    }, []);

    const openAddModal = useCallback(() => {
        setIsNewOpen(true);
    }, []);

    const openEditModal = useCallback((id, value) => {
        setIsEditOpen(true);
        setActionId(id);
        if ((value.match(/-/g) || []).length > 0) {
            setCurrentDataType(value.slice(0, value.indexOf("-")));
            setCurrentContext(value.slice(value.indexOf("-") + 1, value.length));
        } else {
            setCurrentContext(value);
        }
    }, []);

    const openDeleteModal = useCallback(id => {
        setIsDeleteOpen(true);
        setActionId(id);
    }, []);

    const closeAddModal = useCallback(() => {
        setIsNewOpen(false);
        setErrorMsg("");
        setCurrentDataType("");
        setCurrentContext("");
    }, []);

    const closeEditModal = useCallback(() => {
        setIsEditOpen(false);
        setActionId(-1);
        setErrorMsg("");
        setCurrentDataType("");
        setCurrentContext("");
    }, []);

    const closeDeleteModal = useCallback(() => {
        setIsDeleteOpen(false);
        setActionId(-1);
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

    const onContextChange = useCallback(e => { setCurrentContext(e.target.value); }, []);
    const onDataTypeChange = useCallback(e=> { setCurrentDataType(e.target.value); }, []);

    const invalidCheck = useCallback((modal) => {
        const currentCommand = cmdType === Define.PLAN_TYPE_VFTP_SSS ? currentContext : currentDataType + currentContext;
        if (stringBytes(currentCommand) > MAX_STRING_BYTES) {
            setErrorMsg("This command is too long.");
            setOpenedModal(modal);
            return true;
        } else {
            if (cmdType === Define.PLAN_TYPE_VFTP_SSS) {
                if (currentDataType === "") {
                    setErrorMsg("Data type is empty.");
                    setOpenedModal(modal);
                    return true;
                }
            } else {
                if (currentContext === "") {
                    setErrorMsg("Context is empty.");
                    setOpenedModal(modal);
                    return true;
                }
            }
        }
        return false;
    }, [currentContext, currentDataType]);

    const setCurrentCommand = useCallback(() => {
        if (cmdType === Define.PLAN_TYPE_VFTP_COMPAT) {
            return "%s-%s-" + currentContext;
        } else {
            if (currentContext.length > 0) {
                return currentDataType + "-%s-%s-" + currentContext;
            } else {
                return currentDataType + "-%s-%s";
            }
        }
    }, [currentContext, currentDataType]);

    const addCommand = useCallback(async () => {
        if (invalidCheck(modalType.NEW)) {
            setIsNewOpen(false);
            setTimeout(() => { setIsErrorOpen(true); }, 500);
        } else {
            const currentCommand = setCurrentCommand();
            const duplicateArray = commandList.filter(command => command.cmd_name.toLowerCase() === currentCommand.toLowerCase().replace(regex, ""));

            if (duplicateArray.length !== 0 || currentCommand.toLowerCase() === UNIQUE_COMMAND) {
                setErrorMsg("This command is duplicate.");
                setOpenedModal(modalType.NEW);
                setIsNewOpen(false);
                setTimeout(() => { setIsErrorOpen(true); }, 500);
            } else {
                const addData = {
                    cmd_name: currentCommand,
                    cmd_type: cmdType
                }
                try {
                    const res = await services.axiosAPI.requestPost("/rss/api/vftp/command", addData);
                    const {data: {id}} = res;
                    await commandActions.commandLoadList(`/rss/api/vftp/command?type=${cmdType}`);
                    await commandActions.commandCheckOnlyOneList(id);

                    setSelectCommand(id)
                    setIsNewOpen(false);
                    setCurrentContext("");
                    setCurrentDataType("");
                    setErrorMsg("");
                    setOpenedModal("");
                } catch (e) {
                    // 에러 처리
                    console.error(e);
                }
            }
        }
    }, [commandList]);

    const editCommand = useCallback(async () => {
        if (invalidCheck(modalType.EDIT)) {
            setIsEditOpen(false);
            setTimeout(() => { setIsErrorOpen(true); }, 500);
        } else {
            const currentCommand = setCurrentCommand();
            const duplicateArray = commandList.filter(command => command.cmd_name.toLowerCase() === currentCommand.toLowerCase().replace(regex, ""));

            if ((duplicateArray.length !== 0 && duplicateArray[0].id !== actionId) || currentCommand.toLowerCase() === UNIQUE_COMMAND) {
                setErrorMsg("This command is duplicate.");
                setOpenedModal(modalType.EDIT);
                setIsEditOpen(false);
                setTimeout(() => { setIsErrorOpen(true); }, 500);
            } else {
                const editItem = { cmd_name: currentCommand };
                try {
                    const res = await services.axiosAPI.requestPut(`/rss/api/vftp/command/${actionId}`, editItem);
                    await commandActions.commandLoadList(`/rss/api/vftp/command?type=${cmdType}`);
                    if (selectCommand !== -1) {
                        await commandActions.commandCheckOnlyOneList(selectCommand);
                    }
                    setIsEditOpen(false);
                    setActionId(-1);
                    setCurrentContext("");
                    setCurrentDataType("");
                    setErrorMsg("");
                    setOpenedModal("");
                } catch (e) {
                    // 에러 처리
                    console.error(e);
                }
            }
        }
    }, [commandList, actionId, selectCommand]);

    const deleteCommand = useCallback(async () => {
        try {
            const res = await services.axiosAPI.requestDelete(`/rss/api/vftp/command/${actionId}`);
            await commandActions.commandLoadList(`/rss/api/vftp/command?type=${cmdType}`);
            if(commandList.length === 0 || actionId === selectCommand) {
                setSelectCommand(-1);
            } else {
                if(selectCommand !== -1) await commandActions.commandCheckOnlyOneList(selectCommand);
            }
            setIsDeleteOpen(false);
            setActionId(-1);
        } catch (e) {
            // 에러 처리
            console.error(e);
        }
    }, [actionId, selectCommand]);

    return (
        <>
            <Card className="ribbon-wrapper catlist-card command-list manual">
                <CardBody className="custom-scrollbar manual-card-body">
                    <div className="ribbon ribbon-clip ribbon-secondary">Command</div>
                    <Col>
                        <FormGroup className="catlist-form-group">
                            <ul>
                                {cmdType === "vftp_compat" &&
                                    <li>
                                        <CustomInput
                                            type="radio"
                                            id={-1}
                                            name="notUse"
                                            label="not use."
                                            checked={selectCommand === -1}
                                            onChange={() => handleCommandChange(-1)}
                                        />
                                    </li>
                                }
                                <CreateCommandList
                                    commandList={commandList}
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
                listType={cmdType}
                dataType={currentDataType}
                context={currentContext}
                dataTypeChanger={onDataTypeChange}
                contextChanger={onContextChange}
                newOpen={isNewOpen}
                editOpen={isEditOpen}
                deleteOpen={isDeleteOpen}
                errorOpen={isErrorOpen}
                actionNew={addCommand}
                actionEdit={editCommand}
                actionDelete={deleteCommand}
                closeNew={closeAddModal}
                closeEdit={closeEditModal}
                closeDelete={closeDeleteModal}
                closeError={closeErrorModal}
                msg={errorMsg}
            />
        </>
    );
};

const CreateModal = React.memo(({ ...props }) => {
    const { listType, dataType, context, dataTypeChanger, contextChanger, newOpen, editOpen, deleteOpen, errorOpen,
            actionNew, actionEdit, actionDelete, closeNew, closeEdit, closeDelete, closeError, msg } = props;

    if (newOpen) {
        return (
            <ReactTransitionGroup
                transitionName={"Custom-modal-anim"}
                transitionEnterTimeout={200}
                transitionLeaveTimeout={200}
            >
                <div className="Custom-modal-overlay" onClick={closeNew} />
                <div className="Custom-modal command">
                    <p className="title">Add</p>
                    <div className="content-with-title">
                        <FormGroup className={"command-input-modal" + (listType === Define.PLAN_TYPE_VFTP_COMPAT ? " hidden" : "")}>
                            <label className="manual">Data type</label>
                            <Input
                                type="text"
                                placeholder="Enter data type"
                                className="manual"
                                value={dataType}
                                onChange={dataTypeChanger}
                            />
                        </FormGroup>
                        <FormGroup className="command-input-modal">
                            <label className="manual">Context</label>
                            <Input
                                type="text"
                                placeholder="Enter context"
                                className="manual"
                                value={context}
                                onChange={contextChanger}
                            />
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
                <div className="Custom-modal command">
                    <p className="title">Edit</p>
                    <div className="content-with-title">
                        <FormGroup className={"command-input-modal" + (listType === Define.PLAN_TYPE_VFTP_COMPAT ? " hidden" : "")}>
                            <label className="manual">Data type</label>
                            <Input
                                type="text"
                                placeholder="Enter data type"
                                className="manual"
                                value={dataType}
                                onChange={dataTypeChanger}
                            />
                        </FormGroup>
                        <FormGroup className="command-input-modal">
                            <label className="manual">Context</label>
                            <Input
                                type="text"
                                placeholder="Enter context"
                                className="manual"
                                value={context}
                                onChange={contextChanger}
                            />
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
                <div className="Custom-modal-overlay" onClick={closeDelete}/>
                <div className="Custom-modal">
                    <div className="content-without-title">
                        <p><FontAwesomeIcon icon={faTrashAlt} size="6x"/></p>
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
                        <button className="primary alert-type" onClick={closeError}>
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
}, propsCompare);

const CreateCommandList = React.memo(({ commandList, commandChanger, editModal, deleteModal }) => {
    return (
        <>
            {commandList.map((command, index) => {
                return (
                    <li key={index}>
                        <CustomInput
                            type="radio"
                            id={command.id}
                            name={command.cmd_name}
                            label={command.cmd_name}
                            checked={command.checked}
                            onChange={() => commandChanger(command.id)}
                        />
                        <span className="icon" onClick={() => deleteModal(command.id)}>
                            <FontAwesomeIcon icon={faTimes} />
                        </span>
                        <span className="icon" onClick={() => editModal(command.id, command.cmd_name)}>
                            <FontAwesomeIcon icon={faPencilAlt} />
                        </span>
                    </li>
                );
            })}
        </>
    );
}, propsCompare);

export default connect(
  (state) => ({
      dbCommand: state.command.get('command'),
  }),
  (dispatch) => ({
      commandActions: bindActionCreators(commandActions, dispatch),
  })
)(RSScommandlist);
