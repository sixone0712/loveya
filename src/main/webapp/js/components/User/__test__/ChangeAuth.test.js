import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import ChangeAuth from "../ChangeAuth";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../define";
import * as UserAPI from "../../../api/User";

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
    user: {
        get: (id) => {
            switch (id) {
                case "UserList":
                    return Map({
                        isServerErr: false,
                        totalCnt: 13,
                        result: 0,
                        list: List([
                            Map({
                                id:10001,
                                name:"chpark",
                                auth:"100",
                                created:"2020-05-25T20:56:28.844+0000",
                                modified:null,
                                validity:true,
                                last_access:"2020-06-03T02:44:14.012+0000",
                            }),
                            Map({
                                id:10002,
                                name:"ymkwon",
                                auth:"50",
                                created:"2020-05-25T20:56:28.844+0000",
                                modified:null,
                                validity:true,
                                last_access:"2020-06-03T02:44:14.012+0000",
                            })
                        ])
                    });
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
    right: jest.fn(),
    alertOpen: jest.fn()
};

describe('ChangeAuth', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<ChangeAuth
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('renders other conditiotns', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<ChangeAuth
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.setState({
            selectedValue: "test",
        })
        wrapper.setProps({
            isOpen: true
        })
        expect(wrapper).toMatchSnapshot();
    });

    it('changePermissionProcess', async () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<ChangeAuth
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        UserAPI.getUserInfoErrorCode = jest.fn().mockReturnValue(Define.USER_SET_FAIL_SAME_NAME)
        await wrapper.instance().changePermissionProcess("chpark");

        UserAPI.getUserInfoErrorCode = jest.fn().mockReturnValue(0)
        await wrapper.instance().changePermissionProcess("chpark");

        UserAPI.getUserInfoErrorCode = jest.fn().mockReturnValue(-1)
        await wrapper.instance().changePermissionProcess("chpark");
    });
    it('closeModal, handleRadio, settingClose', async () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<ChangeAuth
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().closeModal();
        wrapper.instance().handleRadio("chpark");
        wrapper.instance().settingClose();
    });
});