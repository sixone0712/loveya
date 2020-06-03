import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import InputModal from "../InputModal";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../../define";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../../services';

const initialStore = {
    genreList: {
        get: (id) => Map({
            isServerErr: false,
            totalCnt: 1,
            curIdx: "",
            needUpdate : false,
            update: "2020-06-01T01:44:43.416+0000",
            result: 0,
            list: List([
                Map({
                    id: 1,
                    name: "Test1",
                    category: List(["001", "002", "003"]),
                    created: "2020-05-27T04:22:54.751+0000",
                    modified: "2020-05-27T04:22:54.751+0000",
                    validity: true
                })
            ])
        })
    },
};

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;
const initProps = {
    title: "Create Genre",
    openbtn: "Create",
    inputname: "genName",
    inputpholder: "Enter Genre Name",
    leftbtn: "Create",
    rightbtn: "Cancel",
    nowAction: jest.fn(),
    setNowAction: jest.fn(),
    confirmFunc: jest.fn(),
    selectedGenre: jest.fn(),
    selectedGenreName: jest.fn(),
    logInfoListCheckCnt: jest.fn(),
    handleSelectBoxChange: jest.fn(),
    getSelectedIdByName: jest.fn(),
};

describe('InputModal', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<InputModal
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('inputOpen and alertOpen are true', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<InputModal
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.setState({
            inputOpen: true,
        })
        wrapper.setState({
            alertOpen : true,
        })
    });

    it('getDerivedStateFromProps', () => {
        const newProps = {
            ...initProps,
            nowAction: "Create",
            openbtn: "Create",
        }
        const newStore = {
            ...initialStore,
            genreList: {
                    get: (id) => Map({
                        isServerErr: true,
                        totalCnt: 1,
                        curIdx: "",
                        needUpdate : false,
                        update: "2020-06-01T01:44:43.416+0000",
                        result: 0,
                        list: List([
                            Map({
                                id: 1,
                                name: "Test1",
                                category: List(["001", "002", "003"]),
                                created: "2020-05-27T04:22:54.751+0000",
                                modified: "2020-05-27T04:22:54.751+0000",
                                validity: true
                            })
                        ])
                    })
            },
        }
        store = mockStore(newStore);
        const wrapper = shallow(<InputModal
            dispatch={dispatch}
            store={store}
            {...newProps}
        />).dive().dive();
    });

    it('openInputModal, closeInputModal, openAlertModal, closeAlertModal, canOpenModal', async () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<InputModal
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().openInputModal();
        wrapper.instance().closeInputModal();
        await wrapper.instance().openAlertModal();
        await wrapper.instance().closeAlertModal();
        await wrapper.setProps({
            logInfoListCheckCnt: 0
        })
        await wrapper.instance().canOpenModal("Create");
        await wrapper.setProps({
            logInfoListCheckCnt: 1
        })
        await wrapper.instance().canOpenModal("Create");

        await wrapper.setProps({
            selectedGenre : 0,
        })
        await wrapper.instance().canOpenModal("Edit");
        await wrapper.setProps({
            selectedGenre : 1,
            logInfoListCheckCnt: 0
        })
        await wrapper.instance().canOpenModal("Edit");
        await wrapper.setProps({
            selectedGenre : 1,
            logInfoListCheckCnt: 1
        })
        await wrapper.instance().canOpenModal("Edit");
        await wrapper.instance().canOpenModal("");
    });

    it('actionFunc', async () => {
        const newStore = {
            ...initialStore,
            genreList: {
                get: (id) => Map({
                    isServerErr: true,
                    totalCnt: 1,
                    curIdx: "",
                    needUpdate : true,
                    update: "2020-06-01T01:44:43.416+0000",
                    result: 0,
                    list: List([
                        Map({
                            id: 1,
                            name: "Test1",
                            category: List(["001", "002", "003"]),
                            created: "2020-05-27T04:22:54.751+0000",
                            modified: "2020-05-27T04:22:54.751+0000",
                            validity: true
                        })
                    ])
                })
            },
        }
        store = mockStore(initialStore);
        const wrapper = shallow(<InputModal
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        await wrapper.instance().actionFunc();

        await wrapper.setState({ genreName: "Test1234" })
        await wrapper.instance().actionFunc();

        await wrapper.setState({ genreName: "!@#$Test1234" })
        await wrapper.instance().actionFunc();

        await wrapper.setState({ genreName: "Test1234" });
        await wrapper.setProps({ confirmFunc: jest.fn().mockResolvedValue(Define.RSS_SUCCESS) });
        await wrapper.instance().actionFunc();
        await wrapper.setState({ genreName: "Test1234" });
        await wrapper.setProps({ confirmFunc: jest.fn().mockResolvedValue(Define.GENRE_SET_FAIL_SAME_NAME) });
        await wrapper.instance().actionFunc();
        await wrapper.setState({ genreName: "Test1234" });
        await wrapper.setProps({ confirmFunc: jest.fn().mockResolvedValue(Define.GENRE_SET_FAIL_EMPTY_NAME) });
        await wrapper.instance().actionFunc();
        await wrapper.setState({ genreName: "Test1234" });
        await wrapper.setProps({ confirmFunc: jest.fn().mockResolvedValue(Define.GENRE_SET_FAIL_SEVER_ERROR) });
        await wrapper.instance().actionFunc();

        jest.useFakeTimers();
        await wrapper.setState({ genreName: "Test1234" });
        await wrapper.setProps({ confirmFunc: jest.fn().mockResolvedValue(Define.GENRE_SET_FAIL_NOT_EXIST_GENRE) });
        await wrapper.instance().actionFunc();
        jest.advanceTimersByTime(400);
        jest.useRealTimers();

        await wrapper.setState({ genreName: "Test1234" });
        await wrapper.setProps({ confirmFunc: jest.fn().mockResolvedValue(-1) });
        await wrapper.instance().actionFunc();
        await wrapper.setState({ genreName: "Test1234" });
        await wrapper.setProps({
            confirmFunc: jest.fn().mockResolvedValue(-1),
            genreList:
                    Map({
                    isServerErr: true,
                    totalCnt: 1,
                    curIdx: "",
                    needUpdate : true,
                    update: "2020-06-01T01:44:43.416+0000",
                    result: 0,
                    list: List([
                        Map({
                            id: 1,
                            name: "Test1",
                            category: List(["001", "002", "003"]),
                            created: "2020-05-27T04:22:54.751+0000",
                            modified: "2020-05-27T04:22:54.751+0000",
                            validity: true
                        })
                    ])
                })
        });
        await wrapper.instance().actionFunc();
    });
});