import React, { Component } from "react";
import {Col, CardHeader, CardBody, Table, Card, Container, Breadcrumb, BreadcrumbItem, Button} from "reactstrap";
import * as API from "../../api";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as dwHistoryAction from "../../modules/dwHistory";
import { Select } from "antd";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheckCircle, faUser, faExclamationCircle, faPlus, faTrashAlt} from "@fortawesome/free-solid-svg-icons";
import {filePaginate, renderPagination} from "../Common/Pagination";
import moment from "moment";

const { Option } = Select;

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

    handleSelectBoxChange = value => {
        const { pageSize, currentPage } = this.state;
        const startIndex = (currentPage - 1) * pageSize === 0 ? 1 : (currentPage - 1) * pageSize + 1;

        this.setState({
            pageSize: value,
            currentPage: Math.ceil(startIndex / value)
        });
    };

     render() {
        const formatDate = 'YYYY/MM/DD HH:mm:ss';
        const historyList = API.getDwHistoryList(this.props);
        const {length:count} = historyList;
        console.log("historyList: ",historyList);
        console.log("count: ",count);

        const {currentPage, pageSize} = this.state;
        const lists = filePaginate(historyList, currentPage, pageSize);
        const pagination = renderPagination(
            pageSize,
            count,
            this.handlePaginationChange,
            "custom-pagination"
        );

        if (count === 0) {
         return (
             <Card className="auto-plan-box">
                 <Breadcrumb className="topic-path">
                     <BreadcrumbItem>Administrator</BreadcrumbItem>
                     <BreadcrumbItem active>Download History</BreadcrumbItem>
                 </Breadcrumb>
                 <CardHeader className="auto-plan-card-header">
                     Download History
                     <p>Check the <span>user's download history.</span></p>
                 </CardHeader>
                 <CardBody className="auto-plan-card-body">
                     <Col className="auto-plan-collection-list">
                         <p className="no-registered-plan">
                             <FontAwesomeIcon icon={faExclamationCircle} size="7x" />
                         </p>
                         <p className="no-registered-plan">
                             No registered user's download history.
                         </p>
                     </Col>
                 </CardBody>
             </Card>
         );
        } else {
            return (
                <>
                    <Container className="rss-container" fluid={true}>
                        <Breadcrumb className="topic-path">
                            <BreadcrumbItem>Administrator</BreadcrumbItem>
                            <BreadcrumbItem active>Download History</BreadcrumbItem>
                        </Breadcrumb>
                        <Card className="auto-plan-box administrator">
                            <CardHeader className="auto-plan-card-header administrator">
                                Download History
                                <p>Check the <span>user's download history.</span></p>
                                <div className="select-area">
                                    <label>Rows per page : </label>
                                    <Select defaultValue={10} onChange={this.handleSelectBoxChange} className="administrator">
                                        <Option value={10}>10</Option>
                                        <Option value={30}>30</Option>
                                        <Option value={50}>50</Option>
                                        <Option value={100}>100</Option>
                                    </Select>
                                </div>
                            </CardHeader>
                            <CardBody className="auto-plan-card-body not-flex">
                                <div className="auto-plan-collection-list">
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
                                                    <td>{idx + 1}</td>
                                                    <td>{history.dw_user}</td>
                                                    <td>{(history.dw_date != null) ? moment(history.dw_date).format(formatDate) : ""}</td>
                                                    <td>{(history.dw_type != null) ? "Auto(Ftp)" : "Manual(Ftp)"}</td>
                                                    <td>Detail</td>
                                                </tr>
                                            );
                                        })}
                                        </tbody>
                                    </Table>
                                </div>
                            </CardBody>
                            {pagination}
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