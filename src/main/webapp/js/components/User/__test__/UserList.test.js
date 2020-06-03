import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import UserList from "../UserList";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../define";
import * as UserAPI from "../../../api/User";
import * as CommonAPI from "../../../api/Common";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../services';


const initialStore = {
    user: {
        get: (id) => {
            switch (id) {
                case "UserList":
                    return Map({
                        isServerErr: false,
                        totalCnt: 2,
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
const userListData = [
    {
        created: "2020-05-25T20:56:28.844+0000",
        id: 10001,
        lastAccess: "2020-05-29T04:33:34.416+0000",
        modified: "2020-06-03T02:45:12.192+0000",
        password: "c4ca4238a0b923820dcc509a6f75849b",
        permissions: "100",
        username: "chpark",
        validity: true,
    },
    {
        created: "2020-05-25T20:56:28.844+0000",
        id: 10002,
        lastAccess: "2020-05-27T03:49:11.901+0000",
        modified: "2020-05-26T12:04:39.739+0000",
        password: "c4ca4238a0b923820dcc509a6f75849b",
        permissions: "50",
        username: "ymkwon",
        validity: true,
    }
];

const userListDataFalse = [
    {
        created: null,
        id: 10001,
        lastAccess: null,
        modified: "2020-06-03T02:45:12.192+0000",
        password: "c4ca4238a0b923820dcc509a6f75849b",
        permissions: "100",
        username: "chpark",
        validity: true,
    },
];

describe('UserList', () => {


    beforeEach(() => {
        console.log("!!!!!!!!!!!!!!!!!!!!!!!!");
        UserAPI.getDBUserList = jest.fn().mockResolvedValue({ data: { data: userListData } });
    });

    it('renders correctly', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<UserList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });


    it('renders correctly(other condition)', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<UserList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();

        wrapper.setState({
            isModalOpen: true,
            isMode: 'deleteUser'
        })

        wrapper.setState({
            isModalOpen: true,
            isMode: 'ChangAuth'
        })

        wrapper.setState({
            isModalOpen: true,
            isMode: 'SignOut'
        })
    });

    it('renders correctly(other condition2)', () => {
        UserAPI.getDBUserList = jest.fn().mockResolvedValue({ data: { data: userListDataFalse } });
        store = mockStore(initialStore);
        const wrapper = shallow(<UserList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
    });

    it('openAlert', async () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<UserList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        jest.useFakeTimers();
        await wrapper.instance().openAlert("permission");
        jest.advanceTimersByTime(200);
        jest.useRealTimers();

        jest.useFakeTimers();
        await wrapper.instance().openAlert("create");
        jest.advanceTimersByTime(200);
        jest.useRealTimers();

        jest.useFakeTimers();
        await wrapper.instance().openAlert("delete");
        jest.advanceTimersByTime(200);
        jest.useRealTimers();

        jest.useFakeTimers();
        await wrapper.instance().openAlert("");
        jest.advanceTimersByTime(200);
        jest.useRealTimers();
    });

    it('closeAlert, closeModal', async () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<UserList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().closeAlert();
        await wrapper.instance().closeModal();
    });

    it('uDelete , uChangeAuth, handlePaginationChange, handleSelectBoxChange ', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<UserList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().uDelete("chpark", 1);
        wrapper.instance().uChangeAuth("chpark");
        wrapper.instance().handlePaginationChange(1);
        wrapper.instance().handleSelectBoxChange(100);
        wrapper.setState({
            currentPage: 2
        })
        wrapper.instance().handleSelectBoxChange(100);
    });

    it('DeleteAccount', async () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<UserList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();

        UserAPI.getUserInfoErrorCode = jest.fn().mockReturnValue(0);
        await wrapper.instance().DeleteAccount();

        UserAPI.getUserInfoErrorCode = jest.fn().mockReturnValue(0);
        wrapper.setState({
            deleteIndex: 1
        })
        await wrapper.instance().DeleteAccount();

        UserAPI.getUserInfoErrorCode = jest.fn().mockReturnValue(Define.LOGIN_FAIL_NO_REGISTER_USER);
        await wrapper.instance().DeleteAccount();

        CommonAPI.getErrorMsg = jest.fn().mockReturnValue("")
        UserAPI.getUserInfoErrorCode = jest.fn().mockReturnValue(Define.LOGIN_FAIL_NO_REGISTER_USER);
        await wrapper.instance().DeleteAccount();
    });
});