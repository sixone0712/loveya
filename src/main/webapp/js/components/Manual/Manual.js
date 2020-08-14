import React, {Component} from "react";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../modules/viewList";
import * as genreListActions from "../../modules/genreList";
import * as searchListActions from "../../modules/searchList";
import * as Define from '../../define';
import {Breadcrumb, BreadcrumbItem, Col, Container, Row} from "reactstrap";
import Machinelist from "./Machine/MachineList";
import Categorylist from "./Category/CategoryList";
import Formlist from "./Search/FormList";
import Filelist from "./File/FileList";
import Footer from "../Common/Footer";
import ScrollToTop from "react-scroll-up";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleDoubleUp} from "@fortawesome/free-solid-svg-icons";

const scrollStyle = {
    backgroundColor: "#343a40",
    width: "40px",
    height: "40px",
    textAlign: "center",
    borderRadius: "3px",
    zIndex: "101"
};

class Manual extends Component {

    componentDidMount() {
      const loadInfos = async () => {
        try {
          console.log("[Manual][componentDidMount]componentDidMount");
          const {viewListActions, genreListActions, searchListActions} = this.props;

          await viewListActions.viewInitAllList();
          await searchListActions.searchSetInitAllList();
          await genreListActions.genreInitAllList();

          await viewListActions.viewLoadToolInfoList(Define.REST_INFOS_GET_MACHINES);
          const {toolInfoList} = this.props;
          const targetname = toolInfoList.getIn([0, "targetname"]);
          console.log("[Manual][componentDidMount]toolInfoList", toolInfoList.toJS());
          console.log("[Manual][componentDidMount]targetname", targetname);
          await viewListActions.viewLoadLogTypeList(`${Define.REST_INFOS_GET_CATEGORIES}/${targetname}`);

          await genreListActions.genreLoadDbList(Define.REST_API_URL + "/genre/get");
        } catch (e) {
          console.error(e);
        }
      }
      loadInfos().then(r => r).catch(e => console.log(e));
    }

    render() {
        const {
            logTypeSuccess,
            toolInfoSuccess,
            genreSuccess,
            logTypeFailure,
            toolInfoFailure,
            genreFailure
        } = this.props;
        const isSuccess = logTypeSuccess && toolInfoSuccess && genreSuccess;
        const isFailure = logTypeFailure || toolInfoFailure || genreFailure;
        console.log("isSuccess", isSuccess);
        console.log("isFailure", isFailure);
        //console.log(this.props.history);

        return (
            <>
                {isSuccess &&
                <>
                    <Container className="rss-container manual" fluid={true}>
                        <Breadcrumb className="topic-path">
                            <BreadcrumbItem>Manual Download</BreadcrumbItem>
                            <BreadcrumbItem active>FTP</BreadcrumbItem>
                        </Breadcrumb>
                        <Row>
                            <Col>
                                <Machinelist/>
                            </Col>
                            <Col>
                                <Categorylist/>
                            </Col>
                            <Col>
                                <Formlist/>
                            </Col>
                        </Row>
                        <Filelist/>
                    </Container>
                    <Footer/>
                    <ScrollToTop showUnder={160} style={scrollStyle}>
                        <span className="scroll-up-icon"><FontAwesomeIcon icon={faAngleDoubleUp} size="lg"/></span>
                    </ScrollToTop>
                </>
                }
                { isFailure &&
                    <div className="network-connection-error">Network Connection Error</div>
                }
            </>
        );
    }
}

export default connect(
    (state) => ({
        equipmentList: state.viewList.get('equipmentList'),
        toolInfoList: state.viewList.get('toolInfoList'),
        logInfoList: state.viewList.get('logInfoList'),
        genreList: state.genreList.get('genreList'),
        requestList: state.searchList.get('requestList'),
        responseList: state.searchList.get('responseList'),
        startDate: state.searchList.get('startDate'),
        endDate: state.searchList.get('endDate'),
        logTypeSuccess: state.pender.success['viewList/VIEW_LOAD_TOOLINFO_LIST'],
        toolInfoSuccess: state.pender.success['viewList/VIEW_LOAD_LOGTYPE_LIST'],
        genreSuccess: state.pender.success['genreList/GENRE_LOAD_DB_LIST'],
        logTypeFailure: state.pender.failure['viewList/VIEW_LOAD_TOOLINFO_LIST'],
        toolInfoFailure: state.pender.failure['viewList/VIEW_LOAD_LOGTYPE_LIST'],
        genreFailure: state.pender.failure['genreList/GENRE_LOAD_DB_LIST'],
    }),
    (dispatch) => ({
        viewListActions: bindActionCreators(viewListActions, dispatch),
        genreListActions: bindActionCreators(genreListActions, dispatch),
        searchListActions: bindActionCreators(searchListActions, dispatch)
    })
)(Manual);