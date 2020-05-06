import React, { Component } from "react";
import { Col, Card, CardHeader, CardBody, Button, Table } from "reactstrap";
import Select from "react-select";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
    faTrashAlt,
    faCheck,
    faExclamation
} from "@fortawesome/free-solid-svg-icons";

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

class RSSAutoDownloadList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isOpen: false
        };
    }

    openModal = () => {
        this.setState({
            isOpen: true
        });
    };

    closeModal = () => {
        this.setState({
            isOpen: false
        });
    };

    render() {
        return (
            <Card className="auto-plan-box">
                <CardHeader className="auto-plan-card-header">
                    Download List
                    <p>
                        Check the download list of <span>collection plan.</span>
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
                    <Col className="auto-plan-collection-list download-list">
                        <div className="content-section header">
                            <div className="plan-id">ID: CollectionPlan-501201-01</div>
                            <div>
                                <Button size="sm" className="download-btn">
                                    New File Download
                                </Button>
                            </div>
                        </div>
                        <Table className="content-section">
                            <thead>
                            <tr>
                                <th>No.</th>
                                <th>Request ID</th>
                                <th>Status</th>
                                <th>Delete</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>1</td>
                                <td>
                                    <div className="request-id-area">0000000123456781</div>
                                </td>
                                <td>
                                    <FontAwesomeIcon className="twinkle" icon={faExclamation} />{" "}
                                    New
                                </td>
                                <td>
                                    <div className="icon-area">
                                        <FontAwesomeIcon icon={faTrashAlt} />
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>2</td>
                                <td>
                                    <div className="request-id-area">0000000123456782</div>
                                </td>
                                <td>
                                    <FontAwesomeIcon icon={faCheck} /> Finished
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

export default RSSAutoDownloadList;
