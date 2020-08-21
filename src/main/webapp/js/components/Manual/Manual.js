import React, {Component} from "react";
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

    // Moved to MoveRefreshPage.js
    /*
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
    */

    render() {
        return (
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
        );
    }
}

export default Manual;