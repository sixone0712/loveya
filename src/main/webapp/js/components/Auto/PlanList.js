import React, { Component } from "react";
import { Col, Card, CardHeader, CardBody, Spinner, Table } from "reactstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
    faExclamationCircle,
    faEdit,
    faStop,
    faTrashAlt
} from "@fortawesome/free-solid-svg-icons";
import Select from "react-select";

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
        transition:
            "color .15s ease-in-out, background-color .15s ease-in-out, border-color .15s ease-in-out, box-shadow .15s ease-in-out",
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
            color: "rgba(92, 124, 250, 1)"
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

const optionList = [
    { value: 10, label: "10" },
    { value: 30, label: "30" },
    { value: 50, label: "50" },
    { value: 100, label: "100" }
];

class RSSautoplanlist extends Component {
    render() {
        return (
            <Card className="auto-plan-box-shadow">
                <CardHeader className="auto-plan-card-header">
                    Plan Status
                    <p>
                        Check the status of the <span>registered collection plan.</span>
                    </p>
                    <div className="select-area">
                        <label>Rows per page : </label>
                        <Select
                            options={optionList}
                            styles={customSelectStyles}
                            defaultValue={optionList[0]}
                        />
                    </div>
                </CardHeader>
                <CardBody className="auto-plan-card-body">
                    <Col className="auto-plan-collection-list">
                        <Table>
                            <thead>
                            <tr>
                                <th>Plan ID</th>
                                <th>Description</th>
                                <th>Target</th>
                                <th>Collection Period</th>
                                <th>Status</th>
                                <th>Last Run Time</th>
                                <th>Detail</th>
                                <th>Edit</th>
                                <th>Delete</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>Plan 1</td>
                                <td>default collection plan</td>
                                <td>3</td>
                                <td>2020-04-20 00:00 ~ 2020-04-27 23:59</td>
                                <td>
                                    <Spinner size="sm" className="spinner-color" /> Running
                                </td>
                                <td>2020-04-27 15:37</td>
                                <td>Completed</td>
                                <td>
                                    <div className="icon-area move-left">
                                        <FontAwesomeIcon icon={faEdit} />
                                    </div>
                                </td>
                                <td>
                                    <div className="icon-area">
                                        <FontAwesomeIcon icon={faTrashAlt} />
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Plan 2</td>
                                <td>gtpark's plan</td>
                                <td>14</td>
                                <td>2019-01-24 00:22 ~ 2019-05-31 23:59</td>
                                <td>
                                    <FontAwesomeIcon icon={faStop} /> Stopped
                                </td>
                                <td>2019-06-04 00:00</td>
                                <td>Failed</td>
                                <td>
                                    <div className="icon-area move-left">
                                        <FontAwesomeIcon icon={faEdit} />
                                    </div>
                                </td>
                                <td>
                                    <div className="icon-area">
                                        <FontAwesomeIcon icon={faTrashAlt} />
                                    </div>
                                </td>
                            </tr>
                            </tbody>
                        </Table>
                    </Col>
                </CardBody>
            </Card>
        );
    }
}

export default RSSautoplanlist;
