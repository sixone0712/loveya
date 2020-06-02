import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import EquipmentCollapse from "../EquipmentCollapse";
import sinon from "sinon";
import moment from "moment";
import * as Define from "../../../../define";

import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import services from '../../../../services';

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;
let props = {
    machineList: [{
        checked: false,
        collectHostName: null,
        collectServerId: "0",
        keyIndex: 0,
        structId: "Fab1",
        targetname: "MPA_1",
        targettype: "eesp_data_CKBSTest_1.0.0",
    }],
    checkMachineItem: jest.fn(),
    structId: "Fab1"
};

describe('EquipmentCollapse', () => {

    /*
    beforeEach(() => {
    });
     */

    it('renders correctly(matched StructId)', () => {
        const wrapper = shallow(<EquipmentCollapse {...props} />)
        expect(wrapper).toMatchSnapshot();
    });

    it('renders correctly(not matched StructId)', () => {
        const newProps = {
            ...props,
            structId: "Fab2"
        }
        const wrapper = shallow(<EquipmentCollapse {...newProps} />)
        expect(wrapper).toMatchSnapshot();
    });

    it('toggle', () => {
        const wrapper = shallow(<EquipmentCollapse {...props} />)
        wrapper.instance().toggle();
    });
});