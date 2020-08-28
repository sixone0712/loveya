import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import DownloadHistory, { getDownloadType } from "../DownloadHistory";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../define";
import * as DownHistoryAPI from "../../../api/DownloadHistory";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../services';


const initialStore = {
    dlHistory: {
        get: (id) => {
            switch (id) {
                case "dlHistoryInfo":
                    return Map({
                        result: 0,
                        totalCnt: 2,
                        isServerErr: "false",
                        dl_list: List([
                            Map({
                                dl_id: 1,
                                dl_user: "chpark",
                                dl_date: "2020-06-03T04:17:18.155+0000",
                                dl_type: "1",
                                dl_filename: "chpark_Fab2_20200603_122411.zip",
                                dl_status: "Download Completed",
                            }),
                            Map({
                                dl_id: 2,
                                dl_user: "chpark2",
                                dl_date: "2020-06-03T04:17:18.155+0000",
                                dl_type: "2",
                                dl_filename: "chpark2_Fab2_20200603_122411.zip",
                                dl_status: "Download Completed",
                            }),
                        ])
                    })
                default: return jest.fn();
            }
        }
    },
};

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;
const initProps = {
};

describe('DownloadHistory', () => {

    /*
    beforeEach(() => {
    });
     */

    DownHistoryAPI.loadDlHistoryList = jest.fn();

    it('renders correctly', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<DownloadHistory
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('conter is 0', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<DownloadHistory
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.setProps({
            dlHistoryInfo: Map({
                dl_list: List([])
            })
        })

        wrapper.setProps({
            dlHistoryInfo: Map({
                result: 0,
                totalCnt: 1,
                isServerErr: "false",
                dl_list: List([
                    Map({
                        dl_id: 1,
                        dl_user: "chpark",
                        dl_date: null,
                        dl_type: "1",
                        dl_filename: "chpark_Fab2_20200603_122411.zip",
                        dl_status: "Download Completed",
                    }),
                ])
            })
        });
    });

    it('getDownloadType', () => {
        getDownloadType(Define.RSS_TYPE_FTP_MANUAL);
        getDownloadType(Define.RSS_TYPE_FTP_AUTO);
        getDownloadType(Define.RSS_TYPE_VFTP_MANUAL_SSS);
        getDownloadType(Define.RSS_TYPE_VFTP_MANUAL_COMPAT);
        getDownloadType(Define.RSS_TYPE_VFTP_AUTO_SSS);
        getDownloadType(Define.RSS_TYPE_VFTP_AUTO_COMPAT);
        getDownloadType("");
    });

    it('handlePaginationChange', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<DownloadHistory
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().handlePaginationChange(1);
    });

    it('handleSelectBoxChange', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<DownloadHistory
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().handleSelectBoxChange(100);

        wrapper.setState({
            currentPage: 2
        });
        wrapper.instance().handleSelectBoxChange(100);
    });
});