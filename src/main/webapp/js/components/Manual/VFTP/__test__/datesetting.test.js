import 'babel-polyfill';
import React from 'react';
import configureStore from 'redux-mock-store'
import {shallow} from 'enzyme';

import sinon from "sinon";
import RSSdatesettings, {CreateDatetimePicker} from "../datesetting";
import moment from "moment";


const mockStore = configureStore();
const dispatch =  sinon.spy();
let store;

describe('deatesetting', () => {

    afterEach(() => {
        jest.restoreAllMocks();
    });

    it('renders', async () => {
        const props = {
            from: moment().startOf('day'),
            FromDateChangehandler: jest.fn(),
            to: moment().endOf('day'),
            ToDateChangehandler: jest.fn()
        }
        const wrapper = shallow(<RSSdatesettings { ...props } />);

        let funcExec = wrapper.find("CreateDatetimePicker").at(0).prop("handleChange");
        funcExec();
        funcExec = wrapper.find("CreateDatetimePicker").at(1).prop("handleChange");
        funcExec();

        const wrapperDatePicker = shallow(<CreateDatetimePicker label={"test"} date={moment()} handleChange={jest.fn()} />);

    });
});