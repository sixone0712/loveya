import React from "react";
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

export default function RSSnav() {
  return (
    <div className="navbar-container">
      <Navbar color="dark" dark expand="md">
        <NavbarBrand className="custom-brand" href="/">
          RSS
        </NavbarBrand>
        <Nav className="mr-auto" navbar>
          <NavItem>
            <NavLink href="#" className="nav-item-custom-select">
              Manual Download
            </NavLink>
          </NavItem>
          <NavItem>
            <NavLink href="#">Auto Download</NavLink>
          </NavItem>
        </Nav>
        <Nav className="ml-auto" navbar>
          <UncontrolledDropdown nav inNavbar>
            <DropdownToggle nav>
              <FontAwesomeIcon icon={faUserCircle} size="lg" /> ymkwon
            </DropdownToggle>
            <DropdownMenu right>
              <DropdownItem>Change Password</DropdownItem>
              <DropdownItem divider />
              <DropdownItem>Logout</DropdownItem>
            </DropdownMenu>
          </UncontrolledDropdown>
        </Nav>
      </Navbar>
    </div>
  );
}