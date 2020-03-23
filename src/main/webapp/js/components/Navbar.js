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

const colorWhite = {
  color: "white"
};

const selectedMenu = {
  backgroundColor: "#339af0",
  borderRadius: "0.25rem",
  color: "white"
};

const brandStyle = {
  fontWeight: "600"
};

const leftMargin = {
  marginLeft: "10px"
};

const divStyle = {
  width: "100%",
  position: "fixed",
  zIndex: "200",
  top: "0",
  boxShadow:
    "0px 2px 4px -1px rgba(0,0,0,0.2), 0px 4px 5px 0px rgba(0,0,0,0.14), 0px 1px 10px 0px rgba(0,0,0,0.12)"
};

export default function RSSnav() {
  return (
    <div style={divStyle}>
      <Navbar color="dark" dark expand="md">
        <NavbarBrand style={brandStyle} href="/">
          RSS
        </NavbarBrand>
        <Nav className="mr-auto" style={leftMargin} navbar>
          <NavItem>
            <NavLink href="#" style={selectedMenu}>
              Manual Download
            </NavLink>
          </NavItem>
          <NavItem style={leftMargin}>
            <NavLink href="#" style={colorWhite}>
              Auto Download
            </NavLink>
          </NavItem>
        </Nav>
        <Nav className="ml-auto" navbar>
          <UncontrolledDropdown nav inNavbar>
            <DropdownToggle nav style={colorWhite}>
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
