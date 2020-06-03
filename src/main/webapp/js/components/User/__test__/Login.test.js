import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import Login from "../Login";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../define";
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
};

describe('Login', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<Login
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('renders correctly(error)', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<Login
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.setState({
            errors: {
                username: 'chpark',
                password: '1234',
                ModalMsg: 'test',
            }
        })
        expect(wrapper).toMatchSnapshot();
    });

    it('openModal, closeModal', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<Login
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().openModal();
        wrapper.instance().closeModal();
    });

    it('handleSubmit, handleEnter', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<Login
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.setState({
            username: "chpark",
            password: "1234"
        })
        wrapper.instance().handleSubmit();

        wrapper.setState({
            username: "",
            password: ""
        })
        wrapper.instance().handleSubmit();
    });

    it('handleEnter', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<Login
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();

        const e  = {
            key: "Enter",
            target: {
                blur: jest.fn()
            },
            preventDefault: jest.fn(),
            stopPropagation: jest.fn()
        }
        wrapper.setState({
            isModalOpen: true
        })
        wrapper.instance().handleEnter(e);

        wrapper.setState({
            isModalOpen: false
        })
        wrapper.instance().handleEnter(e);

        wrapper.setState({
            isModalOpen: false
        })
        wrapper.instance().handleEnter({ ...e, key:"" });
    });

    it('loginProcess', async () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<Login
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();

        wrapper.setState({
            username: "chpark",
            password: "1234"
        })
        wrapper.setProps({
            loginInfo: Map({
                    errCode: 0,
                    isLoggedIn: true,
                    username: "chpark",
                    password: "",
                    auth: "100",
            }),
            history: {
                replace: jest.fn()
            }
        })
        await wrapper.instance().loginProcess();

        wrapper.setState({
            username: "chpark",
            password: "1234"
        })
        wrapper.setProps({
            loginInfo: Map({
                errCode: Define.CHANGE_PW_FAIL_EMPTY_PASSWORD,
                isLoggedIn: false,
                username: "chpark",
                password: "",
                auth: "100",
            }),
            history: {
                replace: jest.fn()
            }
        })
        await wrapper.instance().loginProcess();

        wrapper.setState({
            username: "chpark",
            password: "1234"
        })
        wrapper.setProps({
            loginInfo: Map({
                errCode: -1,
                isLoggedIn: false,
                username: "chpark",
                password: "",
                auth: "100",
            }),
            history: {
                replace: jest.fn()
            }
        })
        await wrapper.instance().loginProcess();

        wrapper.setState({
            username: "",
            password: ""
        })
        await wrapper.instance().loginProcess();

        CommonAPI.getErrorMsg = jest.fn().mockResolvedValue("");
        wrapper.setState({
            username: "",
            password: ""
        })
        await wrapper.instance().loginProcess();
    });

    it('handleChange', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<Login
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().handleChange({ target: { name: "username", value: "chpark" } });
        wrapper.instance().handleChange({ target: { name: "username", value: "" } });

        wrapper.instance().handleChange({ target: { name: "password", value: "1234" } });
        wrapper.instance().handleChange({ target: { name: "password", value: "" } });

        wrapper.instance().handleChange({ target: { name: "", value: "" } });
    });
});