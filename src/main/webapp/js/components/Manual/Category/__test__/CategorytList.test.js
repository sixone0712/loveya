import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import CategoryList, { customSelectStyles } from "../CategoryList";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../../define";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../../services';
import * as genreAPI from '../../../../api/GenreList'

const initialStore= {
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
let props;

describe('CategoryList', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('renders exception condition', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState({
            query: "!@#$%^&"
        })

        wrapper.setState({
            showSearch : true
        })

        wrapper.setState({
            ItemsChecked : true
        })

        wrapper.setState({
            showGenre  : true
        })
        expect(wrapper).toMatchSnapshot();
    });

    it('handleGenreToggle', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();

        wrapper.instance().handleGenreToggle();

        wrapper.setState({
            showSearch: true
        })
        jest.useFakeTimers();
        wrapper.instance().handleGenreToggle();
        jest.advanceTimersByTime(400);
        jest.useRealTimers();
    });

    it('handleSearchToggle', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();

        jest.useFakeTimers();
        wrapper.instance().handleSearchToggle();
        jest.advanceTimersByTime(400);
        jest.useRealTimers();

        wrapper.setState({
            showGenre: true
        })
        jest.useFakeTimers();
        wrapper.instance().handleSearchToggle();
        jest.advanceTimersByTime(400);
        jest.useRealTimers();
    });

    it('handleSelectBoxChange', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();

        const genreObj = {
            value: 1,
            label: "test"
        }
        wrapper.instance().handleSelectBoxChange(genreObj);
        wrapper.instance().handleSelectBoxChange(null);
        wrapper.instance().handleSelectBoxChange(0);
        wrapper.instance().handleSelectBoxChange(1);
    });

    it('getSelectedIdByName', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.instance().getSelectedIdByName("Test1");
    });

    it('checkCategoryItem', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        const e = {
            target: {
                id: "Test1_{#div#}_1"
            }
        }
        wrapper.instance().checkCategoryItem(e);
    });

    it('checkAllLogInfoList', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.instance().checkAllLogInfoList(false);
        wrapper.instance().checkAllLogInfoList(true);
    });

    it('addGenreList', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        genreAPI.addGenreList = jest.fn().mockResolvedValue(true);
        wrapper.instance().addGenreList(2, "Test2")
    });

    it('editGenreList', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        genreAPI.editGenreList = jest.fn().mockResolvedValue(true);
        wrapper.instance().editGenreList(2, "Test2")
    });

    it('deleteGenreList', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        genreAPI.deleteGenreList = jest.fn().mockResolvedValue(true);
        wrapper.instance().deleteGenreList(2, "Test2")
    });

    it('handleSearch', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        const e = {
            target: {
                value: "Test"
            }
        }
        wrapper.instance().handleSearch(e)
    });

    it('handleSearch', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        const e = {
            target: {
                value: "Test"
            }
        }
        wrapper.instance().handleSearch(e)
    });

    it('setNowAction', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.instance().setNowAction("Test1")
    });

    it('selectFilter', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<CategoryList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.instance().selectFilter({ label: "1234"}, "Test1234");
        wrapper.instance().selectFilter({ label: "1234"}, "");
    });

    it('customSelectStyles', () => {
        const { container, option, control, dropdownIndicator, indicatorSeparator, menu} = customSelectStyles ;
        const style = {
            testStyle: "testStyle"
        }
        container(style, { isDisabled: false, isFocused: false, isSelected: false});
        container(style, { isDisabled: true, isFocused: false, isSelected: false});
        option(style, { isDisabled: true, isFocused: false, isSelected: false});
        option(style, { isDisabled: true, isFocused: true, isSelected: false});
        option(style, { isDisabled: true, isFocused: false, isSelected: true});
        control();
        dropdownIndicator(style);
        indicatorSeparator(style);
        menu(style);
    });
});