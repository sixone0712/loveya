import 'babel-polyfill';
import React from 'react';
import configureStore from 'redux-mock-store'
import {shallow} from 'enzyme';
import {fromJS} from 'immutable';

import sinon from "sinon";
import moment from "moment";
import ManualVftpCompat from "../ManualVftpCompat";

import {initialState as commandInit} from "../../../modules/command";
import {initialState as vftpCompatInit} from "../../../modules/vftpCompat";
import {initialState as viewListInit} from "../../../modules/viewList";
import produce from 'immer';
import services from "../../../services"
import * as Define from "../../../define";

const mockStore = configureStore();
const dispatch =  sinon.spy();
let store;

const commandLists = [
    {
        index: 0,
        id: 1,
        cmd_name: "%s-%s-COMPAT_1",
        cmd_type: "vftp_compat",
        checked: false,
    },
    {
        index: 1,
        id: 5,
        cmd_name: "%s-%s-COMPAT_2",
        cmd_type: "vftp_compat",
        checked: false,
    },
    {
        index:2,
        id:6,
        cmd_name: "%s-%s-COMPAT_3",
        cmd_type:"vftp_compat",
        checked:false,
    }
];

const machines = [{
    keyIndex: 0,
    structId: "Fab1",
    targetname: "MPA_1",
    checked: false,
}, {
    keyIndex: 1,
    structId: "Fab1",
    targetname: "MPA_2",
    checked: false,
}, {
    keyIndex:2,
    structId:"Fab2",
    targetname:"MPA_3",
    checked:false,
}]

describe('ManualVftpCompat', () => {
    it('renders with empty data', () => {
        const props = {
            command: commandInit,
            vftpCompat: vftpCompatInit,
            viewList: viewListInit
        }
        store = mockStore(props);
        const wrapper = shallow(<ManualVftpCompat store={store} dispatch={dispatch} />)
        expect(wrapper).toMatchSnapshot();
    });

    it('renders with downloadFunc', async () => {
        let newProps = (() => {
            const newCommandLists = produce(commandLists, draft => {
                draft[0].checked = true;
            })

            const newMachines = produce(machines, draft => {
                draft[0].checked = true;
            })
            const newCommandInit = produce(commandInit.toJS(), draft => {
                draft.command.lists = newCommandLists;
            })

            const newViewList = produce(viewListInit.toJS(), draft => {
                draft.toolInfoList = newMachines;
                draft.toolInfoListCheckCnt = 1;
            })

            return ({
                command: fromJS(newCommandInit),
                vftpCompat: fromJS(produce(vftpCompatInit.toJS(), draft => {
                })),
                viewList: fromJS(newViewList),
            })
        })();

        store = mockStore(newProps);
        let wrapper = shallow(<ManualVftpCompat store={store} dispatch={dispatch}/>)

        let funcExec = wrapper.children().dive().find('RSSCommandLine').prop('confirmfunc');
        services.axiosAPI.requestPost = jest.fn().mockResolvedValue({
            data: {
                downloadId: "downloadId_1"
            }
        });
        funcExec(store);
    })

    it('renders with downloadFunc machine error', async () => {
        const newProps = (() => {
            const newCommandLists = produce(commandLists, draft => {
                draft[0].checked = true;
            })

            const newCommandInit = produce(commandInit.toJS(), draft => {
                draft.command.lists = newCommandLists;
            })

            const newViewList = produce(viewListInit.toJS(), draft => {
                draft.toolInfoList = machines;
                draft.toolInfoListCheckCnt = 0;
            })

            return ({
                command: fromJS(newCommandInit),
                vftpCompat: fromJS(produce(vftpCompatInit.toJS(), draft => {})),
                viewList: fromJS(newViewList),
            })
        })();

        store = mockStore(newProps);
        let wrapper = shallow(<ManualVftpCompat store={store} dispatch={dispatch}/>)

        let funcExec = wrapper.children().dive().find('RSSCommandLine').prop('confirmfunc');
        services.axiosAPI.requestPost = jest.fn().mockResolvedValue({
            data: {
                downloadId: "downloadId_1"
            }
        });
        funcExec(store);
    })

    it('renders with downloadFunc date error', async () => {
        const newProps = (() => {
            const newCommandLists = produce(commandLists, draft => {
                draft[0].checked = true;
            })

            const newCommandInit = produce(commandInit.toJS(), draft => {
                draft.command.lists = newCommandLists;
            })

            const newMachines = produce(machines, draft => {
                draft[0].checked = true;
            })

            const newViewList = produce(viewListInit.toJS(), draft => {
                draft.toolInfoList = newMachines;
                draft.toolInfoListCheckCnt = 1;
            })

            const newVftpCompat = produce(vftpCompatInit.toJS(), draft => {
                draft.startDate = moment().endOf('day');
                draft.endDate = moment().startOf('day');
            })

            return ({
                command: fromJS(newCommandInit),
                vftpCompat: fromJS(newVftpCompat),
                viewList: fromJS(newViewList),
            })
        })();

        store = mockStore(newProps);
        let wrapper = shallow(<ManualVftpCompat store={store} dispatch={dispatch}/>)

        let funcExec = wrapper.children().dive().find('RSSCommandLine').prop('confirmfunc');
        services.axiosAPI.requestPost = jest.fn().mockResolvedValue({
            data: {
                downloadId: "downloadId_1"
            }
        });
        funcExec(store);
    })

    it('renders with downloadFunc downloadId is null', async () => {
        const newProps = (() => {
            const newCommandLists = produce(commandLists, draft => {
                draft[0].checked = true;
            })

            const newCommandInit = produce(commandInit.toJS(), draft => {
                draft.command.lists = newCommandLists;
            })

            const newMachines = produce(machines, draft => {
                draft[0].checked = true;
            })

            const newViewList = produce(viewListInit.toJS(), draft => {
                draft.toolInfoList = newMachines;
                draft.toolInfoListCheckCnt = 1;
            })

            return ({
                command: fromJS(newCommandInit),
                vftpCompat: fromJS(produce(vftpCompatInit.toJS(), draft => {})),
                viewList: fromJS(newViewList),
            })
        })();

        store = mockStore(newProps);
        let wrapper = shallow(<ManualVftpCompat store={store} dispatch={dispatch}/>)

        let funcExec = wrapper.children().dive().find('RSSCommandLine').prop('confirmfunc');
        services.axiosAPI.requestPost = jest.fn().mockResolvedValue({
            data: {
                downloadId: ""
            }
        });
        funcExec(store);
    })

    it('renders with statusCheckFunc done', async () => {
        store = mockStore({
            command: fromJS(commandInit),
            vftpCompat: fromJS(vftpCompatInit),
            viewList: fromJS(viewListInit)
        });
        let wrapper = shallow(<ManualVftpCompat store={store} dispatch={dispatch}/>)
        let funcExec = wrapper.children().dive().find('RSSCommandLine').prop('processfunc');
        services.axiosAPI.requestGet = jest.fn().mockResolvedValue({
            data: {
                status:"done",
                url:"",
                totalFiles: 100,
                downloadedFiles: 20
            }
        });
        await funcExec(store);
    });

    it('renders with statusCheckFunc error', async () => {
        store = mockStore({
            command: fromJS(commandInit),
            vftpCompat: fromJS(vftpCompatInit),
            viewList: fromJS(viewListInit)
        });
        let wrapper = shallow(<ManualVftpCompat store={store} dispatch={dispatch}/>)
        let funcExec = wrapper.children().dive().find('RSSCommandLine').prop('processfunc');
        services.axiosAPI.requestGet = jest.fn().mockResolvedValue({
            data: {
                status:"error",
                url:"",
                totalFiles: 100,
                downloadedFiles: 20
            }
        });
        await funcExec(store);
    });

    it('renders with statusCheckFunc in-progress', async () => {
        store = mockStore({
            command: fromJS(commandInit),
            vftpCompat: fromJS(vftpCompatInit),
            viewList: fromJS(viewListInit)
        });
        let wrapper = shallow(<ManualVftpCompat store={store} dispatch={dispatch}/>)
        let funcExec = wrapper.children().dive().find('RSSCommandLine').prop('processfunc');
        services.axiosAPI.requestGet = jest.fn().mockResolvedValue({
            data: {
                status:"in-progress",
                url:"",
                totalFiles: 100,
                downloadedFiles: 20
            }
        });
        await funcExec(store);
    });

    it('renders with completeFunc success', async () => {
        store = mockStore({
            command: fromJS(commandInit),
            vftpCompat: fromJS(vftpCompatInit),
            viewList: fromJS(viewListInit)
        });
        let wrapper = shallow(<ManualVftpCompat store={store} dispatch={dispatch}/>)
        let funcExec = wrapper.children().dive().find('RSSCommandLine').prop('completeFunc');
        services.axiosAPI.downloadFile = jest.fn().mockResolvedValue({
            result: Define.RSS_SUCCESS
        });
        await funcExec(store);
    });

    it('renders with completeFunc fail', async () => {
        store = mockStore({
            command: fromJS(commandInit),
            vftpCompat: fromJS(vftpCompatInit),
            viewList: fromJS(viewListInit)
        });
        let wrapper = shallow(<ManualVftpCompat store={store} dispatch={dispatch}/>)
        let funcExec = wrapper.children().dive().find('RSSCommandLine').prop('completeFunc');
        services.axiosAPI.downloadFile = jest.fn().mockResolvedValue({
            result: Define.RSS_FAIL
        });

        await funcExec(store);
    });

    it('renders with cancelFunc downloadStatus.dlId !== 0', async () => {
        const newProps = (() => {
            const newVftpCompat = produce(vftpCompatInit.toJS(), draft => {
                draft.downloadStatus = {
                    func: null,
                    dlId: "",
                    status: "",
                    totalFiles: 0,
                    downloadFiles: 0,
                    downloadUrl: ""
                };
            })

            return ({
                command: fromJS(produce(commandInit.toJS(), draft => {})),
                vftpCompat: fromJS(newVftpCompat),
                viewList: fromJS(produce(viewListInit.toJS(), draft => {}))
            })
        })();

        store = mockStore(newProps);
        let wrapper = shallow(<ManualVftpCompat store={store} dispatch={dispatch} />)
        let funcExec = wrapper.children().dive().find('RSSCommandLine').prop('cancelFunc');
        services.axiosAPI.requestDelete = jest.fn().mockResolvedValue();
        await funcExec(store);
    });

    it('renders with FromDateChangehandler', () => {
        store = mockStore({
            command: commandInit,
            vftpCompat: vftpCompatInit,
            viewList: viewListInit
        });

        let wrapper = shallow(<ManualVftpCompat store={store} dispatch={dispatch}/>)
        console.log(wrapper.children().dive().debug());
        let funcExec = wrapper.children().dive().find('RSSdatesettings').prop('FromDateChangehandler');
        funcExec(store);
    });

    it('renders with ToDateChangehandler', () => {
        store = mockStore({
            command: fromJS(commandInit),
            vftpCompat: fromJS(vftpCompatInit),
            viewList: fromJS(viewListInit)
        });

        let wrapper = shallow(<ManualVftpCompat store={store} dispatch={dispatch}/>)
        let funcExec = wrapper.children().dive().find('RSSdatesettings').prop('ToDateChangehandler');
        funcExec(store);
    });
});
