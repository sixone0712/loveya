import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import FileList from "../FileList";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../../define";
import * as DownloadAPI from "../../../../api/SearchList";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../../services';

const penderSuccess = {
    success: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': true },
    pending: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': false },
    failure: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': false }
}

const penderFailure= {
    success: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': false },
    pending: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': true },
    failure: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': false }
}
const penderPending = {
    success: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': false },
    pending: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': false },
    failure: { 'searchList/SEARCH_LOAD_RESPONSE_LIST': true }
}

const initialStore = {
    searchList: {
        get: (id) => {
            switch (id) {
                case "responseList":
                    return List([
                        Map({
                            checked: true,
                            file: true,
                            fileDate: "20200602000000",
                            fileId: 0,
                            fileName: "20200602000000",
                            filePath: "20200602000000",
                            fileSize: 71688,
                            fileStatus: "",
                            keyIndex: 0,
                            logId: "001",
                            logName: "001_RUNNING_STATUS",
                            sizeKB: "70.0 KB",
                            structId: "Fab1",
                            targetName: "MPA_1",
                        }),
                        Map({
                            checked: true,
                            file: true,
                            fileDate: "20200602000100",
                            fileId: 0,
                            fileName: "20200602000100",
                            filePath: "20200602000100",
                            fileSize: 68424,
                            fileStatus: "",
                            keyIndex: 1,
                            logId: "001",
                            logName: "001_RUNNING_STATUS",
                            sizeKB: "66.8 KB",
                            structId: "Fab1",
                            targetName: "MPA_1",
                        })
                    ]);
                case "responseListCnt": return 2;
                case "downloadCnt": return 1;
                case "downloadStatus":
                    return Map({
                        dlId: "",
                        downloadFiles: 0,
                        func: null,
                        status: "init",
                        totalFiles: 1,
                    });
                case "responsePerPage": return 10;
                default: return jest.fn();
            }
        }
    },
    pender: penderSuccess,
};

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;
const initProps = {
};

describe('FileList', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('pender resError is true', () => {
        const newStore = {
            ...initialStore,
            pender: penderFailure
        }
        store = mockStore(newStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
    });

    it('itemsChecked is false', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.setState({
            itemsChecked: false
        })
    });

    it('setErrorMsg', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().setErrorMsg(-1);
        wrapper.instance().setErrorMsg(Define.FILE_FAIL_NO_ITEM);
    });

    it('openDownloadModal', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().openDownloadModal();

        wrapper.setProps({
            downloadCnt: 0
        })
        wrapper.instance().openDownloadModal();
    });

    it('closeDownloadModal', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().closeDownloadModal();
    });

    it('openProcessModal', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();

        DownloadAPI.requestDownload = jest.fn().mockResolvedValue("1");
        jest.useFakeTimers();
        wrapper.instance().openProcessModal();
        jest.advanceTimersByTime(100);
        jest.useRealTimers();

        DownloadAPI.requestDownload = jest.fn().mockResolvedValue("");
        jest.useFakeTimers();
        wrapper.instance().openProcessModal();
        jest.advanceTimersByTime(100);
        jest.useRealTimers();
    });

    it('closeProcessModal, openCancelModal', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().closeProcessModal();
        wrapper.instance().openCancelModal();
    });

    it('closeCancelModal', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();

        jest.useFakeTimers();
        wrapper.instance().closeCancelModal(true);
        jest.advanceTimersByTime(100);
        jest.useRealTimers();

        wrapper.instance().closeCancelModal(false);

        const newStore = {
            ...initialStore,
            searchList: {
                get: (id) => {
                    switch (id) {
                        case "responseList":
                            return List([
                                Map({
                                    checked: true,
                                    file: true,
                                    fileDate: "20200602000000",
                                    fileId: 0,
                                    fileName: "20200602000000",
                                    filePath: "20200602000000",
                                    fileSize: 71688,
                                    fileStatus: "",
                                    keyIndex: 0,
                                    logId: "001",
                                    logName: "001_RUNNING_STATUS",
                                    sizeKB: "70.0 KB",
                                    structId: "Fab1",
                                    targetName: "MPA_1",
                                })
                            ]);
                        case "responseListCnt": return 1;
                        case "downloadCnt": return 1;
                        case "downloadStatus":
                            return Map({
                                dlId: "1",
                                downloadFiles: 1,
                                func: jest.fn(),
                                status: "done",
                                totalFiles: 1,
                            });
                        case "responsePerPage": return 10;
                        default: return jest.fn();
                    }
                }
            },
        }
        store = mockStore(newStore);
        const wrapper2 = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        services.axiosAPI.get = jest.fn().mockResolvedValue("1");
        jest.useFakeTimers();
        wrapper2.instance().closeCancelModal(true);
        jest.advanceTimersByTime(100);
        jest.useRealTimers();

        wrapper2.instance().closeCancelModal(false);
    });

    it('openCompleteModal', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().openCompleteModal();

        wrapper.setState({
            isCancelOpen: true
        });
        wrapper.instance().openCompleteModal();
    });

    it('closeCompleteModal', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        services.axiosAPI.downloadFile = jest.fn().mockResolvedValue( { result: Define.RSS_SUCCESS });
        wrapper.instance().closeCompleteModal(true);
        services.axiosAPI.downloadFile = jest.fn().mockResolvedValue( { result: Define.RSS_FAIL });
        wrapper.instance().closeCompleteModal(true);
        wrapper.instance().closeCompleteModal(false);
    });

    it('openAlertModal, closeAlertModal', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().openAlertModal();
        wrapper.instance().closeAlertModal();
    });

    it('getDownloadStatus', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().getDownloadStatus();
    });

    it('setSearchListActions', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().setSearchListActions({
            func: jest.fn(),
            dlId: "1",
            status: "done",
            totalFiles: 1,
            downloadFiles: 1
        });
    });

    it('setErrorStatus', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().setErrorStatus(Define.FILE_FAIL_NO_ITEM);
    });

    it('handlePageChange', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().handlePageChange(1);
    });

    it('onChangeRowsPerPage', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().onChangeRowsPerPage(100);

        wrapper.setState({
            currentPage: 2
        });
        wrapper.instance().onChangeRowsPerPage(100);
    });

    it('checkFileItem', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        let e;
        e = {
            target: {
                id: "20200602000000_{#div#}_0"
            },
            stopPropagation: jest.fn()
        }
        wrapper.instance().checkFileItem(e);
        e = {
            target: {
                id: "20200602000000_{#div#}"
            },
            stopPropagation: jest.fn()
        }
        wrapper.instance().checkFileItem(e);

        wrapper.instance().checkFileItem(null);
    });

    it('handleTrClick', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        let e;
        e = {
            target: {
                parentElement: {
                    getAttribute: () => "1"
                }
            },
            stopPropagation: jest.fn()
        }
        wrapper.instance().handleTrClick(e);
        e = {
            target: {
                parentElement: {
                    getAttribute: () => null
                }
            },
            stopPropagation: jest.fn()
        }
        wrapper.instance().handleTrClick(e);

        wrapper.instance().handleTrClick(null);
    });

    it('checkAllFileItem', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().checkAllFileItem(false);
        wrapper.instance().checkAllFileItem(true);
    });

    it('handleThClick', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        //wrapper.instance().handleThClick("test");

        wrapper.setState({
            sortKey: "",
            sortDirection: "asc"
        });
        wrapper.instance().handleThClick("");

        wrapper.setState({
            sortKey: "",
            sortDirection: "dsc"
        });
        wrapper.instance().handleThClick("");
    });

    it('sortIconRender', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();
        wrapper.instance().sortIconRender("test");

        wrapper.setState({
            sortKey: "",
            sortDirection: "asc"
        });
        wrapper.instance().sortIconRender("");

        wrapper.setState({
            sortKey: "",
            sortDirection: "dsc"
        });
        wrapper.instance().sortIconRender("");

        wrapper.setState({
            sortKey: "targetName",
            sortDirection: "dsc"
        });
        wrapper.instance().sortIconRender("targetName");
    });

    it.only('click', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<FileList
            dispatch={dispatch}
            store={store}
            {...initProps}
        />).dive().dive();

        wrapper.find("tr").simulate("click");

    });

});