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
import * as API from "../../api";
import * as Define from "../../define";

class RSSNavbar extends Component{
  constructor() {
    super();
    this.state = {
      currentPage: "Manual"
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
              <UncontrolledDropdown nav inNavbar className={this.getClassName("Auto")}>
                <DropdownToggle nav>
                  Auto Download
                </DropdownToggle>
                <DropdownMenu>
                  <DropdownItem tag={RRNavLink} to={Define.PAGE_AUTO} onClick={() => this.handlePageChange("Auto")}>
                    Add New Plan
                  </DropdownItem>
                  <DropdownItem divider />
                  <DropdownItem tag={RRNavLink} to={Define.PAGE_AUTO} onClick={() => this.handlePageChange("Auto")}>
                    Plan Status
                  </DropdownItem>
                </DropdownMenu>
              </UncontrolledDropdown>
            </Nav>
            <Nav className="ml-auto" navbar>
              <UncontrolledDropdown nav inNavbar>
                <DropdownToggle nav>
                  <FontAwesomeIcon icon={faUserCircle} size="lg"/> ymkwon
                </DropdownToggle>
                <DropdownMenu right>
                  <DropdownItem>Change Password</DropdownItem>
                  <DropdownItem divider/>
                  <DropdownItem onClick={() => this.onLogout()}>Logout</DropdownItem>
                </DropdownMenu>
              </UncontrolledDropdown>
            </Nav>
          </Navbar>
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
    })
)(RSSNavbar);