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

import CheckSetting from "../CheckSetting";
import * as Define from "../../../define";
//import * as LogInfoListAPI from "../../../api/LogInfoList";

const initialState = {
    viewList: {
        get: (id) => {
            switch (id) {
                case "toolInfoListCheckCnt": return 1;
                case "toolInfoList":
                    return (List([
                        Map({
                            checked: false,
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
                                keyIndex: 1,
                                logType: 0,
                                logCode: "001",
                                logName: "001 RUNNING STATUS",
                                fileListForwarding: null,
                                checked: true
                            })
                        ])
                );
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

describe('CheckSetting', () => {

    /*
    beforeEach(() => {
        store = mockStore(initialState);
    });
    */

    it('renders correctly', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<CheckSetting
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('collectType is  Define.AUTO_MODE_CYCLE', () => {
        const newState = {
            ...initialState,
            autoPlan: {
                get: () => {
                    return Map({
                        planId: "test1",
                        collectStart: moment().startOf('day'),
                        from: moment().startOf('day'),
                        to : moment().endOf('day'),
                        collectType: Define.AUTO_MODE_CYCLE,
                        interval: 1,
                        intervalUnit: Define.AUTO_UNIT_MINUTE,
                        description: "this is test1"
                    })
                }
            },
        }
        store = mockStore(newState);
        const wrapper = shallow(<CheckSetting
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
    });
});
