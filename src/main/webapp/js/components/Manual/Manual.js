import React, {Component} from "react";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../modules/viewList";
import * as genreListActions from "../../modules/genreList";
import * as searchListActions from "../../modules/searchList";

import { Container, Row, Col } from "reactstrap";
import Machinelist from "./Machine/MachineList";
import Categorylist from "./Category/CategoryList";
import Formlist from "./Search/FormList";
import Filelist from "./File/FileList";
import Footer from "../common/Footer";
import ScrollToTop from "react-scroll-up";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleDoubleUp} from "@fortawesome/free-solid-svg-icons";

class Manual extends Component {

    async componentDidMount() {
        console.log("componentDidMount");
        const {viewListActions, genreListActions, searchListActions} = this.props;

        searchListActions.searchSetInitAllList();
        genreListActions.genreInitAllList();
        viewListActions.viewInitAllList();
        viewListActions.viewLoadToolInfoList("/api/createToolList");
        viewListActions.viewLoadLogTypeList("/api/createFileTypeList");
        //viewListActions.viewLoadToolInfoList("/test/createToolList");
        //viewListActions.viewLoadLogTypeList("/test/createFileTypeList");
        genreListActions.genreLoadDbList("/db/genre/get");
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
        console.log(this.props.history);

        return (
            <>
                {isSuccess &&
                <>
                    <Container className="rss-container" fluid={true}>
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
                    <ScrollToTop showUnder={160} style={{
                        position: 'fixed',
                        bottom: 50,
                        right: 30,
                        cursor: 'pointer',
                        transitionDuration: '0.2s',
                        transitionTimingFunction: 'linear',
                        transitionDelay: '0s',
                        width: "40px",
                        height: "40px",
                        textAlign: "center",
                        backgroundColor: "rgb(52, 58, 64)",
                        borderRadius: "3px"
                    }}>
                        <span className="scroll-up-icon"><FontAwesomeIcon icon={faAngleDoubleUp} size="lg"/></span>
                    </ScrollToTop>
                </>
                }
                { isFailure &&
                    <div style={{fontsize: 40, marginTop: 400, textAlign: "center"}}>Network Connection Error</div>
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