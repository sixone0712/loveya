import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import DateForm, { CreateDatetimePicker } from "../DateForm";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../define";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../services';
import PropTypes from "prop-types";


const initialStore = {
};

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;
const initProps = {
    startDate: moment().startOf("day"),
    endDate: moment().endOf("day"),
    sDateChanageFunc: jest.fn(),
    eDateChanageFunc: jest.fn()
};

describe('DateForm', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly', () => {
        const wrapper = shallow(<DateForm
            {...initProps}
        />);
        expect(wrapper).toMatchSnapshot();
    });

    it('CreateDatetimePicker', () => {
        CreateDatetimePicker({
            label: "test",
            date: moment(),
            onChangeDate: jest.fn()
        });
    });
});