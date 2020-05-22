import React, { Component } from "react";
import {Col, CardHeader, CardBody, Table, Card, Container, Breadcrumb, BreadcrumbItem, Button} from "reactstrap";
import * as API from "../../api";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as userActions from "../../modules/User";
import Select from "react-select";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheckCircle, faUser, faExclamationCircle, faPlus, faTrashAlt} from "@fortawesome/free-solid-svg-icons";
import {filePaginate, renderPagination} from "../Common/Pagination";
import ConfirmModal from "../Common/ConfirmModal";
import moment from "moment";
import ChangeAuthModal from "./ChangeAuth";
import SignOut from "./SignOut";
import AlertModal from "../Common/AlertModal";

const AUTH_ALERT_MESSAGE = "Permission change completed.";
const CREATE_ALERT_MESSAGE = "New Account create completed.";
const DELETE_ALERT_MESSAGE = "Account delete completed.";
const DELETE_CONFIRM_MESSAGE ="Do you want to delete account?";

class UserList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            pageSize: 10,
            currentPage: 1,
            isConfirmOpen: false,
            selected: "",
            isModalOpen : false,
            isAlertOpen: false,
            alertMessage: "",
            isMode:"",
            Permission:""
        };

    }

    async componentDidMount()
    {
        console.log("componentDidMount");
        await API.getDBUserList(this.props);
    };

    openAlert = (type) => {
        setTimeout(() => {
            switch(type) {
                case "permission":
                    this.setState({
                        ...this.state,
                        isAlertOpen: true,
                        alertMessage: AUTH_ALERT_MESSAGE
                    });
                    break;
                case "create":
                    this.setState({
                        ...this.state,
                        isAlertOpen: true,
                        alertMessage: CREATE_ALERT_MESSAGE
                    });
                    break;

                case "delete":
                    this.setState({
                        ...this.state,
                        isAlertOpen: true,
                        alertMessage: DELETE_ALERT_MESSAGE
                    });
                    break;


                default:
                    console.log("invalid type!!");
                    break;
            }
        }, 800);
    };

    closeAlert = () => {
        this.setState({
            ...this.state,
            isAlertOpen: false,
            alertMessage: ""
        });
    };
    closeModal = async () => {
        await this.setState(() => ({
            ...this.state,
            isMode:'',
            isModalOpen:false,
            selected:'',
        }));
    };
    uDelete = (id) => {
        this.setState(() => ({
            ...this.state,
            isMode:'deleteUser',
            isModalOpen:true,
            selected:id,
        }));
    }
    uChangeAuth = (id) => {
        this.setState(() => ({
            ...this.state,
            isMode:'ChangAuth',
            isModalOpen:true,
            selected:id,
        }));
    };

    handlePaginationChange = page => {
        this.setState({
            ...this.state,
            currentPage: page
        });
    };

    handleSelectBoxChange = newValue => {
        const { pageSize, currentPage } = this.state;
        const startIndex = (currentPage - 1) * pageSize === 0 ? 1 : (currentPage - 1) * pageSize + 1;

        this.setState({
            ...this.state,
            pageSize: parseInt(newValue.value),
            currentPage: Math.ceil(startIndex / parseInt(newValue.value))
        });
    };

    DeleteAccount = async (e) => {
        console.log("[VFT .....] DeleteAccount");
        console.log(this.state.selected);
        await API.deleteUser(this.props, this.state.selected);
        let result = API.getUserInfoErrorCode(this.props);
        console.log("result:" + result);
        if(result !== 0 )
        {
            let msg = API.getErrorMsg(result) ;
            if (msg.length > 0) {
                this.props.alertOpen("error");
                this.setState({
                    ...this.state,
                    alertMessage: msg,
                    isAlertOpen: true,
                })
            }
        }
        else
        {
            await this.closeModal(); //delete modal Close
            this.openAlert("delete"); //delete complete
            await API.getDBUserList(this.props);//user list refresh
        }
    };
    render()
    {
        const formatDate = 'YYYY/MM/DD HH:mm:ss';
    const UserList  = API.getUserList(this.props);
   const { length: count } = UserList;
    console.log(UserList);
    if (count === 0) {
        return (
            <Card className="auto-plan-box">
                <CardHeader className="auto-plan-card-header">
                    User List
                </CardHeader>
                <CardBody className="auto-plan-card-body">
                    <Col className="auto-plan-collection-list">
                        <p className="no-registered-plan">
                            <FontAwesomeIcon icon={faExclamationCircle} size="7x" />
                        </p>
                        <p className="no-registered-plan">
                            No registered User List
                        </p>
                    </Col>
                </CardBody>
            </Card>
        );
    } else {
    const { currentPage, pageSize, isConfirmOpen, selected } = this.state;
    const users = filePaginate(UserList, currentPage, pageSize);
    const pagination = renderPagination(
        pageSize,
        count,
        this.handlePaginationChange,
        currentPage,
        "custom-pagination"
    );
    const optionList = [
        { value: 10, label: "10" },
        { value: 30, label: "30" },
        { value: 50, label: "50" },
        { value: 100, label: "100" }
    ];

    const customSelectStyles = {
        container: styles => ({
            ...styles,
            display: "inline-block",
            width: "85px",
            fontSize: "14px",
            marginLeft: "10px"
        }),
        option: (styles, { isFocused, isSelected }) => {
            return {
                ...styles,
                backgroundColor: isSelected
                    ? "rgba(92, 124, 250, 0.5)"
                    : isFocused
                        ? "rgba(92, 124, 250, 0.3)"
                        : null,
                color: "black",
                ":active": {
                    ...styles[":active"],
                    backgroundColor: isSelected
                        ? "rgba(92, 124, 250, 0.9)"
                        : isFocused
                            ? "rgba(92, 124, 250, 0.7)"
                            : null
                }
            };
        },
        control: () => ({
            display: "flex",
            border: "1px solid rgb(92, 124, 250)",
            borderRadius: "3px",
            caretColor: "transparent",
            transition: "all .15s ease-in-out",
            ":hover": {
                outline: "0",
                boxShadow: "0 0 0 0.2em rgba(92, 124, 250, 0.5)"
            }
        }),
        dropdownIndicator: styles => ({
            ...styles,
            color: "rgba(92, 124, 250, 0.6)",
            ":hover": {
                ...styles[":hover"],
                color: "rgb(92, 124, 250)"
            }
        }),
        indicatorSeparator: styles => ({
            ...styles,
            backgroundColor: "rgba(92, 124, 250, 0.6)"
        }),
        menu: styles => ({
            ...styles,
            borderRadius: "3px",
            boxShadow:
                "0 0 0 1px rgba(92, 124, 250, 0.6), 0 4px 11px rgba(92, 124, 250, 0.6)"
        })
    };
        const { isModalOpen, isAlertOpen, alertMessage} = this.state;
        const renderAlert = AlertModal(isAlertOpen, faCheckCircle, alertMessage, "gray", this.closeAlert);
        const deleteModal = ConfirmModal((isModalOpen && this.state.isMode==='deleteUser'), faTrashAlt, DELETE_CONFIRM_MESSAGE, "gray", this.closeModal,this.DeleteAccount,this.closeModal);

        return (
        <>
            {renderAlert}
            {deleteModal}
            <ChangeAuthModal isOpen={isModalOpen && this.state.isMode==='ChangAuth'} right={this.closeModal} alertOpen={this.openAlert} userID = {selected} />
            <SignOut isOpen={isModalOpen && this.state.isMode==='SignOut'} right={this.closeModal} alertOpen={this.openAlert}/>

            <Container className="rss-container" fluid={true}>

            <Card className="auto-plan-box">
                <CardHeader className="auto-plan-card-header">
                    User Account
                    <p>Manage user accounts.</p>
                    <div className="select-area">
                        <label>Rows per page : </label>
                        <Select
                            options={optionList}
                            styles={customSelectStyles}
                            defaultValue={optionList[0]}
                            onChange={this.handleSelectBoxChange}
                        />
                    </div>
                </CardHeader>
                <CardBody className="auto-plan-card-body not-flex">
                    <div>
                        <Button outline size="sm" className="new-account-btn"
                                onClick={() => this.setState({...this.state,isModalOpen: true, isMode : "SignOut"})}>
                            <FontAwesomeIcon icon={faUser} /> New Account
                        </Button>
                    </div>
                    <div className="auto-plan-collection-list">
                        <Table>
                            <thead>
                            <tr>
                                <th>No.</th>
                                <th>User Name</th>
                                <th>Permission</th>
                                <th>Account Created Date</th>
                                <th>last access Date</th>
                                <th>Delete</th>
                            </tr>
                            </thead>
                            <tbody>
                            {users.map((user, idx) => {
                                return (
                                    <tr key={idx}>
                                        <td>{idx+1}</td>
                                        <td>{user.name}</td>
                                        <td>
                                            <div className="plan-id-area"
                                                onClick={()=> this.uChangeAuth(user.id)}>
                                              {user.auth}
                                            </div>
                                        </td>
                                        <td>{(user.created!=null) ? moment(user.created).format(formatDate): ""}</td>
                                        <td>{(user.last_access!=null) ? moment(user.last_access).format(formatDate): ""}</td>
                                        <td>
                                            <div className="icon-area" onClick={ () => this.uDelete(user.id) }>
                                                <FontAwesomeIcon icon={faTrashAlt} />
                                            </div>
                                        </td>
                                    </tr>
                                );
                            })}
                            </tbody>
                        </Table>
                    </div>
                </CardBody>
                {pagination}
            </Card>
            </Container>
        </>
    );
}
}
}

export default connect(
    (state) => ({
        UserList : state.user.get('UserList'),
        userInfo: state.user.get('UserInfo'),
    }),
    (dispatch) => ({
        userActions: bindActionCreators(userActions, dispatch),
    })
)(UserList);