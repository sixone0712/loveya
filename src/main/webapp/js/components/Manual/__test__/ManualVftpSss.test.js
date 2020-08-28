import 'babel-polyfill';
import React from 'react';
import configureStore from 'redux-mock-store'
import {shallow} from 'enzyme';
import {fromJS} from 'immutable';

import sinon from "sinon";
import moment from "moment";
import ManualVftpSss from "../ManualVftpSss";

import {initialState as commandInit} from "../../../modules/command";
import {initialState as vftpSssInit} from "../../../modules/vftpSss";
import {initialState as viewListInit} from "../../../modules/viewList";
import produce from 'immer';
import services from "../../../services"

const mockStore = configureStore();
const dispatch =  sinon.spy();
let store;

const commandLists = [{
    index: 0,
    id: 1,
    cmd_name: "SSS_1-%s-%s",
    cmd_type: "vftp_sss",
    checked: false,
},{
    index: 1,
    id: 5,
    cmd_name: "SSS_2-%s-%s-Option",
    cmd_type: "vftp_sss",
    checked: false,
}, {
    index:2,
    id:6,
    cmd_name:"SSS_2-%s-%s-Option",
    cmd_type:"vftp_sss",
    checked:false,
}];

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

services.axiosAPI.requestPost = jest.fn().mockResolvedValue();
services.axiosAPI.postCancel = jest.fn().mockResolvedValue();

describe('ManualVftpSss', () => {

    it('renders with empty data', () => {
        const props = {
            command: commandInit,
            vftpSss: vftpSssInit,
            viewList: viewListInit
        }
        store = mockStore(props);
        const wrapper = shallow(<ManualVftpSss store={store} dispatch={dispatch} />)
        expect(wrapper).toMatchSnapshot();
    });

    it('renders with data', async () => {

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
        })

        const newProps = {
            command: fromJS(newCommandInit),
            vftpSss: fromJS(produce(vftpSssInit.toJS(), draft => {})),
            viewList: fromJS(newViewList),
        }

        store = mockStore(newProps);
        const wrapper = shallow(<ManualVftpSss store={store} dispatch={dispatch} />)

        const confirmfunc = wrapper.children().dive().find('RSSCommandLine').prop('confirmfunc');
        confirmfunc();

        const processfunc = wrapper.children().dive().find('RSSCommandLine').prop('processfunc');
        await processfunc();

        const cancelFunc = wrapper.children().dive().find('RSSCommandLine').prop('cancelFunc');
        await cancelFunc();
    });

    it('renders with data option', async () => {

        const newCommandLists = produce(commandLists, draft => {
            draft[1].checked = true;
        })

        const newMachines = produce(machines, draft => {
            draft[0].checked = true;
        })
        const newCommandInit = produce(commandInit.toJS(), draft => {
            draft.command.lists = newCommandLists;
        })

        const newViewList = produce(viewListInit.toJS(), draft => {
            draft.toolInfoList = newMachines;
        })

        const newProps = {
            command: fromJS(newCommandInit),
            vftpSss: fromJS(produce(vftpSssInit.toJS(), draft => {})),
            viewList: fromJS(newViewList),
        }

        store = mockStore(newProps);
        const wrapper = shallow(<ManualVftpSss store={store} dispatch={dispatch} />)

        const confirmfunc = wrapper.children().dive().find('RSSCommandLine').prop('confirmfunc');
        confirmfunc();

        const processfunc = wrapper.children().dive().find('RSSCommandLine').prop('processfunc');
        await processfunc();

        const cancelFunc = wrapper.children().dive().find('RSSCommandLine').prop('cancelFunc');
        await cancelFunc();
    });

    it('renders[checkRequest/machineNames.length === 0]',  () => {

        const newCommandLists = produce(commandLists, draft => {
            draft[0].checked = true;
        })

        const newCommandInit = produce(commandInit.toJS(), draft => {
            console.log("dratf", draft);
            draft.command.lists = newCommandLists;
        })

        const newViewList = produce(viewListInit.toJS(), draft => {
            console.log("dratf", draft);
            draft.toolInfoList = commandLists;
        })

        const newProps = {
            command: fromJS(newCommandInit),
            vftpSss: fromJS(produce(vftpSssInit.toJS(), draft => {})),
            viewList: fromJS(newViewList),
        }

        store = mockStore(newProps);
        const wrapper = shallow(<ManualVftpSss store={store} dispatch={dispatch} />)
        console.log("wrapper", wrapper.children().dive().find('RSSCommandLine').debug());

        const confirmfunc = wrapper.children().dive().find('RSSCommandLine').prop('confirmfunc');
        confirmfunc();
    });

    it('renders[checkCmd.length === 0]',  () => {

        const newMachines = produce(machines, draft => {
            draft[0].checked = true;
        })

        const newCommandInit = produce(commandInit.toJS(), draft => {
            console.log("dratf", draft);
            draft.command.lists = commandLists;
        })

        const newViewList = produce(viewListInit.toJS(), draft => {
            console.log("dratf", draft);
            draft.toolInfoList = newMachines;
        })

        const newProps = {
            command: fromJS(newCommandInit),
            vftpSss: fromJS(produce(vftpSssInit.toJS(), draft => {})),
            viewList: fromJS(newViewList),
        }

        store = mockStore(newProps);
        const wrapper = shallow(<ManualVftpSss store={store} dispatch={dispatch} />)
        console.log("wrapper", wrapper.children().dive().find('RSSCommandLine').debug());

        const confirmfunc = wrapper.children().dive().find('RSSCommandLine').prop('confirmfunc');
        confirmfunc();
    });

    it('renders[fromDate.isAfter(toDate)]',  () => {

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
        })

        const newVftpSss = produce(vftpSssInit.toJS(), draft => {
            draft.startDate = moment().endOf('day');
            draft.endDate = moment().startOf('day');
        })

        const newProps = {
            command: fromJS(newCommandInit),
            vftpSss: fromJS(newVftpSss),
            viewList: fromJS(newViewList),
        }

        store = mockStore(newProps);
        const wrapper = shallow(<ManualVftpSss store={store} dispatch={dispatch} />)
        console.log("wrapper", wrapper.children().dive().find('RSSCommandLine').debug());

        const confirmfunc = wrapper.children().dive().find('RSSCommandLine').prop('confirmfunc');
        confirmfunc();
    });
});
