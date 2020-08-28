import 'babel-polyfill';
import React from 'react';
import configureStore from 'redux-mock-store'
import {shallow} from 'enzyme';
import {fromJS} from 'immutable';
import Manual from "../Manual";
import sinon from "sinon";

const penderSuccess = {
    success: {
        'viewList/VIEW_LOAD_TOOLINFO_LIST': true,
        'viewList/VIEW_LOAD_LOGTYPE_LIST': true,
        'genreList/GENRE_LOAD_DB_LIST': true
    },
    failure: {
        'viewList/VIEW_LOAD_TOOLINFO_LIST': false,
        'viewList/VIEW_LOAD_LOGTYPE_LIST': false,
        'genreList/GENRE_LOAD_DB_LIST': false,
    },
}

const penderFailure = {
    success: {
        'viewList/VIEW_LOAD_TOOLINFO_LIST': false,
        'viewList/VIEW_LOAD_LOGTYPE_LIST': false,
        'genreList/GENRE_LOAD_DB_LIST': false
    },
    failure: {
        'viewList/VIEW_LOAD_TOOLINFO_LIST': true,
        'viewList/VIEW_LOAD_LOGTYPE_LIST': true,
        'genreList/GENRE_LOAD_DB_LIST': true,
    },
}

const initialState = {
    viewList: {
        get: (item) => {
            switch (item) {
                case "toolInfoList":
                    return fromJS([]);
            }
            jest.fn()
        }
    },
    genreList: {
        get: () => jest.fn()
    },
    searchList: {
        get: () => jest.fn()
    },
    pender: penderSuccess
};

const mockStore = configureStore();
const dispatch = sinon.spy();
let store;

describe('Manual', () => {

    /*
    beforeEach(() => {
        store = mockStore(initialState);
    });
    */

    it('renders correctly(success)', () => {
        store = mockStore(initialState);
        const wrapper = shallow(<Manual />)
        expect(wrapper).toMatchSnapshot();
    });


    // it('renders correctly(false)', () => {
    //     store = mockStore({
    //         ...initialState,
    //         pender: penderFailure
    //     });
    //     const wrapper = shallow(<Manual
    //         dispatch={dispatch}
    //         store={store}
    //     />).dive().dive();
    //     expect(wrapper).toMatchSnapshot();
    //    // expect('network-connection-error').toExist();
    // });
});
