import React, { Component } from "react";
import {Col, CardHeader, CardBody, Table, Card, Container, Breadcrumb, BreadcrumbItem, Button} from "reactstrap";
import * as API from "../../api";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as dwHistoryAction from "../../modules/dwHistory";
import Select from "react-select";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheckCircle, faUser, faExclamationCircle, faPlus, faTrashAlt} from "@fortawesome/free-solid-svg-icons";
import {filePaginate, renderPagination} from "../Common/Pagination";
import moment from "moment";

function HistoryListEmpty(props) {
    return (
        <Card className="auto-plan-box">
            <CardBody className="auto-plan-card-body">
                <Col className="auto-plan-collection-list">
                    <p className="no-registered-plan">
                        <FontAwesomeIcon icon={faExclamationCircle} size="7x"/>
                    </p>
                    <p className="no-registered-plan">
                        No registered Download History
                    </p>
                </Col>
            </CardBody>
        </Card>
    );
}

class DownloadHistory extends Component {
    constructor(props) {
        super(props);
        this.state = {
            pageSize: 10,
            currentPage: 1,
        };

    }

    async componentDidMount()
    {
        console.log("componentDidMount");
        await API.getDBDwHistoryList(this.props);
    };

    handlePaginationChange = page => {
        this.setState({
            ...this.state,
            currentPage: page
        });
    };
    handleSelectBoxChange = newValue => {
        this.setState({
            ...this.state,
            pageSize: newValue.value
        });
    };
     render() {
        const formatDate = 'YYYY/MM/DD HH:mm:ss';
        const historyList = API.getDwHistoryList(this.props);
        const {length:count} = historyList;
        console.log("historyList: ",historyList);
         console.log("count: ",count);
         {
            const {currentPage, pageSize} = this.state;
            const lists = filePaginate(historyList, currentPage, pageSize);
            const pagination = renderPagination(
                pageSize,
                count,
                this.handlePaginationChange,
                "custom-pagination"
            );
            const optionList = [
                {value: 10, label: "10"},
                {value: 30, label: "30"},
                {value: 50, label: "50"},
                {value: 100, label: "100"}
            ];

            const customSelectStyles = {
                container: styles => ({
                    ...styles,
                    display: "inline-block",
                    width: "85px",
                    fontSize: "14px",
                    marginLeft: "10px"
                }),
                option: (styles, {isFocused, isSelected}) => {
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
                    transition: "all .15s ease-in-out",
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
                        color: "rgb(92, 124, 250)"
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
            return (
                <>
                    <Container className="rss-container" fluid={true}>

                        <Card className="auto-plan-box">
                            <CardHeader className="auto-plan-card-header">
                                Download History
                                <p>Administrator / Download History</p>
                                {(count > 0) &&
                                <div className="select-area">
                                    <label>Rows per page : </label>
                                    <Select
                                        options={optionList}
                                        styles={customSelectStyles}
                                        defaultValue={optionList[0]}
                                        onChange={this.handleSelectBoxChange}
                                    />
                                </div>}
                            </CardHeader>
                            <CardBody className="auto-plan-card-body not-flex">
                                {(count ===0)
                                    ? <HistoryListEmpty/>
                                    : <div className="auto-plan-collection-list">
                                    <Table>
                                        <thead>
                                        <tr>
                                            <th>No.</th>
                                            <th>User Name</th>
                                            <th>Downloaded Date</th>
                                            <th>Download Type</th>
                                            <th>Detail</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {lists.map((history, idx) => {
                                        return (
                                            <tr key={idx}>
                                                <td>
                                                    <div className="plan-id-area">
                                                        {idx + 1}
                                                    </div>
                                                </td>
                                                <td>{history.dw_user}</td>
                                                <td>{(history.dw_date != null) ? moment(history.dw_date).format(formatDate) : ""}</td>
                                                <td>{(history.dw_type != null) ? "Auto(Ftp)" : "Manual(Ftp)"}</td>
                                                <td><u>Detail</u></td>
                                            </tr>
                                        );
                                        })}
                                        </tbody>
                                    </Table>
                                </div>
                                }
                            </CardBody>
                            {(count > 0) && pagination}
                        </Card>
                    </Container>
                </>
            );
        }
    }
}

export default connect(
    (state) => ({
        dwHistoryInfo: state.dwHis.get('dwHistoryInfo'),
    }),
    (dispatch) => ({
        dwHistoryAction: bindActionCreators(dwHistoryAction, dispatch),
    })
)(DownloadHistory);