import React, { Component } from "react";
import { Card, CardBody, Col, FormGroup, Button } from "reactstrap";
import DatePicker from "./DatePicker";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../modules/viewList";
import * as searchListActions from "../../modules/searchList";
import * as API from "../../api";

const formGroupStyle = {
  marginBottom: "0px"
};

const buttonPosition = {
  position: "absolute",
  top: "17px",
  right: "20px"
};

class FormList extends Component{

    constructor() {
        super();
    }

    onSerach = async () => {
        console.log('##########################################################');
        console.log('setSearchList before');
        console.log('##########################################################');
        await API.setSearchList(this.props);
        console.log('##########################################################');
        console.log('setSearchList after');
        console.log('##########################################################');
        API.startSearchList(this.props);
        console.log('##########################################################');
        console.log('startSearchList end');
        console.log('##########################################################');

    };

    render() {
        return (
            <Card className="ribbon-wrapper formlist-custom">
                <CardBody className="custom-scrollbar card-body-custom card-body-formlist">
                    <div className="ribbon ribbon-clip ribbon-success">Date</div>
                    <Col>
                        <FormGroup style={formGroupStyle}>
                            <DatePicker/>
                        </FormGroup>
                    </Col>
                    <div style={buttonPosition}>
                        <Button
                            outline size="sm"
                            color="info"
                            className="formlist-btn"
                            onClick={this.onSerach}
                        >
                            Search
                        </Button>
                    </div>
                </CardBody>
            </Card>
        );
    }
}

export default connect(
    (state) => ({
      toolInfoList: state.viewList.get('toolInfoList'),
      logInfoList: state.viewList.get('logInfoList'),
      requestList: state.searchList.get('requestList'),
      startDate: state.searchList.get('startDate'),
      endDate: state.searchList.get('endDate'),
    }),
    (dispatch) => ({
      // bindActionCreators 는 액션함수들을 자동으로 바인딩해줍니다.
      viewListActions: bindActionCreators(viewListActions, dispatch),
      searchListActions: bindActionCreators(searchListActions, dispatch)
    })
)(FormList);