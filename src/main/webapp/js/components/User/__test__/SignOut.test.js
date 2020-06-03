import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import SignOut from "../SignOut";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../define";
import * as UserAPI from "../../../api/User";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../services';


const initialStore = {
    user: {
        get: (id) => {
            switch (id) {
                case "UserInfo": return Map({
                    name: "",
                    pwd: "",
                    result: 0
                })
                default: return jest.fn();
            }
        }
    },
};

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;
const initProps = {
    isOpen: true,
    right: jest.fn(),
    alertOpen: jest.fn()
};

describe('SignOut', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<SignOut
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('isOpen is false', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<SignOut
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.setProps({
            isOpen: false
        })
    });

    it('openModal, closeModal', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<SignOut
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().openModal();
        wrapper.instance().closeModal();
    });

    it('SignOutProcess', async () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<SignOut
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        await wrapper.instance().SignOutProcess();

        wrapper.setState({
            uInfo:{
                name: "chpark",
                pwd: '',
                cfpwd: '',
                authValue: ''
            },
        })
        await wrapper.instance().SignOutProcess();

        wrapper.setState({
            uInfo:{
                name: "chpark",
                pwd: '12345',
                cfpwd: '',
                authValue: ''
            },
        })
        await wrapper.instance().SignOutProcess();

        wrapper.setState({
            uInfo:{
                name: "chpark",
                pwd: '123456',
                cfpwd: '',
                authValue: ''
            },
        })
        await wrapper.instance().SignOutProcess();

        wrapper.setState({
            uInfo:{
                name: "chpark",
                pwd: '123456',
                cfpwd: '1234567',
                authValue: ''
            },
        })
        await wrapper.instance().SignOutProcess();

        wrapper.setState({
            uInfo:{
                name: "chpark",
                pwd: '123456',
                cfpwd: '123456',
                authValue: ''
            },
        })
        await wrapper.instance().SignOutProcess();

        UserAPI.getUserInfoErrorCode = jest.fn().mockReturnValue(Define.USER_SET_FAIL_SAME_NAME);
        wrapper.setState({
            uInfo:{
                name: "chpark",
                pwd: '123456',
                cfpwd: '123456',
                authValue: ''
            },
        })
        await wrapper.instance().SignOutProcess();

        UserAPI.getUserInfoErrorCode = jest.fn().mockReturnValue(-1);
        wrapper.setState({
            uInfo:{
                name: "chpark",
                pwd: '123456',
                cfpwd: '123456',
                authValue: ''
            },
        })
        await wrapper.instance().SignOutProcess();
    });

    it('handleRadio, handleInput', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<SignOut
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().handleRadio("100");
        wrapper.instance().handleInput ({ target: { name: "name", value: "chpark" }});
        wrapper.instance().handleInput ({ target: { name: "pwd", value: "123456" }});
        wrapper.instance().handleInput ({ target: { name: "cfpwd", value: "123456" }});
        wrapper.instance().handleInput ({ target: { name: "", value: "" }});
    });
});