import React, { Component } from "react";
import { Card, CardBody, Col, FormGroup, Button } from "reactstrap";
import DatePicker from "./DatePicker";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import * as viewListActions from "../../modules/viewList";
import * as searchListActions from "../../modules/searchList";
import * as API from "../../api";
import * as Define from "../../define";

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
        this.state = {
           isError: Define.RSS_SUCCESS
        };
    }

    setErrorStatus = (error) => {
        this.setState({
            ...this.state,
            isError: error
        })
    };

    onSerach = async () => {
        let result = Define.RSS_SUCCESS;
        console.log('##########################################################');
        console.log('setSearchList before');
        console.log('##########################################################');
        result = await API.setSearchList(this.props);
        console.log("result", result)
        console.log('##########################################################');
        console.log('setSearchList after');
        console.log('##########################################################');
        this.setErrorStatus(result);
        if(result === Define.RSS_SUCCESS) {
            API.startSearchList(this.props);
        } else {
            API.dispAlert(result);
        }
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
      toolInfoListCheckCnt: state.viewList.get('toolInfoListCheckCnt'),
      logInfoList: state.viewList.get('logInfoList'),
      logInfoListCheckCnt: state.viewList.get('logInfoListCheckCnt'),
      requestList: state.searchList.get('requestList'),
      startDate: state.searchList.get('startDate'),
      endDate: state.searchList.get('endDate'),
      resSuccess: state.pender.success['searchList/SEARCH_LOAD_RESPONSE_LIST'],
      resPending: state.pender.pending['searchList/SEARCH_LOAD_RESPONSE_LIST'],
      resError: state.pender.failure['searchList/SEARCH_LOAD_RESPONSE_LIST'],
    }),
    (dispatch) => ({
      // bindActionCreators 는 액션함수들을 자동으로 바인딩해줍니다.
      viewListActions: bindActionCreators(viewListActions, dispatch),
      searchListActions: bindActionCreators(searchListActions, dispatch)
    })
)(FormList);