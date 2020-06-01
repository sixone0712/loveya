import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import sinon from "sinon";
import moment from "moment";
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../services';

import DownloadList from "../DownloadList";
import { statusType, modalType, CreateStatus } from "../DownloadList";
import * as Define from "../../../define";

const initialState = {
    autoPlan: {
        get: () => {
            return Map({
                planId: "test1",
                collectStart: moment().startOf('day'),
                from: moment().startOf('day'),
                to : moment().endOf('day'),
                collectType: Define.AUTO_MODE_CONTINUOUS,
                interval: 1,
                intervalUnit: Define.AUTO_UNIT_MINUTE,
                description: "this is test1"
            })
        }
    },
};

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;

describe('DownloadList', () => {
    beforeEach(() => {
        services.axiosAPI.get = jest.fn().mockResolvedValue({
            data: [{
                collectStart: "2020-05-31T15:00:00.000+0000",
                collectTypeStr: "cycle",
                collectionType: 1,
                created: "2020-06-01T06:54:24.099+0000",
                description: "test1234",
                detail: "collected",
                end: "2020-06-01T14:59:59.000+0000",
                fab: "Fab1",
                id: 235,
                interval: 86400000,
                lastCollect: "2020-06-01T06:54:10.334+0000",
                lastPoint: "2020-06-01T06:44:34.000+0000",
                lastStatus: null,
                logType: "001",
                logTypeStr: "001_RUNNING_STATUS",
                nextAction: "2020-06-02T06:54:10.334+0000",
                owner: 10005,
                planName: "test1",
                planStatus: null,
                start: "2020-05-31T15:00:00.000+0000",
                status: "running",
                stop: false,
                tool: "MPA_1",
            }],
        });
    });

    const props = {
        location: {
            search: "?id=235&name=test1"
        }
    }

    it('renders correctly(there is downloadlist)', () => {
        const wrapper = shallow(<DownloadList {...props} />)
        expect(wrapper).toMatchSnapshot();
    });

    it('renders correctly(there is not downloadlist)', () => {
        services.axiosAPI.get = jest.fn().mockResolvedValue({ data: ""});
        const wrapper = shallow(<DownloadList {...props} />)
        expect(wrapper).toMatchSnapshot();
    });

    it('openModal, closeModal', () => {
        const wrapper = shallow(<DownloadList {...props} />)
        wrapper.instance().openModal(modalType.MODAL_DELETE);
        wrapper.instance().openModal(modalType.MODAL_DOWNLOAD_1);
        wrapper.instance().openModal(modalType.MODAL_DOWNLOAD_2);
        wrapper.instance().openModal(modalType.MODAL_ALERT);
        wrapper.instance().openModal(modalType.MODAL_NETWORK_ERROR);
        wrapper.instance().openModal(modalType.MODAL_FILE_NOT_FOUND);
        wrapper.instance().openModal(-1);

        wrapper.instance().closeModal();
    });

    it('handlePaginationChange', () => {
        const wrapper = shallow(<DownloadList {...props} />)
        wrapper.instance().handlePaginationChange(0);
    });

    it('handleSelectBoxChange ', () => {
        const wrapper = shallow(<DownloadList {...props} />)
        wrapper.instance().handleSelectBoxChange(10);

        wrapper.setState({
            pageSize: 2,
            currentPage: 2,
        })
        wrapper.instance().handleSelectBoxChange(10);
    });

    it('checkNewDownloadFile  ', () => {
        const wrapper = shallow(<DownloadList {...props} />)
        wrapper.setState({
            currentPage: 1,
            pageSize: 10,
            requestList: [{
                id: 65,
                path: "planroot\\235\\235_1590994449868.zip",
                planId: 235,
                requestId: "2020-06-01 15:54:10",
                requestStatus: "new"
            }]
        });
        wrapper.instance().checkNewDownloadFile();

        wrapper.setState({
            currentPage: 0,
            pageSize: 10,
            requestList: [{
                id: 65,
                path: "planroot\\235\\235_1590994449868.zip",
                planId: 235,
                requestId: "2020-06-01 15:54:10",
                requestStatus: "finished"
            }]
        });
        wrapper.instance().checkNewDownloadFile();
    });

    it('saveDownloadFile  ', () => {
        services.axiosAPI.downloadFile = jest.fn().mockResolvedValue({
            result:  Define.RSS_SUCCESS,
            fileName: "chpark_Fab1_20200601_155410.zip"
        });
        const wrapper = shallow(<DownloadList {...props} />)
        wrapper.instance().saveDownloadFile();

        services.axiosAPI.downloadFile = jest.fn().mockResolvedValue({
            result:  Define.COMMON_FAIL_NOT_FOUND,
            fileName: ""
        });
        wrapper.instance().saveDownloadFile();

        services.axiosAPI.downloadFile = jest.fn().mockResolvedValue({
            result:  Define.MODAL_NETWORK_ERROR,
            fileName: ""
        });
        wrapper.instance().saveDownloadFile();

        wrapper.setState({
            download: {
                id: "",
            }
        })
        wrapper.instance().saveDownloadFile();
    });

    it('deleteDownloadFile, requestDelete', () => {
        const wrapper = shallow(<DownloadList {...props} />);
        services.axiosAPI.get = jest.fn().mockResolvedValue({ data : "test"});
        wrapper.setState({
            currentPage: 1,
            pageSize: 10,
            requestList: [{
                id: 65,
                path: "planroot\\235\\235_1590994449868.zip",
                planId: 235,
                requestId: "2020-06-01 15:54:10",
                requestStatus: "new"
            }],
            delete: {
                id: ""
            }
        });
        wrapper.instance().deleteDownloadFile();

        wrapper.setState({
            currentPage: 1,
            pageSize: 10,
            requestList: [{
                id: 65,
                path: "planroot\\235\\235_1590994449868.zip",
                planId: 235,
                requestId: "2020-06-01 15:54:10",
                requestStatus: "new"
            }],
            delete: {
                id: "65"
            }
        })
        wrapper.setState({
            currentPage: 0,
            pageSize: 10,
            requestList: [{
                id: 65,
                path: "planroot\\235\\235_1590994449868.zip",
                planId: 235,
                requestId: "2020-06-01 15:54:10",
                requestStatus: "new"
            }],
            delete: {
                id: "65"
            }
        });
        wrapper.instance().deleteDownloadFile();

        let error;
        error = {
            response: {
                status: 404
            }
        }
        services.axiosAPI.get = jest.fn().mockRejectedValue(error);
        wrapper.instance().deleteDownloadFile();

        error = {
            response: {
                status: 0
            }
        }
        services.axiosAPI.get = jest.fn().mockRejectedValue(error);
        wrapper.instance().deleteDownloadFile();

        error = { };
        services.axiosAPI.get = jest.fn().mockRejectedValue(error);
        wrapper.instance().deleteDownloadFile();
    });
});

describe('CreateStatus', () => {
    it('CreateStatus', () => {
        CreateStatus(statusType.STATUS_NEW);
        CreateStatus(statusType.STATUS_FINISHED);
        CreateStatus("");
    });
});