import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import MachineList from "../MachineList";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../../define";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../../services';

const initialStore= {
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
            }
        }
    },
};

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;
let props;

describe('MachineList', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<MachineList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        expect(wrapper).toMatchSnapshot();
    });

    it('ItemsChecked is true', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<MachineList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.setState({ ItemsChecked: true });
    });

    it('checkMachineItem', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<MachineList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        const e = {
            target: {
                id: "MPA_1_{#div#}_1"
            }
        }
        wrapper.instance().checkMachineItem(e);
    });

    it('checkAllMachineItem', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<MachineList
            dispatch={dispatch}
            store={store}
            {...props}
        />).dive().dive();
        wrapper.instance().checkAllMachineItem(true);
        wrapper.instance().checkAllMachineItem(false);
    });
});