import {applyMiddleware, compose, createStore} from 'redux';
import modules from './modules';
import {createLogger} from "redux-logger";
import ReduxThunk from "redux-thunk";
import penderMiddleware from 'redux-pender';

const logger = createLogger();
const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const store = process.env.NODE_ENV === "production"
	? createStore(modules, /* preloadedState, */ applyMiddleware(ReduxThunk, penderMiddleware()))
	: createStore(modules, /* preloadedState, */ composeEnhancers(applyMiddleware(logger, ReduxThunk, penderMiddleware())));

export default store;