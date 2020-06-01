import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import Auto from "../Auto";
import { CreateBreadCrumb } from '../Auto';
import sinon from "sinon";

import * as Define from '../../../define';

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;
const initProps = {
    location: {
        pathname: Define.PAGE_AUTO_PLAN_ADD
    }
}

describe('Auto', () => {

    let props;

    beforeEach(() => {
        props = initProps;
    });

    it('renders when page is Define.PAGE_AUTO_PLAN_ADD', () => {
        const wrapper = shallow(<Auto {...props}/>);
        expect(wrapper).toMatchSnapshot();
    });

    it('renders when page is Define.AUTO_CUR_PAGE_STATUS', () => {
        props.location.pathname = Define.PAGE_AUTO_STATUS;
        const wrapper = shallow(<Auto {...props}/>);
        expect(wrapper).toMatchSnapshot();
    });

    it('renders when page is Define.PAGE_AUTO_DOWNLOAD', () => {
        props.location.pathname = Define.PAGE_AUTO_DOWNLOAD;
        const wrapper = shallow(<Auto {...props}/>);
        expect(wrapper).toMatchSnapshot();
    });

    it('renders when page is Define.PAGE_AUTO_PLAN_EDIT', () => {
        props.location.pathname = Define.PAGE_AUTO_PLAN_EDIT;
        const wrapper = shallow(<Auto {...props}/>);
        expect(wrapper).toMatchSnapshot();
    });

    it('call func CreateBreadCrumb', () => {
        CreateBreadCrumb({page: Define.AUTO_CUR_PAGE_ADD});
        CreateBreadCrumb({page: Define.AUTO_CUR_PAGE_STATUS});
        CreateBreadCrumb({page: Define.AUTO_CUR_PAGE_EDIT});
        CreateBreadCrumb({page: Define.AUTO_CUR_PAGE_DOWNLOAD});
        CreateBreadCrumb({page: 5});
    })
});
