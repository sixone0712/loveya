import React, {useState, useCallback, useEffect} from "react";
import { Card, CardBody, Col, FormGroup, Button, Input, CustomInput } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTrashAlt, faPencilAlt, faTimes } from "@fortawesome/free-solid-svg-icons";
import ReactTransitionGroup from "react-addons-css-transition-group";
import { connect } from "react-redux";
import {bindActionCreators} from "redux";
import * as commandActions from "../../../modules/command";
import services from "../../../services";

const UNIQUE_COMMAND = "not use.";

const RSScommandlist = ({ cmdType, dbCommand, commandActions }) => {
    const commandList = dbCommand.get("lists").toJS();
    const [selectCommand, setSelectCommand] = useState(-1);
    const [actionId, setActionId] = useState(-1);
    const [currentCommand, setCurrentCommand] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [isNewOpen, setIsNewOpen] = useState(false);
    const [isEditOpen, setIsEditOpen] = useState(false);
    const [isDeleteOpen, setIsDeleteOpen] = useState(false);
    /* const [isAlertOpen, setIsAlertOpen] = useState(false); */

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
        setActionId(-1);
        setCurrentCommand("");
        setErrorMsg("");
    }, []);

    const closeDeleteModal = useCallback(() => {
        setIsDeleteOpen(false);
        setActionId(-1);
    }, []);

    const onTextChange = useCallback(e => {
        setCurrentCommand(e.target.value);
        setErrorMsg("");
    }, []);

    const addCommand = useCallback(async () => {
        const lowerCommand = currentCommand.toLowerCase();

        const duplicateArray = commandList.filter(command => command.cmd_name.toLowerCase() === lowerCommand);

        if (duplicateArray.length !== 0 || lowerCommand === UNIQUE_COMMAND) {
            setErrorMsg("This command is duplicate.");
        } else {
            const addData = {
                cmd_name: currentCommand,
                cmd_type: cmdType
            }
            try {
                const res = await services.axiosAPI.requestPost("/rss/api/vftp/command", addData);
                const { data: { id } } = res;
                await commandActions.commandLoadList(`/rss/api/vftp/command?type=${cmdType}`);
                await commandActions.commandCheckOnlyOneList(id);
                setSelectCommand(id)

                setIsNewOpen(false);
                setCurrentCommand("");
                setErrorMsg("");
            } catch (e) {
                // 에러 처리
                console.log(e.message())
            }
        }
    }, [commandList, currentCommand]);

    const editCommand = useCallback(async () => {
        const lowerCommand = currentCommand.toLowerCase();
        const duplicateArray = commandList.filter(command => command.cmd_name.toLowerCase() === lowerCommand);

        if ((duplicateArray.length !== 0 && duplicateArray[0].id !== actionId) || lowerCommand === UNIQUE_COMMAND) {
            setErrorMsg("This command is duplicate.");
        } else {
            const editItem = {
                cmd_name: currentCommand
            }
            try {
                const res = await services.axiosAPI.requestPut(`/rss/api/vftp/command/${actionId}`, editItem);
                await commandActions.commandLoadList(`/rss/api/vftp/command?type=${cmdType}`);
                if(selectCommand !== -1) {
                    await commandActions.commandCheckOnlyOneList(selectCommand);
                }
                setIsEditOpen(false);
                setActionId(-1);
                setCurrentCommand("");
                setErrorMsg("");
            } catch (e) {
                // 에러 처리
                console.log(e.message())
            }
        }
    }, [commandList, actionId, currentCommand, selectCommand]);

    const deleteCommand = useCallback(async () => {
        try {
            const res = await services.axiosAPI.requestDelete(`/rss/api/vftp/command/${actionId}`);
            await commandActions.commandLoadList(`/rss/api/vftp/command?type=${cmdType}`);
            if(commandList.length == 0 || actionId === selectCommand) {
                setSelectCommand(-1);
            } else {
                await commandActions.commandCheckOnlyOneList(selectCommand);
            }
            setIsDeleteOpen(false);
            setActionId(-1);
        } catch (e) {
            // 에러 처리
            console.log(e.message())
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
                actionEdit={editCommand}
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
});

export default connect(
  (state) => ({
      dbCommand: state.command.get('command'),
  }),
  (dispatch) => ({
      commandActions: bindActionCreators(commandActions, dispatch),
  })
)(RSScommandlist);
