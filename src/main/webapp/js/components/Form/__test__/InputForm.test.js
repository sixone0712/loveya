import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import InputForm from "../InputForm";
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
    iType: "test",
    iLabel: "test",
    iName: "test",
    iId: "test",
    iPlaceholder: "test",
    changeFunc: jest.fn(),
    iErrMsg: "test",
    maxLength: 0
};

describe('DateForm', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly', () => {
        const wrapper = shallow(<InputForm
            {...initProps}
        />);
        expect(wrapper).toMatchSnapshot();
    });
});