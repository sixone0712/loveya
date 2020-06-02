import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import ConfirmModal from "../ConfirmModal";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../../define";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../../services';

const initialStore = {
    viewList: {
        get: (id) => {
            switch (id) {
                case "toolInfoListCheckCnt": return 1;
                case "toolInfoList":
                    return (
                        List([
                             Map({
                                 checked: false,
                                 collectHostName: null,
                                 collectServerId: "0",
                                 keyIndex: 0,
                                 structId: "Fab1",
                                 targetname: "MPA_1",
                                 targettype: "eesp_data_CKBSTest_1.0.0",
                            })
                     ]));
                case "logInfoListCheckCnt": return 1;
                case "logInfoList":
                    return (
                        List([
                            Map({
                                checked: false,
                                fileListForwarding: "FileListSelectInDirectory",
                                keyIndex: 0,
                                logCode: "001",
                                logName: "001_RUNNING_STATUS",
                                logType: 0,
                            })
                    ]));

                default: return jest.fn();
            }
        }
    },
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
    openbtn: "Delete",
    message: "Do you want to delete the selected genre?",
    leftbtn: "Delete",
    rightbtn: "Cancel",
    nowAction: jest.fn(),
    setNowAction: jest.fn(),
    confirmFunc: jest.fn().mockResolvedValue(Define.RSS_SUCCESS),
    selectedGenre: 1,
    selectedGenreName: jest.fn(),
    handleSelectBoxChange: jest.fn(),
    getSelectedIdByName: jest.fn(),
};

describe('ConfirmModal', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<ConfirmModal
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('confirmOpen is true', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<ConfirmModal
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.setState({
            confirmOpen: true
        })
    });

    it('getDerivedStateFromProps', () => {
        const newProps = {
            ...initProps,
            nowAction: "Delete",
            openbtn: "Delete",
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
        const wrapper = shallow(<ConfirmModal
            dispatch={dispatch}
            store={store}
            {...newProps}
        />).dive().dive();
    });

    it('openConfirmModal, closeConfirmModal, openAlertModal, closeAlertModal, canOpenModal', async () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<ConfirmModal
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().openConfirmModal();
        wrapper.instance().closeConfirmModal();
        wrapper.instance().openAlertModal();
        wrapper.instance().closeAlertModal();
        await wrapper.instance().canOpenModal("Delete");
        await wrapper.setProps({
             ...initProps,
            selectedGenre: 0
        })
        await wrapper.instance().canOpenModal("Delete");
    });

    it('actionFunc', async () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<ConfirmModal
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        await wrapper.instance().actionFunc();

        jest.useFakeTimers();
        await wrapper.setProps({
            confirmFunc: jest.fn().mockResolvedValue(Define.GENRE_SET_FAIL_NOT_EXIST_GENRE)
        })
        await wrapper.instance().actionFunc();
        jest.advanceTimersByTime(400);
        jest.useRealTimers();

        jest.useFakeTimers();
        await wrapper.setProps({
            confirmFunc: jest.fn().mockResolvedValue(Define.GENRE_SET_FAIL_NEED_UPDATE)
        })
        await wrapper.instance().actionFunc();
        jest.advanceTimersByTime(400);
        jest.useRealTimers();
    });
});