import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import FormList, { modalType } from "../FormList";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../../define";
import * as SearchAPI from "../../../../api/SearchList";
import * as AxiosAPI from "../../../../services/axiosAPI";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../../services';

const penderSuccess = {
    success: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': true },
    pending: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': false },
    failure: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': false }
}

const penderFailure= {
    success: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': false },
    pending: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': true },
    failure: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': false }
}
const penderPending = {
    success: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': false },
    pending: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': false },
    failure: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': true }
}

const initialStore= {
    viewList: {
        get: (id) => {
            switch (id) {
                case "toolInfoListCheckCnt": return 1;
                case "toolInfoList":
                    return (
                        List([
                             Map({
                                 checked: true,
                                 collectHostName: null,
                                 collectServerId: "0",
                                 keyIndex: 0,
                                 structId: "Fab1",
                                 targetname: "MPA_1",
                                 targettype: "eesp_data_CKBSTest_1.0.0",
                            })
                     ]));
                case "logInfoListCheckCnt": return 1;
                case "logInfoList":
                    return (
                        List([
                            Map({
                                checked: true,
                                fileListForwarding: "FileListSelectInDirectory",
                                keyIndex: 0,
                                logCode: "001",
                                logName: "001_RUNNING_STATUS",
                                logType: 0,
                            })
                        ]));
            }
        }
    },
    searchList: {
        get: (id) => {
            switch (id) {
                case "responseListCnt": return 1;
                case "startDate": return moment().startOf('day')
                case "endDate": return moment().endOf('day')
                default: return jest.fn()
            }
        }
    },
    pender: penderSuccess,
};

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;
let props;

describe('FormList', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FormList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('renders correctly(isProcessOpen is true)', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FormList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState({ isProcessOpen: true });
        expect(wrapper).toMatchSnapshot();
    });

    it('getIntervalFunc', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FormList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.instance().getIntervalFunc();
    });

    it('setIntervalFunc', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FormList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.instance().setIntervalFunc();
    });

    it('getResStatus', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FormList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.instance().getResStatus();

        wrapper.setProps({
            resSuccess: false,
            resPending: true,
            resError: false,
        });
        wrapper.instance().getResStatus();

        wrapper.setProps({
            resSuccess: false,
            resPending: false,
            resError: true,
        });
        wrapper.instance().getResStatus();

        wrapper.setProps({
            resSuccess: false,
            resPending: false,
            resError: false,
        });
        wrapper.instance().getResStatus();
    });

    it('onSetErrorState', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FormList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.instance().onSetErrorState(Define.SEARCH_FAIL_NO_MACHINE_AND_CATEGORY);
        wrapper.instance().onSetErrorState(Define.SEARCH_FAIL_DATE);
        wrapper.instance().onSetErrorState(Define.SEARCH_FAIL_SERVER_ERROR);
        wrapper.instance().onSetErrorState(-1);
    });

    it('onSearch', async () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FormList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();

        SearchAPI.startSearchList = jest.fn().mockResolvedValue();
        await wrapper.instance().onSearch();

        SearchAPI.setSearchList = jest.fn().mockResolvedValue(Define.SEARCH_FAIL_NO_MACHINE_AND_CATEGORY);
        await wrapper.instance().onSearch();
    });

    it('openModal', async () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FormList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        await wrapper.instance().openModal(modalType.PROCESS);
        await wrapper.instance().openModal(modalType.CANCEL);
        await wrapper.instance().openModal(modalType.ALERT);

        await wrapper.instance().openModal(modalType.CANCEL_COMPLETE);
        jest.useFakeTimers();
        await wrapper.setProps({
            responseListCnt: 0
        })
        AxiosAPI.postCancel = jest.fn();
        await wrapper.instance().openModal(modalType.CANCEL_COMPLETE);
        jest.advanceTimersByTime(200);
        jest.useRealTimers();

        await wrapper.instance().openModal(-1);
    });

    it('closeModal', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FormList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.instance().closeModal(modalType.PROCESS);
        wrapper.instance().closeModal(modalType.CANCEL);
        wrapper.instance().closeModal(modalType.ALERT);
        wrapper.instance().closeModal(-1);
    });

    it('click cancel modal', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FormList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState({ isProcessOpen: true });
        wrapper.find("button").simulate("click");
    });
});