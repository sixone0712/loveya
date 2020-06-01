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

import OptionList from "../OptionList";
import { CreateDatetimePicker } from "../OptionList";
import * as Define from "../../../define";
//import * as LogInfoListAPI from "../../../api/LogInfoList";

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

describe('OptionList', () => {

    /*
    beforeEach(() => {
        store = mockStore(initialState);
    });
    */

    it('renders correctly', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<OptionList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('collectType is Define.AUTO_MODE_CYCLE', () => {
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
        const wrapper = shallow(<OptionList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
    })

    it('handleDateChange', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<OptionList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        wrapper.instance().handleDateChange(Define.AUTO_DATE_PERIOD_FROM, moment());
        wrapper.instance().handleDateChange(Define.AUTO_DATE_PERIOD_TO, moment());
        wrapper.instance().handleDateChange(Define.AUTO_DATE_COLLECT_START, moment());
        wrapper.instance().handleDateChange(-1, moment());
    });

    it('getDateValue', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<OptionList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        wrapper.instance().getDateValue (Define.AUTO_DATE_PERIOD_FROM);
        wrapper.instance().getDateValue (Define.AUTO_DATE_PERIOD_TO);
        wrapper.instance().getDateValue (Define.AUTO_DATE_COLLECT_START);
        wrapper.instance().getDateValue (-1);
    });

    it('openModal, closeModal', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<OptionList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        wrapper.instance().openModal();
        wrapper.instance().closeModal();
    });

    it('handleModeChange, handlePlanIdChange, handleIntervalChange, handleIntervalUnitChange, handleDiscriptionChange', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<OptionList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        wrapper.instance().handleModeChange(Define.AUTO_MODE_CONTINUOUS);
        wrapper.instance().handleModeChange(Define.AUTO_MODE_CYCLE);

        wrapper.instance().handlePlanIdChange({target: {value: "1"}});
        wrapper.instance().handleIntervalChange({target: {value: "1"}});

        wrapper.instance().handleIntervalUnitChange(Define.AUTO_UNIT_MINUTE);
        wrapper.instance().handleIntervalUnitChange(Define.AUTO_UNIT_HOUR);
        wrapper.instance().handleIntervalUnitChange(Define.AUTO_UNIT_DAY);
        wrapper.instance().handleIntervalUnitChange(-1);

        wrapper.instance().handleDiscriptionChange({target: {value: "Test1234"}});
    });
});

describe('CreateDatetimePicker', () => {

    /*
    beforeEach(() => {
        store = mockStore(initialState);
    });
    */

    it('renders correctly', () => {
        store = mockStore(initialState);
        const props = {
            idx: 0,
            changer: jest.fn()
        }
        const wrapper = shallow(<CreateDatetimePicker {...props} />);
        expect(wrapper).toMatchSnapshot();
    });

    it('handleChange', () => {
        store = mockStore(initialState);
        const props = {
            idx: 0,
            changer: jest.fn()
        }
        const wrapper = shallow(<CreateDatetimePicker {...props} />);
        wrapper.instance().handleChange(moment());
    });
});
