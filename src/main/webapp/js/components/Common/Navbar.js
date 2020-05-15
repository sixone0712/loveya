import React, {Component} from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUserCircle, faCheckCircle } from "@fortawesome/free-solid-svg-icons";
import {
  Navbar,
  NavbarBrand,
  Nav,
  NavItem,
  NavLink,
  UncontrolledDropdown,
  DropdownToggle,
  DropdownMenu,
  DropdownItem
} from "reactstrap";
import {NavLink as RRNavLink } from "react-router-dom";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as loginActions from "../../modules/login";
import * as viewListActions from "../../modules/viewList"
import * as API from "../../api";
import * as Define from "../../define";
import LogOutModal from "../User/LogOut";
import ChangePwModal from "../User/ChangePw";
import ChangeAuthModal from "../User/ChangeAuth";
import AlertModal from "../Common/AlertModal";
import services from "../../services";

const PASSWORD_ALERT_MESSAGE = "Password change completed.";
const AUTH_ALERT_MESSAGE = "Permission change completed.";

class RSSNavbar extends Component{
  constructor(props) {
    super(props);
    this.state = {
      currentPage: "Manual",
      isPasswordOpen : false,
      isAuthOpen : false,
      isLogoutOpen : false,
      isAlertOpen: false,
      alertMessage: "",
      isMode:""
    };
  }

  getClassName = page => {
    const { currentPage } = this.state;

    return currentPage === page ? "nav-item-custom-select" : null;
  };

  handlePageChange = page => {
    this.setState({
      currentPage: page
    });
  };
  
  openModal = async (sMode) => {
    switch(sMode) {
      case "password":
        await this.setState({isPasswordOpen: true, isMode : sMode});
        break;

      case "permission":
        await this.setState(() => ({isAuthOpen: true, isMode:sMode}));
        break;

      case "logout":
        await this.setState(() => ({isLogoutOpen: true, isMode:sMode}));
        break;

      default:
        console.log("sMode error:" + sMode);
        break;
    }
  }
  
  closeModal = async () => {
  	await this.setState(() => ({
      isPasswordOpen: false,
      isAuthOpen: false,
      isLogoutOpen: false,
      isMode:''
  	}));
  }

  openAlert = (type) => {
    setTimeout(() => {
      switch(type) {
        case "password":
          this.setState({
            isAlertOpen: true,
            alertMessage: PASSWORD_ALERT_MESSAGE
          });
          break;

        case "permission":
          this.setState({
            isAlertOpen: true,
            alertMessage: AUTH_ALERT_MESSAGE
          });
          break;

        default:
          console.log("invalid type!!");
          break;
      }
    }, 800);
  }

  closeAlert = () => {
    this.setState({
      isAlertOpen: false,
      alertMessage: ""
    });
  }

  initViewListCheck = async () => {
    await API.checkAllToolInfoList(this.props, false);
    await API.checkAllLogInfoList(this.props, false);
    return true;
  }


  onLogout = async () => {
    window.sessionStorage.removeItem('isLoggedIn');
    window.sessionStorage.removeItem('username');
    window.sessionStorage.removeItem('password');
    window.sessionStorage.removeItem('auth');
    await API.setLoginInit(this.props);
    await services.axiosAPI.get("/user/logout");
    this.props.onMovePage(Define.PAGE_LOGIN)
  };

  render() {
    const { isPasswordOpen, isAuthOpen, isLogoutOpen, isAlertOpen, alertMessage } = this.state;
    const renderAlert = AlertModal(isAlertOpen, faCheckCircle, alertMessage, "gray", this.closeAlert);

    return (
        <>
          {renderAlert}
          <ChangePwModal isOpen={isPasswordOpen} right={this.closeModal} alertOpen={this.openAlert}/>
          <ChangeAuthModal isOpen={isAuthOpen} right={this.closeModal} alertOpen={this.openAlert}/>
          <LogOutModal isOpen={isLogoutOpen} left={this.onLogout} right={this.closeModal} />
          <div className="navbar-container">
            <Navbar color="dark" dark expand="md">
              <NavbarBrand className="custom-brand">
              RSS
            </NavbarBrand>
            <Nav className="mr-auto" navbar>

              <UncontrolledDropdown nav inNavbar className={this.getClassName("Manual")}>
                  <DropdownToggle nav>
                Manual Download
              </DropdownToggle>
                <DropdownMenu>
                    <DropdownItem tag={RRNavLink} to={Define.PAGE_MANUAL} onClick={() => this.handlePageChange("Manual")}>
                        FTP Download
                    </DropdownItem>
                    <DropdownItem divider />
                    <DropdownItem tag={RRNavLink} to={Define.PAGE_MANUAL2} onClick={() => this.handlePageChange("Manual2")}>
                        VFTP Download(COMPAT/Optional)
                    </DropdownItem>
                    <DropdownItem divider />
                    <DropdownItem tag={RRNavLink} to={Define.PAGE_MANUAL3} onClick={() => this.handlePageChange("Manual3")}>
                        VFTP Download(SSSS/Optional)
                    </DropdownItem>
                </DropdownMenu>
              </UncontrolledDropdown>
              <UncontrolledDropdown nav inNavbar>
                <DropdownToggle nav className={this.getClassName("Auto")}>
                  Auto Download
                </DropdownToggle>
                <DropdownMenu>
                  <DropdownItem
                      tag={RRNavLink}
                      to={Define.PAGE_REFRESH_AUTO_PLAN_ADD}
                      onClick={ () => this.handlePageChange("Auto") }>
                    Add New Plan
                  </DropdownItem>
                  <DropdownItem divider />
                  <DropdownItem
                      tag={RRNavLink}
                      to={Define.PAGE_REFRESH_AUTO_STATUS}
                      onClick={() => this.handlePageChange("Auto")}
                  >
                    Plan Status
                  </DropdownItem>
                </DropdownMenu>
              </UncontrolledDropdown>
              {
                (window.sessionStorage.getItem('auth') ==='100')
               ?  <NavLink tag={RRNavLink} to={Define.PAGE_ADMIN} className={this.getClassName("admin")}
                         onClick={() => this.handlePageChange("admin")}>  Administrator  </NavLink>
               : null
              }
            </Nav>
            <Nav className="ml-auto" navbar>
              <UncontrolledDropdown nav inNavbar>
                <DropdownToggle nav>
                  <FontAwesomeIcon icon={faUserCircle} size="lg"/> {window.sessionStorage.getItem('username')}
                </DropdownToggle>
                <DropdownMenu right>
                  <DropdownItem onClick={() => this.openModal("password")}>Change Password</DropdownItem>
                  <DropdownItem divider/>
                  <DropdownItem onClick={() => this.openModal("permission")}>Change Permission</DropdownItem>
                  <DropdownItem divider/>
                  <DropdownItem onClick={() => this.openModal("logout")}>Logout</DropdownItem>
                </DropdownMenu>
              </UncontrolledDropdown>
            </Nav>
          </Navbar>
        </div>
        </>
    );
  }
}

export default connect(
    (state) => ({
      loginInfo : state.login.get('loginInfo'),
    }),
    (dispatch) => ({
      loginActions: bindActionCreators(loginActions, dispatch),
      viewListActions: bindActionCreators(viewListActions, dispatch),
    })
)(RSSNavbar);