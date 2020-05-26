import React, { Component } from "react";
import {Col, CardHeader, CardBody, Table, Card, Container, Breadcrumb, BreadcrumbItem, Button} from "reactstrap";
import * as API from "../../api";
import * as Define from "../../define";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as dlHistoryAction from "../../modules/dlHistory";
import { Select } from "antd";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faCheckCircle,
    faUser,
    faExclamationCircle,
    faPlus,
    faTrashAlt,
    faAngleDoubleUp
} from "@fortawesome/free-solid-svg-icons";
import {filePaginate, renderPagination} from "../Common/Pagination";
import Footer from "../Common/Footer";
import ScrollToTop from "react-scroll-up";
import moment from "moment";

const { Option } = Select;



function getDownloadType(type) {
    let typeString = 0;
    console.log("type: ",type);
    typeString = (type == Define.RSS_TYPE_FTP_MANUAL)  ? "Manual download(ftp)"
        : (type == Define.RSS_TYPE_FTP_AUTO)  ? "Auto download(ftp)"
        : (type == Define.RSS_TYPE_VFTP_SSS)  ? "Manual download(VFTP/SSS)"
        : (type == Define.RSS_TYPE_VFTP_COMPAT)  ? "Manual download(VFTP/COMPAT)"
        : " ";
    return typeString;
}
const scrollStyle = {
    backgroundColor: "#343a40",
    width: "40px",
    height: "40px",
    textAlign: "center",
    borderRadius: "3px",
    zIndex: "101"
};
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
        await API.loadDlHistoryList(this.props);
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
        const historyList = API.getDlHistoryList(this.props);
        const {length:count} = historyList;
        console.log("historyList: ",historyList);
        console.log("count: ",count);

        const {currentPage, pageSize} = this.state;
        const lists = filePaginate(historyList, currentPage, pageSize);
         const pagination = renderPagination(
             pageSize,
             count,
             this.handlePaginationChange,
             currentPage,
             "custom-pagination"
         );

        if (count === 0) {
         return (
             <>
                 <Container className="rss-container" fluid={true}>
                     <Breadcrumb className="topic-path">
                         <BreadcrumbItem>Administrator</BreadcrumbItem>
                         <BreadcrumbItem active>Download History</BreadcrumbItem>
                     </Breadcrumb>
                     <Card className="auto-plan-box">
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
                 </Container>
                 <ScrollToTop showUnder={160} style={scrollStyle}>
                     <span className="scroll-up-icon"><FontAwesomeIcon icon={faAngleDoubleUp} size="lg"/></span>
                 </ScrollToTop>
                 <Footer/>
             </>
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
                                            <th>Downloaded File name</th>
                                            <th>Downloaded Type</th>
                                            <th>Status</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {lists.map((history, idx) => {
                                        return (
                                            <tr key={idx}>
                                            	<td>{idx + 1}</td>
                                                <td>{history.dl_user}</td>
                                                <td>{(history.dl_date != null) ? moment(history.dl_date).format(formatDate) : ""}</td>
                                                <td>{history.dl_filename}</td>
                                                <td>{getDownloadType(history.dl_type)}</td>
                                                <td>{history.dl_status}</td>
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
                    <Footer/>
                    <ScrollToTop showUnder={160} style={scrollStyle}>
                        <span className="scroll-up-icon"><FontAwesomeIcon icon={faAngleDoubleUp} size="lg"/></span>
                    </ScrollToTop>
                </>
            );
        }
    }
}

export default connect(
    (state) => ({
        dlHistoryInfo: state.dlHistory.get('dlHistoryInfo'),
    }),
    (dispatch) => ({
        dlHistoryAction: bindActionCreators(dlHistoryAction, dispatch),
    })
)(DownloadHistory);