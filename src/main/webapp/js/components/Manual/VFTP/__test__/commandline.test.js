import 'babel-polyfill';
import React from 'react';
import configureStore from 'redux-mock-store'
import {shallow} from 'enzyme';

import sinon from "sinon";
import RSSCommandLine from "../commandline";

import produce from 'immer';
import * as Define from "../../../../define";

const mockStore = configureStore();
const dispatch =  sinon.spy();
let store;

const vftpSssProps = {
    type: "sss/optional",
    string: "SSS_1-20200826000000_20200826235959",
    confirmfunc: jest.fn().mockReturnValue(Define.RSS_SUCCESS),
    processfunc: jest.fn().mockResolvedValue(Define.RSS_SUCCESS),
    completeFunc: jest.fn().mockResolvedValue(Define.RSS_SUCCESS),
    cancelFunc: jest.fn().mockResolvedValue(),
    modalMsglist: {
        cancel: "Are you sure want to cancel the search?",
        process: "Searching.....",
        confirm: "Do you want to execute the command?",
        complete: "Search was canceled.",
        ready:"",
    }
};

const vftpCompatProps = {
    type: "compat/optional",
    string: "20200826000000_20200826235959-COMPAT_1",
    confirmfunc: jest.fn().mockResolvedValue({
        error: Define.RSS_SUCCESS,
    }),
    processfunc: jest.fn().mockResolvedValue({
        error:Define.RSS_SUCCESS,
        msg:"",
        status:"done",
        url:"",
        totalFiles:"",
        downloadedFiles:"",
    }),
    completeFunc: jest.fn().mockResolvedValue({
        result: Define.RSS_SUCCESS,
        error: ""
    }),
    cancelFunc: jest.fn().mockResolvedValue(),
    modalMsglist: {
        cancel: "Are you sure want to cancel the download?",
        process: "downloading.....",
        confirm: "Do you want to execute the command?",
        complete: "Download Complete!",
        ready:"",
    }
};


describe('commandline', () => {

    afterEach(() => {
        jest.restoreAllMocks();
    });

    it('renders VftpCompat(Success)', async () => {
        //store = mockStore(props);
        const wrapper =
            shallow(<RSSCommandLine
                {...vftpCompatProps}
            />)

        expect(wrapper).toMatchSnapshot();
        const CardHeader = wrapper.find('CardHeader').find('Button').prop('onClick');
        CardHeader();

        let ConfirmModal;
        ConfirmModal = wrapper.find('ConfirmModal').at(0).prop('actionBg');
        ConfirmModal();

        jest.useFakeTimers();
        ConfirmModal = wrapper.find('ConfirmModal').at(0).prop('actionLeft');
        await ConfirmModal();
        jest.advanceTimersByTime(1500);
        jest.useRealTimers();

        ConfirmModal = wrapper.find('ConfirmModal').at(0).prop('actionRight');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(1).prop('actionLeft');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(1).prop('actionRight');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(2).prop('actionLeft');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(2).prop('actionRight');
        await ConfirmModal();


        //processSequnce
        let newProps = produce(vftpCompatProps, draft => {
            draft.processfunc = jest.fn().mockResolvedValue({
                error:Define.RSS_SUCCESS,
                msg:"",
                status:"error",
                url:"",
                totalFiles:"",
                downloadedFiles:"",
            });
        })
        wrapper.setProps({...newProps});
        jest.useFakeTimers();
        ConfirmModal = wrapper.find('ConfirmModal').at(0).prop('actionLeft');
        await ConfirmModal();
        jest.advanceTimersByTime(1500);
        jest.useRealTimers();

        newProps = produce(vftpCompatProps, draft => {
            draft.processfunc = jest.fn().mockResolvedValue({
                error:Define.RSS_SUCCESS,
                msg:"",
                status:"in-progress",
                url:"",
                totalFiles:"",
                downloadedFiles:"",
            });
        })
        wrapper.setProps({...newProps});
        jest.useFakeTimers();
        ConfirmModal = wrapper.find('ConfirmModal').at(0).prop('actionLeft');
        await ConfirmModal();
        jest.advanceTimersByTime(1500);
        jest.useRealTimers();
    });

    it('renders VftpCompat(Fail)', async () => {
        const newVftpCompatProps = produce(vftpCompatProps, draft => {
            draft.confirmfunc = jest.fn().mockResolvedValue({
                error: Define.RSS_FAIL,
            });
            draft.processfunc = jest.fn().mockResolvedValue({
                error:Define.RSS_FAIL,
                msg:"",
                status:"error",
                url:"",
                totalFiles:"",
                downloadedFiles:"",
            });
            draft.completeFunc = jest.fn().mockResolvedValue({
                result: Define.RSS_FAIL,
            });
            draft.cancelFunc = jest.fn().mockResolvedValue();
        })

        const wrapper =
            shallow(<RSSCommandLine
                {...newVftpCompatProps}
            />)
        expect(wrapper).toMatchSnapshot();

        const CardHeader = wrapper.find('CardHeader').find('Button').prop('onClick');
        CardHeader();

        let ConfirmModal;
        ConfirmModal = wrapper.find('ConfirmModal').at(0).prop('actionBg');
        ConfirmModal();

        jest.useFakeTimers();
        ConfirmModal = wrapper.find('ConfirmModal').at(0).prop('actionLeft');
        await ConfirmModal();
        jest.advanceTimersByTime(1500);
        jest.useRealTimers();

        ConfirmModal = wrapper.find('ConfirmModal').at(1).prop('actionLeft');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(1).prop('actionRight');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(2).prop('actionLeft');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(2).prop('actionRight');
        await ConfirmModal();
    });

    it('renders VftpSss(Success)', async () => {
        //store = mockStore(props);
        const wrapper = shallow(<RSSCommandLine {...vftpSssProps} />)

        expect(wrapper).toMatchSnapshot();
        const CardHeader = wrapper.find('CardHeader').find('Button').prop('onClick');
        CardHeader();

        let ConfirmModal;
        ConfirmModal = wrapper.find('ConfirmModal').at(0).prop('actionBg');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(0).prop('actionLeft');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(0).prop('actionRight');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(1).prop('actionLeft');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(1).prop('actionRight');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(2).prop('actionLeft');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(2).prop('actionRight');
        await ConfirmModal();
    });

    it('renders VftpSss(Fail)', async () => {
        //store = mockStore(props);
        const newVftpCompatProps = produce(vftpCompatProps, draft => {
            draft.confirmfunc = jest.fn().mockReturnValue(Define.RSS_FAIL);
            draft.processfunc = jest.fn().mockResolvedValue(Define.RSS_FAIL);
            draft.completeFunc = jest.fn().mockResolvedValue(Define.RSS_FAIL);
        });
        const wrapper = shallow(<RSSCommandLine {...newVftpCompatProps} />)

        expect(wrapper).toMatchSnapshot();
        const CardHeader = wrapper.find('CardHeader').find('Button').prop('onClick');
        CardHeader();

        let ConfirmModal;
        ConfirmModal = wrapper.find('ConfirmModal').at(0).prop('actionBg');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(0).prop('actionLeft');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(0).prop('actionRight');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(1).prop('actionLeft');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(1).prop('actionRight');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(2).prop('actionLeft');
        await ConfirmModal();

        ConfirmModal = wrapper.find('ConfirmModal').at(2).prop('actionRight');
        await ConfirmModal();
    });
});