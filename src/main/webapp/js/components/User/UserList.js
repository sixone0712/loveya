import React, { Component } from "react";
import {Col, CardHeader, CardBody, Table, Card, Container, Breadcrumb, BreadcrumbItem, Button} from "reactstrap";
import * as API from "../../api";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as userActions from "../../modules/User";
import { Select } from "antd";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheckCircle, faUser, faExclamationCircle, faTrashAlt, faAngleDoubleUp } from "@fortawesome/free-solid-svg-icons";
import {filePaginate, renderPagination} from "../Common/Pagination";
import ConfirmModal from "../Common/ConfirmModal";
import moment from "moment";
import ChangeAuthModal from "./ChangeAuth";
import SignOut from "./SignOut";
import ScrollToTop from "react-scroll-up";
import AlertModal from "../Common/AlertModal";
import Footer from "../Common/Footer";

const { Option } = Select;

const AUTH_ALERT_MESSAGE = "Permission change completed.";
const CREATE_ALERT_MESSAGE = "New Account create completed.";
const DELETE_ALERT_MESSAGE = "Account delete completed.";
const DELETE_CONFIRM_MESSAGE ="Do you want to delete account?";

const scrollStyle = {
    backgroundColor: "#343a40",
    width: "40px",
    height: "40px",
    textAlign: "center",
    borderRadius: "3px",
    zIndex: "101"
};


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
        }, 200);
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

    handleSelectBoxChange = value => {
        const { pageSize, currentPage } = this.state;
        const startIndex = (currentPage - 1) * pageSize === 0 ? 1 : (currentPage - 1) * pageSize + 1;

        this.setState({
            pageSize: value,
            currentPage: Math.ceil(startIndex / value)
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

    render() {
        const formatDate = 'YYYY/MM/DD HH:mm:ss';
        const UserList  = API.getUserList(this.props);
        const { length: count } = UserList;
        console.log(UserList);
        if (count === 0) {
            return (
                <>
                    <Container className="rss-container" fluid={true}>
                        <Breadcrumb className="topic-path">
                            <BreadcrumbItem>Administrator</BreadcrumbItem>
                            <BreadcrumbItem active>User Account</BreadcrumbItem>
                        </Breadcrumb>
                        <Card className="auto-plan-box">
                            <CardHeader className="auto-plan-card-header">
                                User Account
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
                    </Container>
                    <Footer/>
                    <ScrollToTop showUnder={160} style={scrollStyle}>
                        <span className="scroll-up-icon"><FontAwesomeIcon icon={faAngleDoubleUp} size="lg"/></span>
                    </ScrollToTop>
                </>
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

            const { isModalOpen, isAlertOpen, alertMessage} = this.state;
            const renderAlert = AlertModal(isAlertOpen, faCheckCircle, alertMessage, "administrator", this.closeAlert);
            const deleteModal = ConfirmModal((isModalOpen && this.state.isMode==='deleteUser'), faTrashAlt, DELETE_CONFIRM_MESSAGE, "administrator", this.closeModal,this.DeleteAccount,this.closeModal);

            return (
                <>
                    {renderAlert}
                    {deleteModal}
                    <ChangeAuthModal isOpen={isModalOpen && this.state.isMode==='ChangAuth'} right={this.closeModal} alertOpen={this.openAlert} userID={selected} />
                    <SignOut isOpen={isModalOpen && this.state.isMode==='SignOut'} right={this.closeModal} alertOpen={this.openAlert}/>
                    <Container className="rss-container" fluid={true}>
                        <Breadcrumb className="topic-path">
                            <BreadcrumbItem>Administrator</BreadcrumbItem>
                            <BreadcrumbItem active>User Account</BreadcrumbItem>
                        </Breadcrumb>
                        <Card className="auto-plan-box administrator">
                            <CardHeader className="auto-plan-card-header administrator">
                                User Account
                                <p>Manage <span>user accounts.</span></p>
                                <div className="select-area">
                                    <label>Rows per page : </label>
                                    <Select defaultValue={10} onChange={this.handleSelectBoxChange} className="administrator">
                                        <Option value={10}>10</Option>
                                        <Option value={30}>30</Option>
                                        <Option value={50}>50</Option>
                                        <Option value={100}>100</Option>
                                    </Select>
                                </div>
                            </CardHeader>
                            <CardBody className="auto-plan-card-body not-flex">
                                <div className="user-create-btn">
                                    <Button outline size="sm" color="info"
                                            onClick={() => this.setState({...this.state,isModalOpen: true, isMode : "SignOut"})}>
                                        <FontAwesomeIcon icon={faUser} /> New Account
                                    </Button>
                                </div>
                                <div className="auto-plan-collection-list">
                                    <Table>
                                        <thead>
                                        <tr>
                                            <th>No.</th>
                                            <th>User name</th>
                                            <th>Permission</th>
                                            <th>Account created date</th>
                                            <th>Last access date</th>
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
                                                        <span onClick={()=> this.uChangeAuth(user.id)} className="permission">
                                                          {user.auth}
                                                        </span>
                                                    </td>
                                                    <td>{(user.created!=null) ? moment(user.created).format(formatDate): ""}</td>
                                                    <td>{(user.last_access!=null) ? moment(user.last_access).format(formatDate): ""}</td>
                                                    <td>
                                                        <div className="icon-area-administrator" onClick={ () => this.uDelete(user.id) }>
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
                    <Footer/>
                    <ScrollToTop showUnder={160} style={scrollStyle}>
                        <span className="scroll-up-icon"><FontAwesomeIcon icon={faAngleDoubleUp} size="lg"/></span>
                    </ScrollToTop>
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