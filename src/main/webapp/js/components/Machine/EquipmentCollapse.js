import React, { Component }from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBars} from "@fortawesome/free-solid-svg-icons";
import {Collapse} from "react-collapse";
import CheckBox from "../Common/CheckBox";
import * as API from "../../api";

class EquipmentCollapse extends Component {
    constructor(props) {
        super(props);
        this.toggle = this.toggle.bind(this);
        this.state = {
            isOpened: false
        };
    }

    toggle = () => {
        this.setState({
            ...this.state,
            isOpened: !this.state.isOpened
        });
    };

    render() {
        const { isOpened } = this.state;
        const { machineList, checkMachineItem } = this.props;

        const titleStyle = {
            border: "1px solid rgba(171, 140, 228, 0.3)",
            borderRadius: "3px 3px 0 0",
            padding: "0.5rem",
            backgroundColor: "rgba(171, 140, 228, 0.1)",
            cursor: "pointer"
        };
        return (
            <>
                <div style={titleStyle} onClick={this.toggle}>
                    <FontAwesomeIcon icon={faBars} /> {this.props.structId}
                </div>
                <Collapse isOpened={isOpened}>
                    {machineList.map((machine, key) => {
                        if (machine.structId === this.props.structId) {
                            return (
                                <CheckBox
                                    key={key}
                                    index={machine.keyIndex}
                                    name={machine.targetname}
                                    isChecked={machine.checked}
                                    handleCheckboxClick={checkMachineItem}
                                    labelClass="machinelist-label"
                                />
                            );
                        }
                        return "";
                    })}
                </Collapse>
            </>
        );
    }
}

export default EquipmentCollapse;