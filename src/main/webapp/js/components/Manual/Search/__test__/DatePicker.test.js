import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import DatePicker, { CreateDatetimePicker } from "../DatePicker";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../../define";
import * as SearchAPI from "../../../../api/SearchList";
import * as AxiosAPI from "../../../../services/axiosAPI";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../../services';

const initialStore= {
     searchList: {
        get: (id) => {
            switch (id) {
                case "startDate": return moment().startOf('day')
                case "endDate": return moment().endOf('day')
                default: return jest.fn()
            }
        }
    }
};

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;
let props;

describe('DatePicker', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<DatePicker
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('onStartDateChanage', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<DatePicker
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.instance().onStartDateChanage(moment().startOf('day'))
    });

    it('onEndDateChanage', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<DatePicker
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.instance().onEndDateChanage(moment().endOf('day'))
    });

    it('CreateDatetimePicker ', () => {
        CreateDatetimePicker("test", moment().endOf('day'), jest.fn());
    });
});