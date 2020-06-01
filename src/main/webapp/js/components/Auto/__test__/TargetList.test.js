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

import TargetList from "../TargetList";
import { CreateCheckBox } from "../TargetList";
import * as Define from "../../../define";
import * as LogInfoListAPI from "../../../api/LogInfoList";

const initialState = {
    viewList: {
        get: (id) => {
            switch (id) {
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
};

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;

describe('TargetList', () => {

    /*
    beforeEach(() => {
        store = mockStore(initialState);
    });
    */

    const initGetLogInfoList = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11].map(idx => ({
        keyIndex: idx,
        logType: 0,
        logCode: "001",
        logName: "001 RUNNING STATUS",
        fileListForwarding: null,
        checked: true
    }))

    it('renders correctly(success)', () => {
        LogInfoListAPI.getLogInfoList = jest.fn().mockReturnValue(initGetLogInfoList);
        store = mockStore(initialState);
        const wrapper = shallow(<TargetList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('renders correctly(fail)', () => {
        LogInfoListAPI.getLogInfoList = jest.fn().mockReturnValue(initGetLogInfoList);
        store = mockStore(initialState);
        const wrapper = shallow(<TargetList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        wrapper.setState({
            filteredData: []
        })
        expect(wrapper).toMatchSnapshot();
    });

    it('handleSearchToggle', () => {
        LogInfoListAPI.getLogInfoList = jest.fn().mockReturnValue(initGetLogInfoList);
        store = mockStore(initialState);
        const wrapper = shallow(<TargetList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        wrapper.instance().handleSearchToggle();

        wrapper.setState({
            showSearch: true
        })
        wrapper.instance().handleSearchToggle();
    });

    it('selectAllItem ', () => {
        LogInfoListAPI.getLogInfoList = jest.fn().mockReturnValue(initGetLogInfoList);
        store = mockStore(initialState);
        const wrapper = shallow(<TargetList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        wrapper.instance().selectAllItem ();
    });

    it('handleCheckboxClick  ', () => {
        LogInfoListAPI.getLogInfoList = jest.fn().mockReturnValue(initGetLogInfoList);
        store = mockStore(initialState);
        const wrapper = shallow(<TargetList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        wrapper.setState({
            filteredData: [{
                title: 1,
                keyIndex: 1,
                logType: 0,
                logCode: "001",
                logName: "001 RUNNING STATUS",
                fileListForwarding: null,
                checked: true
            }]
        })
        let e;
        e = {
            target: {
                id: "test_{#div#}_1"
            }
        }
        wrapper.instance().handleCheckboxClick(e);
        e = {
            target: {
                id: "test_{#div#}_0"
            }
        }
        wrapper.instance().handleCheckboxClick(e);
    });

    it('handleSearch   ', () => {
        LogInfoListAPI.getLogInfoList = jest.fn().mockReturnValue(initGetLogInfoList);
        store = mockStore(initialState);
        const wrapper = shallow(<TargetList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        wrapper.setState({
            filteredData: [{
                title: 1,
                keyIndex: 1,
                logType: 0,
                logCode: "001",
                logName: "001 RUNNING STATUS",
                fileListForwarding: null,
                checked: true
            }]
        })
        let e;
        e = {
            target: {
                value: "001 RUNNING STATUS"
            }
        }
        wrapper.instance().handleSearch (e);
    });

    it('CreateCheckBox  ', () => {
       let initprops = {
            title: 1,
            list : [{
                title: 1,
                keyIndex: 1,
                logType: 0,
                logCode: "001",
                logName: "001 RUNNING STATUS",
                fileListForwarding: null,
                checked: true
            }],
           handleCheckboxClick: jest.fn()
        }
        CreateCheckBox(initprops);

        CreateCheckBox({
            ...initprops,
            title: 0
        });

    });

});
