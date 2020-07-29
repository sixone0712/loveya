import 'babel-polyfill';
import React from 'react';
import renderer from 'react-test-renderer'
import configureMockStore from 'redux-mock-store'
import configureStore from 'redux-mock-store'
import { shallow, mount } from 'enzyme';
import {createStore} from 'redux';
import {Provider} from 'react-redux';
import { Map, List, fromJS, Record } from 'immutable';
import MoveRefreshPage from "../MoveRefreshPage";
import sinon from "sinon";

import * as Define from '../../../define';
import MachineList from "../../Auto/MachineList";

const initialStore = {
    viewList: {
        get: jest.fn()
    },
    autoPlan: {
        get: jest.fn()
    },
}
const mockStore = configureStore();
const dispatch = sinon.spy();
let store;
const initProps = {
    location: {
        search: `?target=${Define.PAGE_AUTO_PLAN_EDIT}`
    },
    history: {
        replace: jest.fn()
    }
}

describe('PAGE_AUTO_PLAN_EDIT', () => {
    it('renders correctly', () => {
        store = mockStore(initialStore);
        const wrapper = shallow(<MoveRefreshPage
            dispatch={dispatch}
            store={store}
            isNew = {true}
            {...initProps}
        />).dive().dive();
    });

    it('PAGE_AUTO_PLAN_ADD', () => {
        store = mockStore(initialStore);
        const newProps = {
            ...initProps,
            location: {
                search: `?target=${Define.PAGE_AUTO_PLAN_ADD}`
            }
        }
        const wrapper = shallow(<MoveRefreshPage
            dispatch={dispatch}
            store={store}
            {...newProps}
        />).dive().dive();
    });

    it('PAGE_MANUAL', () => {
        store = mockStore(initialStore);
        const newProps = {
            ...initProps,
            location: {
                search: `?target=${Define.PAGE_MANUAL_FTP}`
            }
        }
        const wrapper = shallow(<MoveRefreshPage
            dispatch={dispatch}
            store={store}
            {...newProps}
        />).dive().dive();
    });

    it('PAGE_AUTO_STATUS', () => {
        store = mockStore(initialStore);
        const newProps = {
            ...initProps,
            location: {
                search: `?target=${Define.PAGE_AUTO_STATUS}`
            }
        }
        const wrapper = shallow(<MoveRefreshPage
            dispatch={dispatch}
            store={store}
            {...newProps}
        />).dive().dive();
    });

    it('exception', () => {
        store = mockStore(initialStore);
        const newProps = {
            ...initProps,
            location: {
                search: `?target=`
            }
        }
        const wrapper = shallow(<MoveRefreshPage
            dispatch={dispatch}
            store={store}
            {...newProps}
        />).dive().dive();
    });
});
