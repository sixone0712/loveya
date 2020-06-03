import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import UserAuthForm from "../UserAuthForm";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../define";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../services';


const initialStore = {
};

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;
const initProps = {
    sValue: "test",
    changeFunc: jest.fn()
};

describe('UserAuthForm', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly', () => {
        const wrapper = shallow(<UserAuthForm
            {...initProps}
        />);
        expect(wrapper).toMatchSnapshot();

        wrapper.find('input').at(0).simulate('change', { target: { value: "test" } });
        wrapper.find('input').at(1).simulate('change', { target: { value: "test" } });
        wrapper.find('input').at(2).simulate('change', { target: { value: "test" } });
        wrapper.find('input').at(3).simulate('change', { target: { value: "test" } });
    });
});