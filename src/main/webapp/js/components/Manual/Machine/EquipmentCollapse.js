import React, { Component }from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBars} from "@fortawesome/free-solid-svg-icons";
import {Collapse} from "react-collapse";
import CheckBox from "../../Common/CheckBox";
import * as API from "../../../api";

class EquipmentCollapse extends Component {
    constructor(props) {
        super(props);
        this.toggle = this.toggle.bind(this);
        this.state = {
            isOpened: true
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

        return (
            <>
                <div className="collapse-title" onClick={this.toggle}>
                    <FontAwesomeIcon icon={faBars} /> {this.props.structId}
                </div>
                <Collapse isOpened={isOpened}>
                    {machineList.map((machine, key) => {
                        if (machine.structId === this.props.structId) {
                            return (
                                <div className="custom-control custom-checkbox" key={key}>
                                    <CheckBox
                                        index={machine.keyIndex}
                                        name={machine.targetname}
                                        isChecked={machine.checked}
                                        labelClass={"machinelist-label"}
                                        handleCheckboxClick={checkMachineItem}
                                    />
                                </div>
                            );
                        } else {
                            return "";
                        }
                    })}
                </Collapse>
            </>
        );
    }
}

export default EquipmentCollapse;