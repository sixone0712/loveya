import React, {Component} from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUserCircle } from "@fortawesome/free-solid-svg-icons";
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

class RSSNavbar extends Component{
  constructor() {
    super();
    this.state = {
      currentPage: "Manual",
            isModalOpen : false,
            isMode:"",
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
  
  openModal =async (sMode) => {
  	await this.setState(() => ({isModalOpen: true, isMode:sMode}));
  }
  
  closeModal = async () => {
  	await this.setState(() => ({isModalOpen: false,isMode:''}));
  }

  initViewListCheck = async () => {
    await API.checkAllToolInfoList(this.props, false);
    await API.checkAllLogInfoList(this.props, false);
    return true;
  }


  onLogout = () => {
    window.sessionStorage.removeItem('isLoggedIn');
    window.sessionStorage.removeItem('username');
    window.sessionStorage.removeItem('password');
    window.sessionStorage.removeItem('auth');
    API.setLoginInit(this.props);
    this.props.onMovePage(Define.PAGE_LOGIN)
  };

  render() {
    return (
        <div className="navbar-container">
          <Navbar color="dark" dark expand="md">
            <NavbarBrand className="custom-brand" style={{pointerEvents: "none"}}>
              RSS
            </NavbarBrand>
            <Nav className="mr-auto" navbar>
              <NavLink tag={RRNavLink} to={Define.PAGE_MANUAL}
                       className={this.getClassName("Manual")}
                       onClick={() => this.handlePageChange("Manual")}>
                Manual Download
              </NavLink>
              <UncontrolledDropdown nav inNavbar>
                <DropdownToggle nav className={this.getClassName("Auto")}>
                  Auto Download
                </DropdownToggle>
                <DropdownMenu>
                  <DropdownItem
                      tag={RRNavLink}
                      to={Define.PAGE_AUTO_PLAN_ADD}
                      onClick={async () => {
                        await this.initViewListCheck();
                        this.handlePageChange("Auto");
                      }}>
                    Add New Plan
                  </DropdownItem>
                  <DropdownItem divider />
                  <DropdownItem
                      tag={RRNavLink}
                      to={Define.PAGE_AUTO_STATUS}
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
        {
            this.state.isMode ==='logout'
            ?<LogOutModal isOpen={this.state.isModalOpen } left={this.onLogout} right={this.closeModal} />
            : this.state.isMode ==='password'
                ? <ChangePwModal isOpen={this.state.isModalOpen } right={this.closeModal} />
                : this.state.isMode ==='permission'
                    ? <ChangeAuthModal isOpen={this.state.isModalOpen } right={this.closeModal} />
                :null
        }
        </div>
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