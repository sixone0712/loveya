import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import ChangePw from "../ChangePw";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../define";
import * as UserAPI from "../../../api/User";
import * as LoginAPI from "../../../api/Login";
import * as CommonAPI from "../../../api/Common";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../services';


const initialStore = {
    login: {
        get: (id) => {
            switch (id) {
                case "loginInfo":
                    return Map({
                        errCode: 0,
                        isLoggedIn: true,
                        username: "chpark",
                        password: "",
                        auth: "100",
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
    isOpen: jest.fn(),
    right: jest.fn(),
    alertOpen: jest.fn()
};

describe('ChangePw', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<ChangePw
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('renders correctly(other conditions)', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<ChangePw
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.setProps({
            isOpen: false
        })
        wrapper.setProps({
            isOpen: true
        })
    });

    it('handleSubmit', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<ChangePw
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.setState({
            newPw: "",
            confirmPw: ""
        })
        wrapper.instance().handleSubmit();

        wrapper.setState({
            newPw: "!@#$",
            confirmPw: "!@#$"
        })
        wrapper.instance().handleSubmit();

        wrapper.setState({
            newPw: "123456",
            confirmPw: "1234567"
        })
        wrapper.instance().handleSubmit();

        wrapper.setState({
            newPw: "123456",
            confirmPw: "123456"
        })
        wrapper.instance().handleSubmit();
    });

    it('changePwProcess', async () => {
        const newStore = {
            login: {
                get: (id) => {
                    switch (id) {
                        case "loginInfo":
                            return Map({
                                errCode: 0,
                                isLoggedIn: true,
                                username: "chpark",
                                password: "",
                                auth: "100",
                            })
                        default: return jest.fn();
                    }
                }
            },
        }
        store = mockStore(initialStore);
        const wrapper = shallow(<ChangePw
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();

        wrapper.setState({
            newPw: "123456",
            confirmPw: "123456"
        })
        await wrapper.instance().changePwProcess();

        wrapper.setState({
            newPw: "123456",
            confirmPw: "1234567"
        })
        await wrapper.instance().changePwProcess();

        LoginAPI.getErrCode = jest.fn().mockReturnValue(Define.CHANGE_PW_FAIL_INCORRECT_CURRENT_PASSWORD);
        wrapper.setState({
            newPw: "123456",
            confirmPw: "123456"
        })
        await wrapper.instance().changePwProcess();
    });

    it('changeHandler)', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<ChangePw
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();

        const e = {
            target: {
                name: "password",
                value: "1234546"
            }
        }
        wrapper.instance().changeHandler(e);
    });
});