import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import sinon from "sinon";
import moment from "moment";
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../services';

import MachineList from "../MachineList";
import { MachineCollapse } from "../MachineList";
import * as Define from "../../../define";
//import * as LogInfoListAPI from "../../../api/LogInfoList";

const initialState = {
    viewList: {
        get: (id) => {
            switch (id) {
                case "equipmentList":
                    return List([
                        Map({
                            id: "1",
                            name: "Fab1"
                        })
                    ]);

                case "toolInfoList":
                    return (List([
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

                 default : return jest.fn();
            }
        }
    }
};

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;

describe('MachineList', () => {

    /*
    beforeEach(() => {
        store = mockStore(initialState);
    });
    */

    it('renders correctly', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<MachineList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('checkAutoMachineItem ', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<MachineList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        let e;
        e = {
            target: {
                id: "test_{#div#}_1"
            }
        }
        wrapper.instance().checkAutoMachineItem(e);
    });

    it('checkAutoAllMachineItem   ', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<MachineList
            dispatch={dispatch}
            store={store}
            isNew = {true}
        />).dive().dive();
        wrapper.instance().checkAutoAllMachineItem(true);
        wrapper.instance().checkAutoAllMachineItem(false);
    });

});

describe('MachineCollapse', () => {

    /*
    beforeEach(() => {
        store = mockStore(initialState);
    });
    */

    const initProps = {
        machineList: [{
            checked: false,
            collectHostName: null,
            collectServerId: "0",
            keyIndex: 0,
            structId: "Fab1",
            targetname: "MPA_1",
            targettype: "eesp_data_CKBSTest_1.0.0",
        }],
        structId: "Fab1",
        checkItem: jest.fn()

    }

    it('renders correctly(there is matched item)', () => {
        const wrapper = shallow(<MachineCollapse {...initProps} />);
        expect(wrapper).toMatchSnapshot();
    });

    it('renders correctly(there is not matched item)', () => {
        const props = {
            ...initProps,
            structId: ""
        }
        const wrapper = shallow(<MachineCollapse {...props} />);
        expect(wrapper).toMatchSnapshot();
    });

    it('toggle', () => {
        const wrapper = shallow(<MachineCollapse {...initProps} />);
        wrapper.instance().toggle();
    });
});


