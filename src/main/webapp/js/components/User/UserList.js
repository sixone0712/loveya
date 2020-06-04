import React, { Component } from "react";
import {Col, CardHeader, CardBody, Table, Card, Container, Breadcrumb, BreadcrumbItem, Button} from "reactstrap";
import * as API from "../../api";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as userActions from "../../modules/User";
import { Select } from "antd";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheckCircle, faUser, faExclamationCircle, faTrashAlt, faAngleDoubleUp } from "@fortawesome/free-solid-svg-icons";
import {filePaginate, RenderPagination} from "../Common/Pagination";
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
            Permission:"",
            registeredList: [],
            deleteIndex: ""
        };

    }

    async componentDidMount()
    {
        console.log("componentDidMount");
        await this.loadUserList();
    };

    loadUserList = async () => {
        const res = await API.getDBUserList(this.props);
        const { data } = res;
        const newData = data.data.map((item, idx) => {
            return (
                {
                    keyIndex: idx + 1,
                    userAuth: item.permissions,
                    userId: item.id,
                    userName: item.username,
                    userCreated: item.created,
                    userLastAccess: item.lastAccess
                }
            );
        });

        await this.setState({
            ...this.state,
            registeredList: newData
        })

        return true;
    }

    openAlert = async (type) => {
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

        await this.loadUserList();
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
    uDelete = (id, index) => {
        this.setState(() => ({
            ...this.state,
            isMode:'deleteUser',
            isModalOpen:true,
            selected:id,
            deleteIndex: index
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

    DeleteAccount = async () => {
        console.log("[VFT .....] DeleteAccount");
        console.log(this.state.selected);
        await API.deleteUser(this.props, this.state.selected);
        let result = API.getUserInfoErrorCode(this.props);
        console.log("result:" + result);
        if(result !== 0)
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
            const { pageSize, deleteIndex } = this.state;
            const numerator = deleteIndex - 1 === 0 ? 1 : deleteIndex - 1;
            await this.closeModal(); //delete modal Close

            this.setState({
                currentPage: Math.ceil(numerator / pageSize),
                deleteIndex: ""
            });

            this.openAlert("delete"); //delete complete
        }
    };

    render() {
        const formatDate = 'YYYY/MM/DD HH:mm:ss';
        const { registeredList } = this.state;
        const { length: count } = registeredList;
        console.log(registeredList);
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
            const { currentPage, pageSize, isConfirmOpen, selected, isModalOpen, isAlertOpen, alertMessage } = this.state;
            const users = filePaginate(registeredList, currentPage, pageSize);

            return (
                <>
                    <AlertModal isOpen={isAlertOpen} icon={faCheckCircle} message={alertMessage} style={"administrator"} closer={this.closeAlert} />
                    <ConfirmModal isOpen={(isModalOpen && this.state.isMode==='deleteUser')}
                                  icon={faTrashAlt}
                                  message={DELETE_CONFIRM_MESSAGE}
                                  leftBtn={"OK"}
                                  rightBtn={"Cancel"}
                                  style={"administrator"}
                                  actionBg={this.closeModal}
                                  actionLeft={this.DeleteAccount}
                                  actionRight={this.closeModal}
                    />
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
                                        {users.map((user) => {
                                            return (
                                                <tr key={user.keyIndex}>
                                                    <td>{user.keyIndex}</td>
                                                    <td>{user.userName}</td>
                                                    <td>
                                                        <span onClick={()=> this.uChangeAuth(user.userId)} className="permission">
                                                          {user.userAuth}
                                                        </span>
                                                    </td>
                                                    <td>{(user.userCreated!=null) ? moment(user.userCreated).format(formatDate): ""}</td>
                                                    <td>{(user.userLastAccess!=null) ? moment(user.userLastAccess).format(formatDate): ""}</td>
                                                    <td>
                                                        <div className="icon-area-administrator" onClick={ () => this.uDelete(user.userId, user.keyIndex) }>
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
                            <RenderPagination
                                pageSize={pageSize}
                                itemsCount={count}
                                onPageChange={this.handlePaginationChange}
                                currentPage={currentPage}
                                className={"custom-pagination"}
                            />
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