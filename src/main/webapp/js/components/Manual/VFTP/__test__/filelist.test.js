import 'babel-polyfill';
import React from 'react';
import configureStore from 'redux-mock-store'
import {shallow} from 'enzyme';

import sinon from "sinon";
import RSSvftpFilelist, {CreateFileList, CreateModal} from "../filelist";
import services from "../../../../services";

import produce from 'immer';

import {initialState as vftpsssInit} from "../../../../modules/vftpSss";
import {fromJS} from "immutable";

const mockStore = configureStore();
const dispatch =  sinon.spy();
let store;

const responseList = [{
    fileType: "F",
    fileName: "111.PNG",
    fileSize: 126905,
    fabName: "Fab1",
    machineName: "MPA_1",
    command: "SSS_1-20200827_000000-20200827_235959",
    index: 0,
    checked: true,
}, {
    fileType: "F",
    fileName: "mirroring_.png",
    fileSize: 253399,
    fabName: "Fab1",
    machineName: "MPA_1",
    command: "SSS_1-20200827_000000-20200827_235959",
    index: 1,
    checked: true
}];

const downloadStatus = {
    func:null,
    dlId:"",
    status:"init",
    totalFiles:0,
    downloadFiles:0,
    downloadUrl:""
}

const statusDone = {
    downloadId: "1234",
    status: "done",
    url: "",
    totalFiles: "",
    downloadedFiles: "",
}

const statusError = {
    downloadId: "1234",
    status: "error",
    url: "",
    totalFiles: "",
    downloadedFiles: "",
}


describe('filelist', () => {

    afterEach(() => {
        jest.restoreAllMocks();
    });

    it('renders[no list]',  () => {
        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = [];
                draft.responseListCnt = 0;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }

        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch} testDownStatus={statusDone}/>)
        wrapper.children().dive();
    })

    it('renders[CreateModal 1]', async () => {

        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }

        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch} />)
        console.log("wrapper", wrapper.children().dive().debug());
        //console.log("wrapper", wrapper.children().dive().find("Memo()").debug());

        const CreateModal = wrapper.children().dive().find("Memo()").at(0).props();
        CreateModal.confirmClose();


        services.axiosAPI.requestPost = jest.fn().mockResolvedValue({
            status: 200,
            data: {
                downloadId: "1234"
            },
        });
        services.axiosAPI.requestGet = jest.fn().mockResolvedValue({
            status: 200,
            data: {
                downloadId: "1234",
                status:"done",
                url:"",
                totalFiles:"",
                downloadedFiles:"",
            }
        });
        jest.useFakeTimers();
        await CreateModal.confirmAction();
        jest.advanceTimersByTime(1000);
        jest.useRealTimers();


        services.axiosAPI.requestPost = jest.fn().mockResolvedValue({
            status: 200,
            data: {
                downloadId: "1234"
            },
        });
        services.axiosAPI.requestGet = jest.fn().mockResolvedValue({
            status: 200,
            data: {
                downloadId: "1234",
                status:"error",
                url:"",
                totalFiles:"",
                downloadedFiles:"",
            }
        });
        jest.useFakeTimers();
        await CreateModal.confirmAction();
        jest.advanceTimersByTime(1000);
        jest.useRealTimers();

        services.axiosAPI.requestPost = jest.fn().mockResolvedValue({
            status: 200,
            data: {
                downloadId: "1234"
            },
        });
        services.axiosAPI.requestGet = jest.fn().mockResolvedValue({
            status: 500,
            data: {
                downloadId: "1234",
                status:"error",
                url:"",
                totalFiles:"",
                downloadedFiles:"",
            }
        });
        jest.useFakeTimers();
        await CreateModal.confirmAction();
        jest.advanceTimersByTime(1000);
        jest.useRealTimers();
    })

    it('renders[CreateModal 1 - error]', async () => {

        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }

        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch}/>)
        const CreateModal = wrapper.children().dive().find("Memo()").at(0).props();

        services.axiosAPI.requestPost = jest.fn().mockRejectedValue({
            status: 200,
            data: {
                downloadId: "1234"
            }
        });
        services.axiosAPI.requestGet = jest.fn().mockReturnValue({
            status: 200,
            data: {
                downloadId: "1234",
                status:"error",
                url:"",
                totalFiles:"",
                downloadedFiles:"",

            }
        });
        jest.useFakeTimers();
        await CreateModal.confirmAction();
        jest.advanceTimersByTime(1000);
        jest.useRealTimers();
    })

    it('renders[CreateModal 2]', async () => {

        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }

        services.axiosAPI.requestPost = jest.fn().mockResolvedValue();

        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch} testDownStatus={statusDone}/>)
        const CreateModal = wrapper.children().dive().find("Memo()").at(0).props();

        jest.useFakeTimers();
        CreateModal.cancelClose("Cancel");
        jest.advanceTimersByTime(500);
        jest.useRealTimers();

        jest.useFakeTimers();
        CreateModal.cancelClose("OK");
        jest.advanceTimersByTime(500);
        jest.useRealTimers();
    })

    it('renders[CreateModal 3]', async () => {

        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }

        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch} testDownStatus={statusError}/>)
        const CreateModal = wrapper.children().dive().find("Memo()").at(0).props();

        jest.useFakeTimers();
        CreateModal.cancelClose("Cancel");
        jest.advanceTimersByTime(500);
        jest.useRealTimers();

    })

    it('renders[CreateModal 4]', async () => {

        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }

        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch} testDownStatus={statusError}/>)
        const CreateModal = wrapper.children().dive().find("Memo()").at(0).props();

        services.axiosAPI.requestPost = jest.fn().mockResolvedValue();

        services.axiosAPI.downloadFile = jest.fn().mockResolvedValue({
            fileName: "test"
        });
        CreateModal.completeClose(true);
        CreateModal.completeClose(false);

        CreateModal.errorClose();

        jest.useFakeTimers();
        CreateModal.confirmAction();
        jest.advanceTimersByTime(500);
        jest.useRealTimers();

        jest.useFakeTimers();
        CreateModal.startAction();
        jest.advanceTimersByTime(500);
        jest.useRealTimers();
    })


    it('renders[CardBody]',  () => {
        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }

        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch} />)

        wrapper.children().dive().find("CardBody").find("ButtonToggle").simulate('click');
        //wrapper.children().dive().find("CardBody").find("th").at(0).simulate('click');
        wrapper.children().dive().find("CardBody").find("th").at(1).simulate('click');
        wrapper.children().dive().find("CardBody").find("th").at(2).simulate('click');
        wrapper.children().dive().find("CardBody").find("th").at(3).simulate('click');
    })

    it('renders[CreateFileList]',  () => {
        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }

        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch} />)

        const value1 = {
            target: {
                parentElement: {
                    getAttribute: () => 0
                }
            },
            stopPropagation: () => {}
        };
        wrapper.children().dive().find("Memo()").at(1).props().trClick(value1);

        const value2 = {
            target: {
                id: 0
            },
            stopPropagation: () => {}
        };
        wrapper.children().dive().find("Memo()").at(1).props().checkboxClick(value2);
    })

    it('renders[RenderPagination]',  () => {
        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }

        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch}/>)
        wrapper.children().dive().find("RenderPagination").props().onPageChange(0);
    })

    it('renders[Select]',  () => {
        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }

        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch}/>)
        wrapper.children().dive().find("Select").props().onChange(10);
    })

    it('renders[Button 1]',  () => {
        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }

        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch}/>)
        wrapper.children().dive().find("Button").simulate('click');
    })

    it('renders[Button 2]',  () => {
        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt =1;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }

        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch}/>)
        wrapper.children().dive().find("Button").simulate('click');
    })

    it('renders[isDownloadConfirm : isDownloadConfirm]',  () => {
        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }
        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch} testIsDownloadConfirm={true}/>)
        wrapper.children().dive().find("Memo()").at(0).dive();
    });

    it('renders[isDownloadConfirm : isDownloadStart]',  () => {
        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }
        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch} testIsDownloadStart={true} testDownStatus={statusDone}/>)
        wrapper.children().dive().find("Memo()").at(0).dive();
    });

    it('renders[isDownloadConfirm : isDownloadCancel]',  () => {
        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }
        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch} testIsDownloadCancel={true} testDownStatus={statusDone}/>)
        wrapper.children().dive().find("Memo()").at(0).dive();
        wrapper.children().dive().find("Memo()").at(0).dive().find("button").at(0).simulate('click');
        wrapper.children().dive().find("Memo()").at(0).dive().find("button").at(1).simulate('click');
    });

    it('renders[isDownloadConfirm : isDownloadComplete]',  () => {
        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }
        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch} testIsDownloadComplete={true}/>)
        wrapper.children().dive().find("Memo()").at(0).dive();
    });

    it('renders[isDownloadConfirm : isDownloadError]',  () => {
        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }
        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch} testIsDownloadError={true}/>)
        wrapper.children().dive().find("Memo()").at(0).dive();
    });

    it('renders[isDownloadConfirm : none]',  () => {
        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }
        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch} />)
        wrapper.children().dive().find("Memo()").at(0).dive();
    });

    it('renders[CreateFileList]',  () => {
        const newProps = () => {
            const newVftpSssInit = produce(vftpsssInit.toJS(), draft => {
                draft.responseList = responseList;
                draft.responseListCnt = 2;
                draft.downloadCnt = 0;
                draft.downloadAll = true;
                draft.isNewResponseList = true;
            })
            return ({
                vftpSss: fromJS(newVftpSssInit)
            })
        }
        store = mockStore(newProps);
        const wrapper = shallow(<RSSvftpFilelist store={store} dispatch={dispatch} />)
        wrapper.children().dive().find("Memo()").at(1).dive();
    });



});