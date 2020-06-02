import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import PlanList from "../PlanList";
import { statusType, detailType, CreateStatus, CreateDetail } from "../PlanList";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../define";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../services';

const initialState = {
    viewList: {
        get: (id) => {
            switch (id) {
                case "toolInfoList":
                    return (
                        List([
                                 Map({
                                keyIndex: 0,
                                structId: "CR7",
                                collectServerId: 0,
                                collectHostName: null,
                                targetname: "EQVM88",
                                targettype: "1",
                                checked: true
                            })
                     ]));
                case "logInfoList":
                    return (
                        List([
                            Map({
                                keyIndex: 1,
                                logType: 0,
                                logCode: "001",
                                logName: "001 RUNNING STATUS",
                                fileListForwarding: null,
                                checked: true
                            })
                    ]));

                default: return jest.fn();
            }
        }
    },
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
let props;
let localState;

describe('PlanList', () => {
    beforeEach(() => {

        services.axiosAPI.get = jest.fn().mockResolvedValue({
            data: [
            {
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
            }
        ]});
        props = {
            history: {
                push: jest.fn()
            }
        }
        localState = {
            registeredList : [{
                collectStart: "2020-06-01 00:00:00",
                collectTypeStr: "cycle",
                expired: undefined,
                id: 235,
                interval: 86400000,
                logType: "001",
                planDescription: "test1234",
                planDetail: "collected",
                planId: "test1",
                planLastRun: "2020-06-01 15:54:10",
                planPeriodEnd: "2020-06-01 23:59:59",
                planPeriodStart: "2020-06-01 00:00:00",
                planStatus: "running",
                planTarget: 1,
                tool: "MPA_1",
            }],
        };
    });

    it('renders correctly(data exist', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<PlanList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('renders correctly(data does not exit', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<PlanList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState(localState);
        expect(wrapper).toMatchSnapshot();
    });

    it('setEditPlanList', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<PlanList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState(localState);
        wrapper.instance().setEditPlanList(235, statusType.RUNNING);
        wrapper.instance().setEditPlanList(235, statusType.STOPPED);
    });

    it('openDeleteModal', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<PlanList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState(localState);
        wrapper.instance().openDeleteModal (235, statusType.RUNNING);
        wrapper.instance().openDeleteModal (235, statusType.STOPPED);
    });

    it('closeDeleteModal', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<PlanList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState(localState);
        services.axiosAPI.get = jest.fn().mockResolvedValue();
        wrapper.instance().closeDeleteModal(false, 235);
        wrapper.instance().closeDeleteModal(true, 235);
    });

    it('openStatusModal', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<PlanList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState(localState);
        wrapper.instance().openStatusModal(statusType.RUNNING, 235);
        wrapper.instance().openStatusModal(statusType.STOPPED, 235);
    });

    it('closeStatusModal', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<PlanList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState(localState);
        wrapper.instance().closeStatusModal();
    });

    it('openAlert, closeAlert', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<PlanList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState(localState);
        wrapper.instance().openAlert();
        wrapper.instance().closeAlert();
    });

    it('handlePaginationChange', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<PlanList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState(localState);
        wrapper.instance().handlePaginationChange(1);
    });

    it('handleSelectBoxChange', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<PlanList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState(localState);
        wrapper.instance().handleSelectBoxChange(1);

        wrapper.setState({
            currentPage: 2
        });
        wrapper.instance().handleSelectBoxChange(1);
    });

    it('stopDownload', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<PlanList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState(localState);
        services.axiosAPI.get = jest.fn().mockResolvedValue();
        wrapper.instance().stopDownload(245);
    });

    it('restartDownload', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<PlanList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState(localState);
        services.axiosAPI.get = jest.fn().mockResolvedValue();
        wrapper.instance().restartDownload(245);
    });

    it('handleStatusChange', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<PlanList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState(localState);
        wrapper.instance().handleStatusChange(statusType.RUNNING, 245);
        wrapper.instance().handleStatusChange(statusType.STOPPED, 245);
        wrapper.instance().handleStatusChange(-1, 245);
    });

    it('lastCollect is null', () => {
        services.axiosAPI.get = jest.fn().mockResolvedValue({
            data: [
                {
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
                    lastCollect: null,
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
                }
            ]});
        store = mockStore(initialState);
        const wrapper = shallow(<PlanList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
    });
});

describe('CreateStatus', () => {
    it('call CreateStatus', () => {
        CreateStatus(statusType.RUNNING, jest.fn());
        CreateStatus(statusType.STOPPED, jest.fn());
        CreateStatus(-1, jest.fn());
    });
});

describe('CreateDetail', () => {
    it('call CreateDetail', () => {
        CreateDetail(detailType.REGISTERED);
        CreateDetail(detailType.COLLECTING);
        CreateDetail(detailType.COLLECTED);
        CreateDetail(detailType.SUSPENDED);
        CreateDetail(detailType.HALTED);
        CreateDetail(detailType.COMPLETED);
        CreateDetail(-1);
    });
});


