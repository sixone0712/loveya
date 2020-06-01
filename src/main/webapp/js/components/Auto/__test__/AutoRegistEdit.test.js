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

import AutoRegistEdit from "../AutoRegistEdit";
import * as Define from "../../../define";
//import * as LogInfoListAPI from "../../../api/LogInfoList";

describe('AutoRegistEdit', () => {

    /*
    beforeEach(() => {
        store = mockStore(initialState);
    });
    */

    it('renders correctly(editId exists)', () => {
        const props = {
            location: {
                search: Define.PAGE_REFRESH_AUTO_PLAN_EDIT + "&editId=1"
            }
        }
        const wrapper = shallow(<AutoRegistEdit {...props} />)
        expect(wrapper).toMatchSnapshot();
    });

    it('renders correctly(editId does not exists)', () => {
        const props = {
            location: {
                search: Define.PAGE_REFRESH_AUTO_PLAN_EDIT
            }
        }
        const wrapper = shallow(<AutoRegistEdit {...props} />)
        expect(wrapper).toMatchSnapshot();
    });
});
